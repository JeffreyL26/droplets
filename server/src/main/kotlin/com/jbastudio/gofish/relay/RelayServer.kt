package com.jbastudio.gofish.relay

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.time.Duration

/**
 * Go Fish! — Matchmaking-/Relay-Server (2–4 Spieler).
 *
 * Protokoll (RELAY_PROTOCOL.md):
 *  1. Client → Relay: {"type":"FIND","size":N}
 *       - N = 2..4  → fester Raum dieser Größe.
 *       - N = 0      → „Beliebiges Spiel": in den startbereitesten offenen Raum
 *                      (egal welcher Größe); existiert keiner, wird ein 2-Spieler-
 *                      Raum eröffnet, dem der nächste Spieler beitreten kann.
 *  2. Relay  → Client: {"type":"MATCHED","role":"HOST|GUEST","playerIndex":0..N-1,"roomSize":N}
 *  3. Routing nach MATCHED:
 *     - 2-Spieler-Raum: Frames 1:1 weiterleiten (Legacy, unverändert).
 *     - 3–4-Spieler HOST→Relay: {"type":"TO","to":K,"msg":{…}} → Peer[K] erhält {…} direkt.
 *     - 3–4-Spieler GUEST→Relay: {…} → HOST erhält {"type":"FROM","from":J,"msg":{…}}.
 *  4. Disconnect: {"type":"PEER_LEFT","playerIndex":J} an alle verbleibenden Peers.
 *
 * Port über Umgebungsvariable PORT (Default 8080) — passend für Render, Railway, Fly.io.
 */

private class Peer(val session: WebSocketSession) {
    @Volatile var room: Room? = null
    @Volatile var playerIndex: Int = -1
}

private class Room(val size: Int) {
    val peers = ArrayList<Peer>(size)
    @Volatile var matched = false
    val hasSpace get() = peers.size < size
    val isFull   get() = peers.size == size
    val freeSeats get() = size - peers.size   // je kleiner, desto startbereiter
}

private val matchMutex = Mutex()
/** Noch nicht gestartete Räume, die auf weitere Spieler warten. */
private val openRooms  = mutableListOf<Room>()

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        relayModule()
    }.start(wait = true)
}

fun Application.relayModule() {
    install(WebSockets) {
        pingPeriod   = Duration.ofSeconds(15)   // hält NAT/Proxy-Verbindungen offen
        timeout      = Duration.ofSeconds(60)
        maxFrameSize = Long.MAX_VALUE
    }
    routing {
        // Health-Check / Browser-Test — bestätigt, dass der Server läuft.
        get("/") {
            call.respondText("Go Fish! relay läuft. WebSocket-Endpunkt: /ws")
        }
        webSocket("/ws") {
            val me = Peer(this)
            try {
                for (frame in incoming) {
                    if (frame !is Frame.Text) continue
                    val text = frame.readText()
                    val room = me.room
                    when {
                        room == null   -> {
                            // Erstes Frame ist die Match-Anfrage (FIND, size:0 = beliebig)
                            val size = runCatching {
                                JSONObject(text).optInt("size", 2)
                            }.getOrDefault(2)
                            joinOrCreate(me, size)
                        }
                        room.matched   -> routeFrame(me, room, text)
                        // sonst: Raum offen, aber noch nicht voll → weitere Frames ignorieren
                    }
                }
            } finally {
                cleanup(me)
            }
        }
    }
}

/**
 * Ordnet [me] einem offenen Raum zu. Bei fester Größe (2–4) wird ein passender
 * offener Raum genutzt bzw. neu eröffnet; bei „beliebig" (alles außerhalb 2–4,
 * insb. 0) der startbereiteste offene Raum, ersatzweise ein neuer 2-Spieler-Raum.
 * Füllt der Beitritt den Raum, werden alle Peers gepaart.
 */
private suspend fun joinOrCreate(me: Peer, requestedSize: Int) {
    val filled: Room? = matchMutex.withLock {
        val room = if (requestedSize in 2..4) {
            openRooms.firstOrNull { it.size == requestedSize && it.hasSpace }
                ?: Room(requestedSize).also { openRooms.add(it) }
        } else {
            // „Beliebiges Spiel": fülle den startbereitesten Raum, sonst neuer 2er-Raum
            openRooms.filter { it.hasSpace }.minByOrNull { it.freeSeats }
                ?: Room(2).also { openRooms.add(it) }
        }
        me.playerIndex = room.peers.size
        me.room        = room
        room.peers.add(me)
        if (room.isFull) {
            room.matched = true
            openRooms.remove(room)
            room
        } else null
    }
    filled?.let { announceMatched(it) }
}

/** Schickt jedem Peer des vollen Raums seine Rolle, seinen Index und die Raumgröße. */
private suspend fun announceMatched(room: Room) {
    room.peers.forEachIndexed { index, peer ->
        val role = if (index == 0) "HOST" else "GUEST"
        runCatching {
            peer.session.send(Frame.Text(
                JSONObject()
                    .put("type",        "MATCHED")
                    .put("role",        role)
                    .put("playerIndex", index)
                    .put("roomSize",    room.size)
                    .toString()
            ))
        }
    }
}

/** Leitet einen Spiel-Frame gemäß Raumgröße und Absender-Rolle weiter. */
private suspend fun routeFrame(me: Peer, room: Room, text: String) {
    if (room.size == 2) {
        // Legacy 1:1 — direkt an den einzigen anderen Peer, Frame unverändert
        val other = room.peers.firstOrNull { it !== me } ?: return
        runCatching { other.session.send(Frame.Text(text)) }
        return
    }
    // 3–4 Spieler: adressiertes Routing
    if (me.playerIndex == 0) {
        // HOST sendet TO-Umschlag → innere Nachricht an Ziel-Peer ausliefern
        runCatching {
            val env   = JSONObject(text)
            val toIdx = env.getInt("to")
            val inner = env.getJSONObject("msg").toString()
            room.peers.getOrNull(toIdx)?.session?.send(Frame.Text(inner))
        }
    } else {
        // GUEST sendet rohen Frame → in FROM-Umschlag einwickeln und an HOST schicken
        runCatching {
            val envelope = JSONObject()
                .put("type", "FROM")
                .put("from", me.playerIndex)
                .put("msg",  JSONObject(text))
                .toString()
            room.peers.getOrNull(0)?.session?.send(Frame.Text(envelope))
        }
    }
}

/** Räumt nach Verbindungsabbruch auf und benachrichtigt verbleibende Peers. */
private suspend fun cleanup(me: Peer) {
    val (room, wasMatched) = matchMutex.withLock {
        val r = me.room ?: return@withLock (null to false)
        me.room = null
        if (!r.matched) {
            // Raum wartet noch → Peer entfernen, übrige Indizes neu vergeben (kein MATCHED verschickt)
            r.peers.remove(me)
            if (r.peers.isEmpty()) openRooms.remove(r)
            else r.peers.forEachIndexed { i, p -> p.playerIndex = i }
        }
        r to r.matched
    }
    if (room != null && wasMatched) {
        val msg = JSONObject()
            .put("type",        "PEER_LEFT")
            .put("playerIndex", me.playerIndex)
            .toString()
        room.peers.forEach { peer ->
            if (peer !== me) runCatching { peer.session.send(Frame.Text(msg)) }
        }
    }
}

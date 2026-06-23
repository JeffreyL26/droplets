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
 *  1. Client → Relay: {"type":"FIND","size":N}   (N = 2–4)
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
    var playerIndex: Int = -1
}

private class Room(val size: Int) {
    val peers = ArrayList<Peer>(size)
    @Volatile var matched = false
    val isFull get() = peers.size == size
}

private val matchMutex  = Mutex()
private val waitingRooms = mutableMapOf<Int, Room>()   // gewünschte Raumgröße → wartender Raum

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        relayModule()
    }.start(wait = true)
}

fun Application.relayModule() {
    install(WebSockets) {
        pingPeriod   = Duration.ofSeconds(15)
        timeout      = Duration.ofSeconds(60)
        maxFrameSize = Long.MAX_VALUE
    }
    routing {
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
                            // Noch nicht im Matchmaking → FIND-Nachricht auswerten
                            val size = runCatching {
                                JSONObject(text).optInt("size", 2).coerceIn(2, 4)
                            }.getOrDefault(2)
                            matchOrWait(me, size)
                        }
                        room.matched   -> routeFrame(me, room, text)
                        // else: Raum noch nicht voll, weiteres Frame ignorieren
                    }
                }
            } finally {
                cleanup(me)
            }
        }
    }
}

/** Fügt [me] in einen wartenden Raum der gewünschten Größe ein; ist der Raum voll, werden alle gepaart. */
private suspend fun matchOrWait(me: Peer, requestedSize: Int) {
    val filledRoom: Room? = matchMutex.withLock {
        val room = waitingRooms.getOrPut(requestedSize) { Room(requestedSize) }
        me.playerIndex = room.peers.size
        me.room        = room
        room.peers.add(me)
        if (room.isFull) {
            waitingRooms.remove(requestedSize)
            room.matched = true
            room
        } else {
            null
        }
    }
    if (filledRoom != null) {
        // Alle Peers gleichzeitig mit ihrer Rolle und Index benachrichtigen
        filledRoom.peers.forEachIndexed { index, peer ->
            val role = if (index == 0) "HOST" else "GUEST"
            runCatching {
                peer.session.send(Frame.Text(
                    JSONObject()
                        .put("type",        "MATCHED")
                        .put("role",        role)
                        .put("playerIndex", index)
                        .put("roomSize",    filledRoom.size)
                        .toString()
                ))
            }
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
        // HOST sendet TO-Envelope → innere Nachricht an Ziel-Peer ausliefern
        runCatching {
            val env    = JSONObject(text)
            val toIdx  = env.getInt("to")
            val inner  = env.getJSONObject("msg").toString()
            room.peers.getOrNull(toIdx)?.session?.send(Frame.Text(inner))
        }
    } else {
        // GUEST sendet rohen Frame → in FROM-Envelope einwickeln und an HOST schicken
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
            // Raum noch im Wartebereich → diesen Peer entfernen
            r.peers.remove(me)
            if (r.peers.isEmpty()) waitingRooms.remove(r.size)
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

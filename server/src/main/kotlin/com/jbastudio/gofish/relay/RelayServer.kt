package com.jbastudio.gofish.relay

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration

/**
 * Go Fish! — Matchmaking-/Relay-Server.
 *
 * Spiel-agnostisch: paart zwei wartende Spieler und reicht danach alle Frames
 * unverändert zwischen ihnen durch. Das eigentliche Spiel (GameEngine + Protokoll)
 * läuft komplett in der App — einer der beiden gepaarten Clients übernimmt die
 * HOST-Rolle und führt die Spiel-Autorität in-process aus.
 *
 * Ablauf:
 *  1. Client verbindet sich (WebSocket auf /ws) und schickt {"type":"FIND"}.
 *  2. Erster Wartender wird HOST, der nächste GUEST → beide bekommen
 *     {"type":"MATCHED","role":"HOST|GUEST"}.
 *  3. Ab dann werden alle Text-Frames 1:1 an den gepaarten Gegner weitergeleitet.
 *  4. Trennt sich einer, bekommt der andere {"type":"PEER_LEFT"}.
 *
 * Port über die Umgebungsvariable PORT (Default 8080) — passend für Railway,
 * Render, Fly.io usw.
 */

private class Peer(val session: WebSocketSession) {
    @Volatile var partner: Peer? = null
}

private val matchMutex = Mutex()
private var waiting: Peer? = null

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
        webSocket(RelayPaths.WS) {
            val me = Peer(this)
            try {
                for (frame in incoming) {
                    if (frame !is Frame.Text) continue
                    val text = frame.readText()
                    val partner = me.partner
                    if (partner != null) {
                        // Bereits gepaart → Frame unverändert weiterreichen
                        runCatching { partner.session.send(Frame.Text(text)) }
                    } else {
                        // Noch nicht gepaart → erstes Frame ist die Match-Anfrage (FIND)
                        matchOrWait(me)
                    }
                }
            } finally {
                cleanup(me)
            }
        }
    }
}

private suspend fun matchOrWait(me: Peer) {
    val opponent: Peer? = matchMutex.withLock {
        val w = waiting
        if (w == null) {
            waiting = me     // wir warten auf einen Gegner
            null
        } else {
            waiting = null   // Gegner gefunden
            w
        }
    }
    if (opponent != null) {
        // Der bereits Wartende wird HOST, der neue GUEST.
        me.partner = opponent
        opponent.partner = me
        runCatching { opponent.session.send(Frame.Text(matched(ROLE_HOST))) }
        runCatching { me.session.send(Frame.Text(matched(ROLE_GUEST))) }
    }
}

private suspend fun cleanup(me: Peer) {
    matchMutex.withLock { if (waiting === me) waiting = null }
    val partner = me.partner
    me.partner = null
    if (partner != null) {
        partner.partner = null
        runCatching { partner.session.send(Frame.Text(PEER_LEFT)) }
    }
}

// ── Konstanten / Nachrichten (müssen zu RelayProtocol in der App passen) ──────

private object RelayPaths { const val WS = "/ws" }

private const val ROLE_HOST  = "HOST"
private const val ROLE_GUEST = "GUEST"
private const val PEER_LEFT  = """{"type":"PEER_LEFT"}"""

private fun matched(role: String) = """{"type":"MATCHED","role":"$role"}"""

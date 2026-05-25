package com.jbastudio.gofish.network

import android.util.Log
import com.jbastudio.gofish.ui.components.AvatarChoice
import org.json.JSONObject

/**
 * Online-Spieler über das WebSocket-Relay.
 *
 * Ablauf:
 *  1. Verbindung zum Relay aufbauen und [RelayProtocol.FIND] senden.
 *  2. Das Relay paart zwei wartende Spieler und weist Rollen zu ([RelayProtocol.MATCHED]).
 *  3. Je nach Rolle:
 *     - **HOST**  → führt die [GameAuthority] in-process aus. Eigene Eingaben sind
 *                   Spieler 0 (lokal), der Gegner ist Spieler 1 (über das Relay).
 *     - **GUEST** → reiner Client: schickt JOIN/ASK über das Relay und empfängt
 *                   die Server-Nachrichten (JOINED / GAME_START / ASK_RESULT / …).
 *
 * Nach außen verhält sich diese Klasse für [com.jbastudio.gofish.MainActivity] und
 * [com.jbastudio.gofish.GameActivity] exakt wie ein [GameClient] (siehe [GameSession]).
 */
class OnlineGameClient(
    relayUrl: String,
    override val playerName: String,
    private val avatar: AvatarChoice = AvatarChoice()
) : GameSession {

    companion object {
        private const val TAG = "GoFishOnline"
        private const val HOST_PID  = 0
        private const val GUEST_PID = 1
    }

    private val relay = RelayConnection(relayUrl)

    /** Nur gesetzt, wenn diese Instanz die HOST-Rolle hat. */
    private var authority: GameAuthority? = null
    private var role: String? = null

    override var playerId: Int = -1
    override var onMessage:   ((JSONObject) -> Unit)? = null
    override var onConnected: (() -> Unit)?           = null
    override var onError:     ((String) -> Unit)?     = null

    fun connect() {
        relay.onOpen   = { relay.send(JSONObject().put("type", RelayProtocol.FIND).toString()) }
        relay.onText   = { text -> handleRelayText(text) }
        relay.onClosed = { reason -> onError?.invoke(reason ?: "Verbindung getrennt") }
        relay.connect()
    }

    // ── Relay-Eingang ────────────────────────────────────────────────────────

    private fun handleRelayText(text: String) {
        val msg = runCatching { JSONObject(text) }.getOrElse {
            Log.e(TAG, "Ungültige Relay-Nachricht: $text"); return
        }
        when (msg.optString("type")) {
            RelayProtocol.MATCHED   -> onMatched(msg.optString("role"))
            RelayProtocol.PEER_LEFT -> onPeerLeft()
            else                    -> onGameMessage(msg)
        }
    }

    private fun onMatched(matchedRole: String) {
        role = matchedRole
        if (matchedRole == RelayProtocol.ROLE_HOST) {
            // Wir sind der Host → Spiel-Autorität in-process aufsetzen.
            val auth = GameAuthority()
            auth.onLog = { Log.d(TAG, it) }
            auth.send = { pid, message ->
                if (pid == HOST_PID) deliverLocal(message)              // an uns selbst
                else relay.send(message.toString())                    // an den Gegner (Relay)
            }
            authority = auth
            onConnected?.invoke()
            // Host tritt als Spieler 0 bei; der Gast folgt mit seinem JOIN über das Relay.
            auth.playerJoin(HOST_PID, playerName, avatar.kind.name, avatar.color.name)
        } else {
            // Wir sind der Gast → JOIN an den Host (über das Relay) schicken.
            onConnected?.invoke()
            relay.send(
                JSONObject()
                    .put("type", Protocol.JOIN)
                    .put("name", playerName)
                    .put("avatarKind",  avatar.kind.name)
                    .put("avatarColor", avatar.color.name)
                    .toString()
            )
        }
    }

    private fun onGameMessage(msg: JSONObject) {
        val auth = authority
        if (auth != null) {
            // HOST: eingehende Frames sind die JOIN/ASK des Gastes (Spieler 1).
            when (msg.optString("type")) {
                Protocol.JOIN -> auth.playerJoin(
                    GUEST_PID,
                    msg.optString("name", "Gast"),
                    msg.optString("avatarKind",  "FISH"),
                    msg.optString("avatarColor", "SUN")
                )
                Protocol.ASK -> auth.playerAsk(GUEST_PID, msg.optString("rank"))
            }
        } else {
            // GUEST: Server-Nachrichten direkt an die UI weiterreichen.
            if (msg.optString("type") == Protocol.JOINED) {
                playerId = msg.optInt("yourId", playerId)
            }
            onMessage?.invoke(msg)
        }
    }

    /** HOST: Nachrichten an uns selbst (Spieler 0) an die UI weiterreichen. */
    private fun deliverLocal(msg: JSONObject) {
        if (msg.optString("type") == Protocol.JOINED) {
            playerId = msg.optInt("yourId", playerId)
        }
        onMessage?.invoke(msg)
    }

    private fun onPeerLeft() {
        val auth = authority
        if (auth != null) {
            // HOST: Gegner (Spieler 1) weg → Autorität informiert uns (OPPONENT_LEFT).
            auth.playerLeft(GUEST_PID)
        } else {
            // GUEST: Host weg → Sitzung beenden.
            onMessage?.invoke(JSONObject().put("type", Protocol.OPPONENT_LEFT))
        }
    }

    // ── GameSession ───────────────────────────────────────────────────────────

    override fun sendAsk(rank: String) {
        val auth = authority
        if (auth != null) auth.playerAsk(HOST_PID, rank)                       // HOST: lokal verarbeiten
        else relay.send(JSONObject().put("type", Protocol.ASK).put("rank", rank).toString()) // GUEST
    }

    override fun disconnect() {
        runCatching { relay.close() }
    }
}

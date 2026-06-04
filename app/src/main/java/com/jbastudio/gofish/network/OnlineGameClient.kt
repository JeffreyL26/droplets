package com.jbastudio.gofish.network

import android.util.Log
import com.jbastudio.gofish.ui.components.AvatarChoice
import org.json.JSONObject

/**
 * Online-Spieler über das WebSocket-Relay (2–4 Spieler).
 *
 * Ablauf:
 *  1. Verbindung zum Relay aufbauen und [RelayProtocol.FIND] mit der gewünschten
 *     Spielerzahl ([desiredPlayers]) senden.
 *  2. Das Relay bildet einen Raum und weist Rollen zu ([RelayProtocol.MATCHED]):
 *     genau ein HOST (Index 0), die übrigen GUESTs (Index 1..N-1).
 *  3. Je nach Rolle:
 *     - **HOST**  → führt die [GameAuthority] in-process aus. Eigene Eingaben sind
 *                   Spieler 0 (lokal); Gäste sind Spieler 1..N-1 (über das Relay).
 *     - **GUEST** → reiner Client: schickt JOIN/ASK über das Relay und empfängt
 *                   die Server-Nachrichten (JOINED / GAME_START / ASK_RESULT / …).
 *
 * Routing (siehe `RELAY_PROTOCOL.md`):
 *  - 2-Spieler-Raum: 1:1-Weiterleitung (Legacy, unverändert).
 *  - 3–4-Spieler-Raum: der HOST adressiert je Spieler via [RelayProtocol.TO];
 *    Gast-Frames erreichen den HOST als [RelayProtocol.FROM]. Gäste bleiben „roh".
 *
 * Nach außen verhält sich diese Klasse für [com.jbastudio.gofish.MainActivity] und
 * [com.jbastudio.gofish.GameActivity] exakt wie ein [GameClient] (siehe [GameSession]).
 */
class OnlineGameClient(
    relayUrl: String,
    override val playerName: String,
    private val avatar: AvatarChoice = AvatarChoice(),
    private val desiredPlayers: Int = 2
) : GameSession {

    companion object {
        private const val TAG = "GoFishOnline"
        private const val HOST_PID  = 0
        private const val GUEST_PID = 1
        /** onError-Sentinel: Relay konnte den gewünschten N-Spieler-Raum nicht bilden. */
        const val ERR_MULTI_UNSUPPORTED = "ONLINE_MULTI_UNSUPPORTED"
    }

    private val relay = RelayConnection(relayUrl)

    /** Nur gesetzt, wenn diese Instanz die HOST-Rolle hat. */
    private var authority: GameAuthority? = null
    private var role: String? = null
    private var roomSize: Int = 2

    override var playerId: Int = -1
    override var onMessage:   ((JSONObject) -> Unit)? = null
    override var onConnected: (() -> Unit)?           = null
    override var onError:     ((String) -> Unit)?     = null

    fun connect() {
        relay.onOpen   = {
            relay.send(
                JSONObject()
                    .put("type", RelayProtocol.FIND)
                    .put(RelayProtocol.FIELD_SIZE, desiredPlayers)
                    .toString()
            )
        }
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
            RelayProtocol.MATCHED   -> onMatched(msg)
            RelayProtocol.PEER_LEFT -> onPeerLeft(msg)
            else                    -> onGameMessage(msg)
        }
    }

    private fun onMatched(msg: JSONObject) {
        role     = msg.optString(RelayProtocol.FIELD_ROLE)
        roomSize = msg.optInt(RelayProtocol.FIELD_ROOM, 2)

        // Sicherheitsnetz: gewünschte Spielerzahl > 2, aber das Relay bildet keinen
        // passenden Raum (z. B. noch ohne N-Spieler-Support) → klare Fehlermeldung
        // statt eines still herabgestuften 2-Spieler-Spiels.
        if (desiredPlayers > 2 && roomSize < desiredPlayers) {
            onError?.invoke(ERR_MULTI_UNSUPPORTED)
            runCatching { relay.close() }
            return
        }

        if (role == RelayProtocol.ROLE_HOST) {
            // Wir sind der Host → Spiel-Autorität in-process aufsetzen.
            val auth = GameAuthority()
            auth.expectedPlayers = roomSize
            auth.onLog = { Log.d(TAG, it) }
            auth.send = { pid, message ->
                when {
                    pid == HOST_PID -> deliverLocal(message)               // an uns selbst
                    roomSize > 2    -> relay.send(envelopeTo(pid, message)) // adressiert an Gast[pid]
                    else            -> relay.send(message.toString())       // 2-Spieler: 1:1 (Legacy)
                }
            }
            authority = auth
            onConnected?.invoke()
            // Host tritt als Spieler 0 bei; Gäste folgen mit ihrem JOIN über das Relay.
            auth.playerJoin(HOST_PID, playerName, avatar.kind.name, avatar.color.name)
        } else {
            // Wir sind ein Gast → JOIN an den Host (über das Relay) schicken.
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
            // HOST: Gast-Frames verarbeiten.
            if (roomSize > 2 && msg.optString("type") == RelayProtocol.FROM) {
                // 3–4-Spieler: adressierter Umschlag mit Absender-Index.
                val from  = msg.optInt(RelayProtocol.FIELD_FROM, GUEST_PID)
                val inner = msg.optJSONObject(RelayProtocol.FIELD_MSG) ?: return
                dispatchFromGuest(auth, from, inner)
            } else {
                // 2-Spieler-Legacy: Frames sind direkt JOIN/ASK des einen Gastes.
                dispatchFromGuest(auth, GUEST_PID, msg)
            }
        } else {
            // GUEST: Server-Nachrichten direkt an die UI weiterreichen.
            if (msg.optString("type") == Protocol.JOINED) {
                playerId = msg.optInt("yourId", playerId)
            }
            onMessage?.invoke(msg)
        }
    }

    /** HOST: eine rohe Gast-Spielnachricht der zuständigen Spieler-ID zuordnen. */
    private fun dispatchFromGuest(auth: GameAuthority, fromPid: Int, msg: JSONObject) {
        when (msg.optString("type")) {
            Protocol.JOIN -> auth.playerJoin(
                fromPid,
                msg.optString("name", "Gast"),
                msg.optString("avatarKind",  "FISH"),
                msg.optString("avatarColor", "SUN")
            )
            Protocol.ASK -> auth.playerAsk(
                fromPid,
                msg.optString("rank"),
                if (msg.has("targetId")) msg.getInt("targetId") else null
            )
        }
    }

    /** HOST: Nachrichten an uns selbst (Spieler 0) an die UI weiterreichen. */
    private fun deliverLocal(msg: JSONObject) {
        if (msg.optString("type") == Protocol.JOINED) {
            playerId = msg.optInt("yourId", playerId)
        }
        onMessage?.invoke(msg)
    }

    private fun onPeerLeft(msg: JSONObject) {
        val idx  = msg.optInt(RelayProtocol.FIELD_INDEX, GUEST_PID)
        val auth = authority
        if (auth != null) {
            // HOST: der betreffende Spieler ist weg → Autorität informiert die übrigen.
            auth.playerLeft(idx)
        } else {
            // GUEST: nur wenn der HOST (Index 0) weg ist, endet die Sitzung.
            if (roomSize <= 2 || idx == HOST_PID) {
                onMessage?.invoke(JSONObject().put("type", Protocol.OPPONENT_LEFT))
            }
        }
    }

    // ── GameSession ───────────────────────────────────────────────────────────

    override fun sendAsk(rank: String, targetId: Int?) {
        val auth = authority
        if (auth != null) {
            auth.playerAsk(HOST_PID, rank, targetId)                           // HOST: lokal verarbeiten
        } else {
            val msg = JSONObject().put("type", Protocol.ASK).put("rank", rank)
            if (targetId != null) msg.put("targetId", targetId)
            relay.send(msg.toString())                                         // GUEST (roh)
        }
    }

    override fun disconnect() {
        runCatching { relay.close() }
    }

    /** Verpackt eine Host-Spielnachricht adressiert an Spieler[toIndex] (N-Spieler-Routing). */
    private fun envelopeTo(toIndex: Int, message: JSONObject): String =
        JSONObject()
            .put("type", RelayProtocol.TO)
            .put(RelayProtocol.FIELD_TO, toIndex)
            .put(RelayProtocol.FIELD_MSG, message)
            .toString()
}

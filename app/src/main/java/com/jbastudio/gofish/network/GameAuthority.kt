package com.jbastudio.gofish.network

import com.jbastudio.gofish.game.AskResult
import com.jbastudio.gofish.game.GameEngine
import com.jbastudio.gofish.model.Card
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Transport-unabhängige Spiel-Autorität.
 *
 * Kapselt die komplette Spiellogik ([GameEngine]) plus den Aufbau der
 * JSON-Nachrichten des Protokolls. Eingaben kommen über
 * [playerJoin]/[playerAsk]/[playerLeft] (jeweils mit der Spieler-ID), Ausgaben
 * gehen über den [send]-Callback an genau einen Spieler raus.
 *
 * Dadurch kann dieselbe Logik sowohl vom LAN-[GameServer] (TCP-Sockets) als auch
 * vom Online-Host ([OnlineGameClient] über das WebSocket-Relay) verwendet werden.
 *
 * Es werden — wie im ursprünglichen Server — genau zwei Spieler erwartet
 * (IDs 0 und 1). Sobald beide beigetreten sind, startet das Spiel genau einmal.
 */
class GameAuthority {

    private data class AvatarMeta(val kind: String, val color: String)

    private val engine      = GameEngine()
    private val avatars     = ConcurrentHashMap<Int, AvatarMeta>()
    private val joined      = ConcurrentHashMap.newKeySet<Int>()
    private val gameStarted = AtomicBoolean(false)

    /** Schickt eine Nachricht an genau einen Spieler (per ID). */
    var send:  ((Int, JSONObject) -> Unit)? = null
    var onLog: ((String) -> Unit)?          = null

    /** Wurde das Spiel bereits gestartet (beide Spieler da)? */
    fun isStarted(): Boolean = gameStarted.get()

    /** Ein Spieler tritt bei. Sobald zwei da sind, startet das Spiel genau einmal. */
    fun playerJoin(pid: Int, name: String, avatarKind: String, avatarColor: String) {
        synchronized(engine) {
            avatars[pid] = AvatarMeta(avatarKind, avatarColor)
            engine.addPlayer(pid, name)
            joined.add(pid)
            log("Spieler $pid beigetreten: $name ($avatarKind/$avatarColor)")

            send?.invoke(pid, JSONObject().apply {
                put("type", Protocol.JOINED)
                put("yourId", pid)
            })

            // Spiel genau einmal starten (CAS-gesichert), wenn beide da sind
            if (joined.size >= 2 && gameStarted.compareAndSet(false, true)) {
                engine.startGame()
                log("Spiel gestartet!")
                sendGameStart()
            }
        }
    }

    /** Ein Spieler fragt nach einem Rang. */
    fun playerAsk(askerId: Int, rank: String) {
        val result: AskResult = synchronized(engine) {
            if (engine.gameOver || !engine.canAsk(askerId, rank)) return
            engine.processAsk(askerId, rank)
        }
        log("Spieler $askerId fragte nach $rank → ${result.cardsReceived.size} Karte(n)")
        broadcastResult(result)
    }

    /** Ein Spieler hat die Verbindung verloren — verbleibende Spieler informieren. */
    fun playerLeft(pid: Int) {
        if (!gameStarted.get()) return
        joined.filter { it != pid }.forEach { other ->
            runCatching { send?.invoke(other, JSONObject().put("type", Protocol.OPPONENT_LEFT)) }
        }
    }

    private fun sendGameStart() {
        for (p in engine.players) {
            val opp   = engine.opponentOf(p.id)
            val oppAv = avatars[opp.id] ?: AvatarMeta("FISH", "SUN")
            send?.invoke(p.id, JSONObject().apply {
                put("type", Protocol.GAME_START)
                put("yourHand", cardsToJson(p.hand))
                put("opponentName", opp.name)
                put("opponentAvatarKind",  oppAv.kind)
                put("opponentAvatarColor", oppAv.color)
                put("opponentHandSize", opp.hand.size)
                put("deckSize", engine.deckSize())
                put("yourTurn", engine.currentPlayerId == p.id)
            })
        }
    }

    private fun broadcastResult(r: AskResult) {
        val asker  = engine.getPlayer(r.askerId)
        val target = engine.opponentOf(r.askerId)

        // Asker erhält vollständige Informationen
        send?.invoke(r.askerId, JSONObject().apply {
            put("type", Protocol.ASK_RESULT)
            put("rank", r.rank)
            put("askerIsYou", true)
            put("gotCards", r.cardsReceived.isNotEmpty())
            put("cardsReceived", cardsToJson(r.cardsReceived))
            put("wentFishing", r.wentFishing)
            r.drawnCard?.let { put("drawnCard", it.toJson()); put("drawnMatched", r.drawnCardMatchesRank) }
            put("newBooks", JSONArray(r.newBooksForAsker))
            put("yourHand", cardsToJson(asker.hand))
            put("yourBooks", JSONArray(asker.books))
            put("opponentHandSize", target.hand.size)
            put("opponentBooks", JSONArray(target.books))
            put("deckSize", engine.deckSize())
            put("yourTurn", r.nextPlayerId == r.askerId && !r.gameOver)
            put("gameOver", r.gameOver)
            if (r.gameOver) putGameOver(this, asker.books.size, target.books.size, r.winnerName)
        })

        // Target erhält eingeschränkte Informationen (gezogene Karte bleibt geheim)
        send?.invoke(target.id, JSONObject().apply {
            put("type", Protocol.ASK_RESULT)
            put("rank", r.rank)
            put("askerIsYou", false)
            put("gotCards", r.cardsReceived.isNotEmpty())
            put("cardCount", r.cardsReceived.size)
            put("wentFishing", r.wentFishing)
            // Hat der Fragende beim Angeln die gesuchte Karte gezogen? Dann ist er
            // erneut dran — der Gegner soll das ebenfalls im Log sehen.
            put("drawnMatched", r.drawnCardMatchesRank)
            put("newBooks", JSONArray(r.newBooksForAsker))
            put("yourHand", cardsToJson(target.hand))
            put("yourBooks", JSONArray(target.books))
            put("opponentHandSize", asker.hand.size)
            put("opponentBooks", JSONArray(asker.books))
            put("deckSize", engine.deckSize())
            put("yourTurn", r.nextPlayerId == target.id && !r.gameOver)
            put("gameOver", r.gameOver)
            if (r.gameOver) putGameOver(this, target.books.size, asker.books.size, r.winnerName)
        })
    }

    private fun putGameOver(obj: JSONObject, myBks: Int, opBks: Int, winner: String?) {
        obj.put("myBooks", myBks)
        obj.put("opBooks", opBks)
        obj.put("winnerName", winner ?: "Unentschieden!")
    }

    private fun cardsToJson(c: List<Card>) = JSONArray(c.map { it.toJson() })
    private fun log(msg: String) { onLog?.invoke(msg) }
}

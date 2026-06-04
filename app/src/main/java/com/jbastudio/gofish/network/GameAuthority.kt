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
 * Unterstützt **2–4 Spieler**. Das Spiel startet, sobald [expectedPlayers]
 * Spieler beigetreten sind (Standard 2). Die Nachrichten enthalten sowohl die
 * vollständige Spielerliste (`players`) als auch — zur Abwärtskompatibilität mit
 * dem 2-Spieler-UI — die bisherigen Singular-Gegnerfelder.
 */
class GameAuthority {

    private data class AvatarMeta(val kind: String, val color: String)

    private val engine      = GameEngine()
    private val avatars     = ConcurrentHashMap<Int, AvatarMeta>()
    private val joined      = ConcurrentHashMap.newKeySet<Int>()
    private val gameStarted = AtomicBoolean(false)

    /** Erwartete Spielerzahl (vom Host gesetzt). Spielstart, sobald erreicht. */
    var expectedPlayers: Int = 2

    /** Schickt eine Nachricht an genau einen Spieler (per ID). */
    var send:  ((Int, JSONObject) -> Unit)? = null
    var onLog: ((String) -> Unit)?          = null

    /** Wurde das Spiel bereits gestartet (genügend Spieler da)? */
    fun isStarted(): Boolean = gameStarted.get()

    /** Ein Spieler tritt bei. Sobald [expectedPlayers] da sind, startet das Spiel genau einmal. */
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

            // Spiel genau einmal starten (CAS-gesichert), wenn genügend Spieler da sind
            if (joined.size >= expectedPlayers && gameStarted.compareAndSet(false, true)) {
                engine.startGame()
                log("Spiel gestartet mit ${engine.players.size} Spielern!")
                sendGameStart()
            } else if (!gameStarted.get() && expectedPlayers > 2) {
                // Mehrspieler-Lobby: Warte-Fortschritt an alle bereits Beigetretenen
                val update = JSONObject().apply {
                    put("type", Protocol.LOBBY)
                    put("joined", joined.size)
                    put("expected", expectedPlayers)
                }
                joined.forEach { id -> runCatching { send?.invoke(id, update) } }
            }
        }
    }

    /**
     * Ein Spieler fragt bei [targetId] nach einem Rang. Ohne [targetId] wird im
     * 2-Spieler-Fall der einzige Gegner gewählt.
     */
    fun playerAsk(askerId: Int, rank: String, targetId: Int? = null) {
        val result: AskResult = synchronized(engine) {
            if (engine.gameOver) return
            val target = targetId ?: engine.othersOf(askerId).singleOrNull()?.id ?: return
            if (!engine.canAsk(askerId, target, rank)) return
            engine.processAsk(askerId, target, rank)
        }
        log("Spieler $askerId fragte ${result.targetId} nach $rank → ${result.cardsReceived.size} Karte(n)")
        broadcastResult(result)
    }

    /**
     * Ein Spieler hat die Verbindung verloren. Bei ≤1 verbleibenden aktiven
     * Spielern endet die Sitzung (wie bisher: [Protocol.OPPONENT_LEFT]); sonst
     * läuft die Partie weiter — die Handkarten wandern zurück ins Deck und alle
     * Verbliebenen erhalten ein [Protocol.PLAYER_LEFT] mit aktualisiertem Stand.
     */
    fun playerLeft(pid: Int) {
        if (!gameStarted.get()) return
        synchronized(engine) {
            val leaving = engine.players.firstOrNull { it.id == pid } ?: return
            if (!leaving.active) return
            val leftName = leaving.name
            engine.removePlayer(pid)

            val active = engine.activePlayers()
            if (active.size <= 1) {
                active.forEach { p ->
                    runCatching { send?.invoke(p.id, JSONObject().put("type", Protocol.OPPONENT_LEFT)) }
                }
            } else {
                active.forEach { p ->
                    runCatching {
                        send?.invoke(p.id, JSONObject().apply {
                            put("type", Protocol.PLAYER_LEFT)
                            put("playerId", pid)
                            put("playerName", leftName)
                            put("players", playersJson())
                            put("currentPlayerId", engine.currentPlayerId)
                            put("yourTurn", engine.currentPlayerId == p.id && !engine.gameOver)
                            put("deckSize", engine.deckSize())
                            put("yourHand", cardsToJson(p.hand))
                            put("gameOver", engine.gameOver)
                        })
                    }
                }
            }
        }
    }

    private fun sendGameStart() {
        for (p in engine.players) {
            val firstOpp   = engine.players.firstOrNull { it.id != p.id }
            val firstOppAv = firstOpp?.let { avatars[it.id] } ?: AvatarMeta("FISH", "SUN")
            send?.invoke(p.id, JSONObject().apply {
                put("type", Protocol.GAME_START)
                put("yourId", p.id)
                put("yourHand", cardsToJson(p.hand))
                put("yourTurn", engine.currentPlayerId == p.id)
                put("currentPlayerId", engine.currentPlayerId)
                put("deckSize", engine.deckSize())
                put("players", playersJson())
                // 2-Spieler-Kompatibilität: erster Gegner als Singular-Felder
                if (firstOpp != null) {
                    put("opponentName", firstOpp.name)
                    put("opponentAvatarKind",  firstOppAv.kind)
                    put("opponentAvatarColor", firstOppAv.color)
                    put("opponentHandSize", firstOpp.hand.size)
                }
            })
        }
    }

    private fun broadcastResult(r: AskResult) {
        for (p in engine.players) {
            if (!p.active) continue
            val askerIsYou  = p.id == r.askerId
            val targetIsYou = p.id == r.targetId
            val firstOpp    = engine.players.firstOrNull { it.id != p.id }
            send?.invoke(p.id, JSONObject().apply {
                put("type", Protocol.ASK_RESULT)
                put("rank", r.rank)
                put("askerId", r.askerId)
                put("targetId", r.targetId)
                put("askerIsYou", askerIsYou)
                put("targetIsYou", targetIsYou)
                put("gotCards", r.cardsReceived.isNotEmpty())
                put("cardCount", r.cardsReceived.size)
                if (askerIsYou) put("cardsReceived", cardsToJson(r.cardsReceived))
                put("wentFishing", r.wentFishing)
                put("drawnMatched", r.drawnCardMatchesRank)
                if (askerIsYou) r.drawnCard?.let { put("drawnCard", it.toJson()) }
                put("newBooks", JSONArray(r.newBooksForAsker))
                put("yourHand", cardsToJson(p.hand))
                put("yourBooks", JSONArray(p.books))
                put("players", playersJson())
                put("currentPlayerId", engine.currentPlayerId)
                put("deckSize", engine.deckSize())
                put("yourTurn", r.nextPlayerId == p.id && !r.gameOver)
                put("gameOver", r.gameOver)
                // 2-Spieler-Kompatibilität: Singular-Gegnerfelder (der jeweils andere)
                if (firstOpp != null) {
                    put("opponentHandSize", firstOpp.hand.size)
                    put("opponentBooks", JSONArray(firstOpp.books))
                }
                if (r.gameOver) putGameOver(this, p.books.size, firstOpp?.books?.size ?: 0, r.winnerName)
            })
        }
    }

    /** Vollständige Spielerliste (für die N-Spieler-fähige UI). */
    private fun playersJson(): JSONArray {
        val arr = JSONArray()
        engine.players.forEach { p ->
            val av = avatars[p.id] ?: AvatarMeta("FISH", "SUN")
            arr.put(JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("avatarKind",  av.kind)
                put("avatarColor", av.color)
                put("handSize", p.hand.size)
                put("books", JSONArray(p.books))
                put("active", p.active)
            })
        }
        return arr
    }

    private fun putGameOver(obj: JSONObject, myBks: Int, opBks: Int, winner: String?) {
        obj.put("myBooks", myBks)
        obj.put("opBooks", opBks)
        obj.put("winnerName", winner ?: "Unentschieden!")
    }

    private fun cardsToJson(c: List<Card>) = JSONArray(c.map { it.toJson() })
    private fun log(msg: String) { onLog?.invoke(msg) }
}

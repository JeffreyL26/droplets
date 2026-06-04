package com.jbastudio.gofish.game

import com.jbastudio.gofish.model.Card
import com.jbastudio.gofish.model.Deck

data class Player(
    val id: Int,
    val name: String,
    val hand: MutableList<Card> = mutableListOf(),
    val books: MutableList<String> = mutableListOf(),
    /** false, sobald der Spieler die Partie verlassen hat (Hand zurück ins Deck). */
    var active: Boolean = true
)

data class AskResult(
    val askerId: Int,
    val targetId: Int,                 // wen wurde gefragt
    val rank: String,
    val cardsReceived: List<Card>,     // leer → Go Fish
    val wentFishing: Boolean,
    val drawnCard: Card?,              // null wenn Deck leer
    val drawnCardMatchesRank: Boolean,
    val newBooksForAsker: List<String>,
    val nextPlayerId: Int,
    val gameOver: Boolean,
    val winnerName: String?            // null = Unentschieden
)

/**
 * Transport-unabhängige Spiellogik für **2–4 Spieler**.
 *
 * Spieler werden in Beitrittsreihenfolge geführt (IDs aufsteigend); der Zug
 * wandert im Uhrzeigersinn. Beim Fragen wird ein konkretes Ziel angegeben
 * ([processAsk] mit `targetId`). Für den 2-Spieler-Fall existieren weiterhin
 * 2-argumentige Komfort-Overloads, sodass bestehende Aufrufer unverändert
 * funktionieren.
 */
class GameEngine {
    val players = mutableListOf<Player>()
    private val deck = Deck()
    var currentPlayerId = 0
        private set
    var gameOver = false
        private set

    /**
     * Zuletzt von einem Spieler gefragtes Ziel. Dieses Ziel darf vom selben
     * Spieler **nicht direkt erneut** gefragt werden — auch zugübergreifend.
     */
    private val lastAskedTarget = mutableMapOf<Int, Int>()

    fun addPlayer(id: Int, name: String) {
        require(players.size < MAX_PLAYERS) { "Maximal $MAX_PLAYERS Spieler" }
        players.add(Player(id, name))
    }

    fun startGame() {
        require(players.size in 2..MAX_PLAYERS)
        deck.reset()
        val handSize = startingHandSize(players.size)
        players.forEach { p ->
            p.hand.clear(); p.books.clear(); p.active = true
            p.hand.addAll(deck.dealN(handSize))
            extractBooks(p)          // unwahrscheinlich, aber abgesichert
        }
        lastAskedTarget.clear()
        currentPlayerId = players.first().id
        gameOver = false
    }

    companion object {
        const val MAX_PLAYERS = 4

        /** Karten je Spieler beim Start, abhängig von der Spielerzahl. */
        fun startingHandSize(playerCount: Int) = when (playerCount) {
            2 -> 8
            3 -> 7
            else -> 5            // 4 Spieler
        }
    }

    fun getPlayer(id: Int) = players.first { it.id == id }

    /** Alle übrigen, noch aktiven Spieler (mögliche Frage-Ziele). */
    fun othersOf(id: Int) = players.filter { it.id != id && it.active }

    fun activePlayers() = players.filter { it.active }

    /** Einziger Gegner (nur im 2-Spieler-Fall eindeutig) — für Altpfade. */
    fun opponentOf(id: Int) = players.first { it.id != id }

    fun deckSize() = deck.size

    // ── Fragen ────────────────────────────────────────────────────────────

    /** Darf [playerId] bei [targetId] nach [rank] fragen? */
    fun canAsk(playerId: Int, targetId: Int, rank: String): Boolean {
        if (gameOver || currentPlayerId != playerId) return false
        val asker  = players.firstOrNull { it.id == playerId } ?: return false
        val target = players.firstOrNull { it.id == targetId } ?: return false
        if (targetId == playerId || !target.active) return false
        if (asker.hand.none { it.rank == rank }) return false
        // Letztes Ziel nicht direkt erneut — außer es gibt keine Alternative.
        if (othersOf(playerId).size > 1 && lastAskedTarget[playerId] == targetId) return false
        return true
    }

    /** 2-Spieler-Komfort: Ziel = einziger Gegner. */
    fun canAsk(playerId: Int, rank: String): Boolean {
        val sole = othersOf(playerId).singleOrNull() ?: return false
        return canAsk(playerId, sole.id, rank)
    }

    fun processAsk(askerId: Int, targetId: Int, rank: String): AskResult {
        check(!gameOver)
        check(currentPlayerId == askerId) { "Nicht dein Zug" }
        val asker  = getPlayer(askerId)
        val target = getPlayer(targetId)
        require(targetId != askerId) { "Man fragt nicht sich selbst" }
        require(target.active) { "Ziel ist nicht mehr im Spiel" }
        require(asker.hand.any { it.rank == rank }) { "Muss $rank halten um zu fragen" }

        lastAskedTarget[askerId] = targetId

        // Karten übertragen oder angeln
        val received = target.hand.filter { it.rank == rank }.toList()
        var wentFishing = false
        var drawn: Card? = null
        var drawnMatched = false

        if (received.isNotEmpty()) {
            target.hand.removeAll(received.toSet())
            asker.hand.addAll(received)
        } else {
            wentFishing = true
            drawn = deck.draw()
            drawn?.let { asker.hand.add(it); drawnMatched = it.rank == rank }
        }

        // Bücher prüfen (nur Asker erhält Karten)
        val newBooks = extractBooks(asker)

        // Leere Hände aller aktiven Spieler aus dem Deck nachfüllen
        players.filter { it.active }.forEach { p ->
            if (p.hand.isEmpty() && !deck.isEmpty) deck.draw()?.let { c -> p.hand.add(c) }
        }

        val keepTurn = received.isNotEmpty() || drawnMatched
        checkGameOver()

        val nextId = when {
            gameOver                            -> currentPlayerId
            keepTurn && asker.hand.isNotEmpty() -> askerId
            else                                -> nextActivePlayerAfter(askerId)
        }
        if (!gameOver) currentPlayerId = nextId

        return AskResult(askerId, targetId, rank, received, wentFishing, drawn,
            drawnMatched, newBooks, nextId, gameOver, winnerNameOrNull())
    }

    /** 2-Spieler-Komfort: Ziel = einziger Gegner. */
    fun processAsk(askerId: Int, rank: String): AskResult =
        processAsk(askerId, othersOf(askerId).single().id, rank)

    // ── Verlassen ───────────────────────────────────────────────────────────

    /**
     * Ein Spieler verlässt die Partie: seine Handkarten werden zurück ins Deck
     * gemischt, der Spieler wird inaktiv. War er am Zug, rückt der nächste
     * aktive Spieler nach. Bei ≤1 verbleibenden aktiven Spielern endet das Spiel.
     */
    fun removePlayer(id: Int) {
        val p = players.firstOrNull { it.id == id } ?: return
        if (!p.active) return
        deck.addAndShuffle(p.hand.toList())
        p.hand.clear()
        p.active = false
        lastAskedTarget.remove(id)
        lastAskedTarget.entries.removeAll { it.value == id }

        if (!gameOver && currentPlayerId == id) {
            currentPlayerId = nextActivePlayerAfter(id)
        }
        checkGameOver()
    }

    // ── intern ───────────────────────────────────────────────────────────────

    /** Nächster aktiver Spieler mit Karten, im Uhrzeigersinn nach [id]. */
    private fun nextActivePlayerAfter(id: Int): Int {
        val n = players.size
        val startIdx = players.indexOfFirst { it.id == id }
        if (startIdx >= 0) {
            for (step in 1..n) {
                val cand = players[(startIdx + step) % n]
                if (cand.active && cand.hand.isNotEmpty()) return cand.id
            }
        }
        return players.firstOrNull { it.active && it.hand.isNotEmpty() }?.id
            ?: players.firstOrNull { it.active }?.id
            ?: id
    }

    /** Entfernt vollständige Sätze (4 Karten) aus der Hand. */
    private fun extractBooks(p: Player): List<String> {
        val newBooks = mutableListOf<String>()
        p.hand.groupBy { it.rank }.forEach { (rank, cards) ->
            if (cards.size == 4) {
                p.hand.removeAll(cards.toSet())
                p.books.add(rank)
                newBooks.add(rank)
            }
        }
        return newBooks
    }

    private fun checkGameOver() {
        val active = players.filter { it.active }
        if (active.size <= 1) { gameOver = true; return }
        if (players.sumOf { it.books.size } >= 13) { gameOver = true; return }
        // Niemand kann mehr fragen: Deck leer und höchstens einer hat noch Karten.
        if (deck.isEmpty && active.count { it.hand.isNotEmpty() } <= 1) gameOver = true
    }

    /** Sieger = eindeutiges Buch-Maximum unter den aktiven Spielern; sonst Unentschieden. */
    private fun winnerNameOrNull(): String? {
        if (!gameOver) return null
        val contenders = players.filter { it.active }
        if (contenders.isEmpty()) return null
        val max = contenders.maxOf { it.books.size }
        return contenders.filter { it.books.size == max }
            .takeIf { it.size == 1 }?.first()?.name
    }
}

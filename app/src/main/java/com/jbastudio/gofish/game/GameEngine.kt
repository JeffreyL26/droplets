package com.jbastudio.gofish.game

import com.jbastudio.gofish.model.Card
import com.jbastudio.gofish.model.Deck

data class Player(
    val id: Int,
    val name: String,
    val hand: MutableList<Card> = mutableListOf(),
    val books: MutableList<String> = mutableListOf()
)

data class AskResult(
    val askerId: Int,
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

class GameEngine {
    val players = mutableListOf<Player>()
    private val deck = Deck()
    var currentPlayerId = 0
        private set
    var gameOver = false
        private set

    fun addPlayer(id: Int, name: String) {
        require(players.size < 2)
        players.add(Player(id, name))
    }

    fun startGame() {
        require(players.size == 2)
        deck.reset()
        players.forEach { p ->
            p.hand.clear(); p.books.clear()
            p.hand.addAll(deck.dealN(STARTING_HAND_SIZE))
            extractBooks(p)          // unwahrscheinlich, aber abgesichert
        }
        currentPlayerId = 0
        gameOver = false
    }

    companion object {
        /** Karten, die jeder Spieler beim Spielstart erhält. */
        const val STARTING_HAND_SIZE = 8
    }

    fun getPlayer(id: Int) = players.first { it.id == id }
    fun opponentOf(id: Int) = players.first { it.id != id }
    fun deckSize() = deck.size

    /** Prüft ob der Spieler diesen Rang erfragen darf. */
    fun canAsk(playerId: Int, rank: String) =
        currentPlayerId == playerId && getPlayer(playerId).hand.any { it.rank == rank }

    fun processAsk(askerId: Int, rank: String): AskResult {
        check(!gameOver)
        check(currentPlayerId == askerId) { "Nicht dein Zug" }
        val asker  = getPlayer(askerId)
        val target = opponentOf(askerId)
        require(asker.hand.any { it.rank == rank }) { "Muss $rank halten um zu fragen" }

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

        // Leere Hände aus Deck nachfüllen
        if (target.hand.isEmpty() && !deck.isEmpty) deck.draw()?.let { target.hand.add(it) }
        if (asker.hand.isEmpty()  && !deck.isEmpty) deck.draw()?.let { asker.hand.add(it) }

        val keepTurn = received.isNotEmpty() || drawnMatched
        checkGameOver()

        // Nächsten Spieler bestimmen
        val nextId = when {
            gameOver                            -> currentPlayerId
            keepTurn && asker.hand.isNotEmpty() -> askerId
            target.hand.isNotEmpty()            -> target.id
            asker.hand.isNotEmpty()             -> askerId
            else -> { gameOver = true; currentPlayerId }
        }
        if (!gameOver) currentPlayerId = nextId

        val winnerName = if (gameOver) {
            val max = players.maxOf { it.books.size }
            players.filter { it.books.size == max }
                .takeIf { it.size == 1 }?.first()?.name   // null = Unentschieden
        } else null

        return AskResult(askerId, rank, received, wentFishing, drawn,
            drawnMatched, newBooks, nextId, gameOver, winnerName)
    }

    // Entfernt vollständige Sätze (4 Karten) aus der Hand
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
        if (players.sumOf { it.books.size } >= 13) { gameOver = true; return }
        if (players.all { it.hand.isEmpty() } && deck.isEmpty) gameOver = true
    }
}
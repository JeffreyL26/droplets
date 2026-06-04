package com.jbastudio.gofish.model

class Deck {
    private val cards: MutableList<Card> = mutableListOf()

    init { reset() }

    fun reset() {
        cards.clear()
        for (r in Card.RANKS) for (s in Card.SUITS) cards.add(Card(r, s))
        cards.shuffle()
    }

    fun draw(): Card? = if (cards.isNotEmpty()) cards.removeAt(0) else null
    fun dealN(n: Int) = (1..n).mapNotNull { draw() }

    /** Karten zurück ins Deck legen und neu mischen (z. B. wenn ein Spieler die Partie verlässt). */
    fun addAndShuffle(newCards: List<Card>) {
        if (newCards.isEmpty()) return
        cards.addAll(newCards)
        cards.shuffle()
    }

    val size get() = cards.size
    val isEmpty get() = cards.isEmpty()
}
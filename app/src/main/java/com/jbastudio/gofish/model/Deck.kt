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
    val size get() = cards.size
    val isEmpty get() = cards.isEmpty()
}
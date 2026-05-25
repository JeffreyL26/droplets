package com.jbastudio.gofish.network

object Protocol {
    const val PORT = 5555

    // Client → Server
    const val JOIN = "JOIN"
    const val ASK  = "ASK"

    // Server → Client
    const val JOINED        = "JOINED"
    const val GAME_START    = "GAME_START"
    const val ASK_RESULT    = "ASK_RESULT"
    const val OPPONENT_LEFT = "OPPONENT_LEFT"
}
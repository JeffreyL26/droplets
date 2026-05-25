package com.jbastudio.gofish.network

/**
 * Steuer-Nachrichten zwischen App und Relay-Server (Matchmaking).
 *
 * Diese liegen "über" dem eigentlichen Spielprotokoll ([Protocol]): Vor dem
 * Match tauschen Client und Relay nur diese Typen aus. Nach dem Match leitet das
 * Relay alle weiteren Frames unverändert an den gepaarten Gegner weiter — das ist
 * dann das normale Spielprotokoll (JOIN / ASK / GAME_START / ASK_RESULT / …).
 *
 * Es gibt keine Typ-Kollision, da sich diese Werte von [Protocol] unterscheiden.
 */
object RelayProtocol {
    /** Standard-Pfad des WebSocket-Endpunkts auf dem Relay. */
    const val PATH = "/ws"

    // Client → Relay
    const val FIND = "FIND"          // "Suche ein Spiel"

    // Relay → Client
    const val MATCHED   = "MATCHED"  // Gegner gefunden, Rolle zugewiesen
    const val PEER_LEFT = "PEER_LEFT" // Gegner-Verbindung weg

    // Rollen (im MATCHED unter "role")
    const val ROLE_HOST  = "HOST"    // führt die Spiel-Autorität in-process
    const val ROLE_GUEST = "GUEST"   // reiner Client
}

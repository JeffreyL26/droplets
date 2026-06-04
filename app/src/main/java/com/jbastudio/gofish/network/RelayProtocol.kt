package com.jbastudio.gofish.network

/**
 * Steuer- und Routing-Nachrichten zwischen App und Relay-Server (Matchmaking +
 * N-Spieler-Weiterleitung). Diese liegen "über" dem eigentlichen Spielprotokoll
 * ([Protocol]).
 *
 * Ablauf: Client sendet [FIND] mit gewünschter Spielerzahl; das Relay bildet einen
 * Raum und weist Rollen zu ([MATCHED]). Danach:
 *  - **2-Spieler-Raum**: das Relay leitet alle Frames unverändert 1:1 an den
 *    anderen Peer weiter (Legacy — bestehender Betrieb bleibt unverändert).
 *  - **3–4-Spieler-Raum**: gerichtete Briefumschläge — der Host sendet [TO]
 *    (an einen bestimmten Spieler), Gast-Frames erreichen den Host als [FROM].
 *
 * Vollständige Spezifikation: `RELAY_PROTOCOL.md` im Repo-Root.
 */
object RelayProtocol {
    /** Standard-Pfad des WebSocket-Endpunkts auf dem Relay. */
    const val PATH = "/ws"

    // Client → Relay
    const val FIND = "FIND"          // {type:FIND, size:N} — Spiel für N Spieler suchen

    // Relay → Client
    const val MATCHED   = "MATCHED"  // {type:MATCHED, role, playerIndex, roomSize}
    const val PEER_LEFT = "PEER_LEFT" // {type:PEER_LEFT, playerIndex}

    // Rollen (im MATCHED unter "role")
    const val ROLE_HOST  = "HOST"    // führt die Spiel-Autorität in-process
    const val ROLE_GUEST = "GUEST"   // reiner Client

    // Gerichtetes Routing ab 3 Spielern (adressierte Briefumschläge):
    const val TO   = "TO"            // Host → Relay: {type:TO, to:Index, msg:<spiel>} → an Spieler[to]
    const val FROM = "FROM"          // Relay → Host: {type:FROM, from:Index, msg:<spiel>}

    // Feldnamen
    const val FIELD_SIZE  = "size"
    const val FIELD_ROLE  = "role"
    const val FIELD_INDEX = "playerIndex"
    const val FIELD_ROOM  = "roomSize"
    const val FIELD_TO    = "to"
    const val FIELD_FROM  = "from"
    const val FIELD_MSG   = "msg"
}

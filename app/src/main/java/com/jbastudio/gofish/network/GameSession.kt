package com.jbastudio.gofish.network

import org.json.JSONObject

/**
 * Gemeinsame Schnittstelle für alles, womit [com.jbastudio.gofish.GameActivity] und
 * [com.jbastudio.gofish.MainActivity] reden, um ein Spiel zu spielen.
 *
 * Sie kapselt die Sicht eines einzelnen Spielers: Nachrichten empfangen
 * ([onMessage]), nach einem Rang fragen ([sendAsk]) und die Verbindung beenden
 * ([disconnect]).
 *
 * Implementierungen:
 *  - [GameClient]        — LAN-Client (rohe TCP-Sockets)
 *  - [OnlineGameClient]  — Online-Spieler über das WebSocket-Relay
 *                          (übernimmt je nach zugewiesener Rolle die Host- oder Gast-Logik)
 */
interface GameSession {
    /** Anzeigename dieses Spielers (für Gewinner-Erkennung in der UI). */
    val playerName: String

    /** Vom Server vergebene Spieler-ID (0..3); -1 solange unbekannt. */
    val playerId: Int

    /** Eingehende Spielnachrichten (GAME_START, ASK_RESULT, OPPONENT_LEFT, …). */
    var onMessage: ((JSONObject) -> Unit)?

    /** Verbindung steht / Gegner gefunden — Sitzung kann gleich starten. */
    var onConnected: (() -> Unit)?

    /** Verbindungs-/Übertragungsfehler (rohe Meldung). */
    var onError: ((String) -> Unit)?

    /** Fragt einen Gegner ([targetId]) nach einem Rang; ohne Ziel = einziger Gegner (2-Spieler). */
    fun sendAsk(rank: String, targetId: Int? = null)

    /** Trennt die Verbindung / beendet die Sitzung. */
    fun disconnect()
}

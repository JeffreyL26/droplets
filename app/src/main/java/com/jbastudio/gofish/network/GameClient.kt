package com.jbastudio.gofish.network

import android.util.Log
import com.jbastudio.gofish.ui.components.AvatarChoice
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class GameClient(
    private val serverHost: String,
    val playerName: String,
    private val avatar: AvatarChoice = AvatarChoice()
) {
    companion object { private const val TAG = "GoFishClient" }

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    var playerId: Int = -1

    var onMessage:   ((JSONObject) -> Unit)? = null
    var onConnected: (() -> Unit)?           = null
    var onError:     ((String) -> Unit)?     = null

    fun connect() {
        Thread {
            try {
                socket = Socket(serverHost, Protocol.PORT)
                writer = PrintWriter(socket!!.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                // JOIN senden — inkl. Avatar-Wahl
                writer!!.println(
                    JSONObject()
                        .put("type", Protocol.JOIN)
                        .put("name", playerName)
                        .put("avatarKind",  avatar.kind.name)
                        .put("avatarColor", avatar.color.name)
                )
                onConnected?.invoke()

                // Nachrichten-Schleife
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    try {
                        val msg = JSONObject(line!!)
                        // Player-ID bei JOINED setzen
                        if (msg.getString("type") == Protocol.JOINED)
                            playerId = msg.getInt("yourId")
                        onMessage?.invoke(msg)
                    } catch (e: Exception) {
                        Log.e(TAG, "Parse-Fehler: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Verbindungsfehler", e)
                onError?.invoke(e.message ?: "Verbindung fehlgeschlagen")
            }
        }.start()
    }

    fun sendAsk(rank: String) {
        Thread {
            writer?.println(JSONObject().put("type", Protocol.ASK).put("rank", rank))
        }.start()
    }

    fun disconnect() { runCatching { socket?.close() } }
}
package com.jbastudio.gofish.network

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * LAN-Host: nimmt bis zu vier Spieler über rohe TCP-Sockets (Port [Protocol.PORT])
 * entgegen (Anzahl per [start]) und delegiert die gesamte Spiellogik an eine [GameAuthority].
 *
 * Der Server ist damit nur noch ein dünner Transport-Wrapper; die identische
 * Logik nutzt auch der Online-Host ([OnlineGameClient]) über das WebSocket-Relay.
 */
class GameServer {
    companion object { private const val TAG = "GoFishServer" }

    private val authority = GameAuthority()
    private val writers   = ConcurrentHashMap<Int, PrintWriter>()
    private val nextId    = AtomicInteger(0)
    private var serverSocket: ServerSocket? = null

    var onLog: ((String) -> Unit)? = null

    init {
        authority.onLog = { msg -> log(msg) }
        authority.send  = { pid, msg -> writers[pid]?.println(msg.toString()) }
    }

    fun start(expectedPlayers: Int = 2, nameFallbackPrefix: String = "Player") {
        authority.expectedPlayers = expectedPlayers
        authority.nameFallbackPrefix = nameFallbackPrefix
        Thread {
            try {
                serverSocket = ServerSocket(Protocol.PORT)
                log("Server läuft auf Port ${Protocol.PORT} … (erwarte $expectedPlayers Spieler)")
                // Bis zu vier Spieler akzeptieren (Anzahl vom Host vorgegeben)
                repeat(expectedPlayers) { serverSocket!!.accept().also { handleClient(it) } }
            } catch (e: Exception) {
                log("Server-Fehler: ${e.message}")
                Log.e(TAG, "Server-Fehler", e)
            }
        }.start()
    }

    private fun handleClient(socket: Socket) {
        Thread {
            var pid = -1
            try {
                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                pid = nextId.getAndIncrement()
                writers[pid] = writer

                // JOIN empfangen — inkl. Avatar (mit Defaults bei alten Clients)
                val join = JSONObject(reader.readLine() ?: return@Thread)
                authority.playerJoin(
                    pid,
                    join.getString("name"),
                    join.optString("avatarKind",  "FISH"),
                    join.optString("avatarColor", "SUN")
                )

                // Nachrichten-Schleife
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    try {
                        val msg = JSONObject(line!!)
                        if (msg.getString("type") == Protocol.ASK)
                            authority.playerAsk(
                                pid,
                                msg.getString("rank"),
                                if (msg.has("targetId")) msg.getInt("targetId") else null
                            )
                    } catch (e: Exception) {
                        log("Nachrichtenfehler von Spieler $pid: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Client-Handler-Fehler", e)
            } finally {
                // Spieler getrennt — verbleibende Mitspieler informieren und Sitzung schließen
                if (pid >= 0) {
                    writers.remove(pid)
                    authority.playerLeft(pid)
                    // Verbindungen schließen, damit auch Reader-Loops sauber enden
                    if (authority.isStarted()) runCatching { serverSocket?.close() }
                }
                runCatching { socket.close() }
            }
        }.start()
    }

    fun stop() { runCatching { serverSocket?.close() } }
    private fun log(msg: String) { Log.d(TAG, msg); onLog?.invoke(msg) }
}

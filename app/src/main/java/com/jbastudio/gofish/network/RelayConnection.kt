package com.jbastudio.gofish.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * Dünner WebSocket-Transport (OkHttp) zum Relay-Server.
 *
 * Stellt eine zeilen-/textbasierte Verbindung bereit. Die Callbacks laufen auf
 * OkHttps Reader-Thread (NICHT dem UI-Thread) — die UI muss selbst auf den
 * Main-Thread wechseln.
 */
class RelayConnection(private val url: String) {

    companion object { private const val TAG = "GoFishRelay" }

    private val client = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)        // hält NAT/Proxy-Verbindungen offen
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)     // 0 = kein Read-Timeout (WebSocket bleibt offen)
        .build()

    private var ws: WebSocket? = null
    private var closed = false

    var onOpen:   (() -> Unit)?            = null
    var onText:   ((String) -> Unit)?      = null
    /** Verbindung geschlossen oder fehlgeschlagen — Grund (falls bekannt). */
    var onClosed: ((String?) -> Unit)?     = null

    fun connect() {
        val request = try {
            Request.Builder().url(url).build()
        } catch (e: IllegalArgumentException) {
            // Ungültige URL (z. B. fehlendes ws://) — direkt als Fehler melden
            onClosed?.invoke("Ungültige Server-URL")
            return
        }
        ws = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                onOpen?.invoke()
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                onText?.invoke(text)
            }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                runCatching { webSocket.close(NORMAL_CLOSURE, null) }
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                fireClosed(reason.ifEmpty { null })
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket-Fehler", t)
                fireClosed(t.message ?: "Verbindung fehlgeschlagen")
            }
        })
    }

    fun send(text: String) {
        runCatching { ws?.send(text) }
    }

    fun close() {
        closed = true
        runCatching { ws?.close(NORMAL_CLOSURE, "bye") }
    }

    private fun fireClosed(reason: String?) {
        // nur einmal melden
        if (closed) return
        closed = true
        onClosed?.invoke(reason)
    }
}

private const val NORMAL_CLOSURE = 1000

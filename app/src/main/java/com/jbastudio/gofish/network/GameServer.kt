package com.jbastudio.gofish.network

import android.util.Log
import com.jbastudio.gofish.game.AskResult
import com.jbastudio.gofish.game.GameEngine
import com.jbastudio.gofish.model.Card
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class GameServer {
    companion object { private const val TAG = "GoFishServer" }

    private data class AvatarMeta(val kind: String, val color: String)

    private val engine      = GameEngine()
    private val writers     = ConcurrentHashMap<Int, PrintWriter>()
    private val avatars     = ConcurrentHashMap<Int, AvatarMeta>()
    private val nextId      = AtomicInteger(0)
    private val latch       = CountDownLatch(2)
    private val gameStarted = AtomicBoolean(false)
    private var serverSocket: ServerSocket? = null

    var onLog: ((String) -> Unit)? = null

    fun start() {
        Thread {
            try {
                serverSocket = ServerSocket(Protocol.PORT)
                log("Server läuft auf Port ${Protocol.PORT} …")
                // Genau 2 Spieler akzeptieren
                repeat(2) { serverSocket!!.accept().also { handleClient(it) } }
            } catch (e: Exception) {
                log("Server-Fehler: ${e.message}")
                Log.e(TAG, "Server-Fehler", e)
            }
        }.start()
    }

    private fun handleClient(socket: Socket) {
        Thread {
            var pid: Int = -1
            try {
                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                pid        = nextId.getAndIncrement()
                writers[pid] = writer

                // JOIN empfangen — inkl. Avatar (mit Defaults bei alten Clients)
                val join = JSONObject(reader.readLine() ?: return@Thread)
                val name = join.getString("name")
                val avKind  = join.optString("avatarKind",  "FISH")
                val avColor = join.optString("avatarColor", "SUN")
                avatars[pid] = AvatarMeta(avKind, avColor)
                log("Spieler $pid verbunden: $name ($avKind/$avColor)")
                synchronized(engine) { engine.addPlayer(pid, name) }

                writer.println(JSONObject().apply {
                    put("type", Protocol.JOINED)
                    put("yourId", pid)
                })

                latch.countDown()
                latch.await()   // warten bis beide Spieler da sind

                // Spiel genau einmal starten (CAS-gesichert)
                if (gameStarted.compareAndSet(false, true)) {
                    synchronized(engine) { engine.startGame() }
                    log("Spiel gestartet!")
                    sendGameStart()
                }

                // Nachrichten-Schleife
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    try {
                        val msg = JSONObject(line!!)
                        if (msg.getString("type") == Protocol.ASK)
                            processAsk(pid, msg.getString("rank"))
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
                    if (gameStarted.get()) {
                        val notice = JSONObject().put("type", Protocol.OPPONENT_LEFT).toString()
                        writers.values.toList().forEach { w ->
                            runCatching { w.println(notice); w.flush() }
                        }
                        // Verbindungen schließen, damit auch Reader-Loops sauber enden
                        runCatching { serverSocket?.close() }
                    }
                }
                runCatching { socket.close() }
            }
        }.start()
    }

    private fun processAsk(askerId: Int, rank: String) {
        val result: AskResult = synchronized(engine) {
            if (engine.gameOver || !engine.canAsk(askerId, rank)) return
            engine.processAsk(askerId, rank)
        }
        log("Spieler $askerId fragte nach $rank → ${result.cardsReceived.size} Karte(n)")
        broadcastResult(result)
    }

    private fun sendGameStart() {
        val p0 = engine.getPlayer(0)
        val p1 = engine.getPlayer(1)
        val a0 = avatars[0] ?: AvatarMeta("FISH", "SUN")
        val a1 = avatars[1] ?: AvatarMeta("FISH", "SUN")
        send(0, JSONObject().apply {
            put("type", Protocol.GAME_START)
            put("yourHand", cardsToJson(p0.hand))
            put("opponentName", p1.name)
            put("opponentAvatarKind",  a1.kind)
            put("opponentAvatarColor", a1.color)
            put("opponentHandSize", p1.hand.size)
            put("deckSize", engine.deckSize())
            put("yourTurn", engine.currentPlayerId == 0)
        })
        send(1, JSONObject().apply {
            put("type", Protocol.GAME_START)
            put("yourHand", cardsToJson(p1.hand))
            put("opponentName", p0.name)
            put("opponentAvatarKind",  a0.kind)
            put("opponentAvatarColor", a0.color)
            put("opponentHandSize", p0.hand.size)
            put("deckSize", engine.deckSize())
            put("yourTurn", engine.currentPlayerId == 1)
        })
    }

    private fun broadcastResult(r: AskResult) {
        val asker  = engine.getPlayer(r.askerId)
        val target = engine.opponentOf(r.askerId)

        // Asker erhält vollständige Informationen
        send(r.askerId, JSONObject().apply {
            put("type", Protocol.ASK_RESULT)
            put("rank", r.rank)
            put("askerIsYou", true)
            put("gotCards", r.cardsReceived.isNotEmpty())
            put("cardsReceived", cardsToJson(r.cardsReceived))
            put("wentFishing", r.wentFishing)
            r.drawnCard?.let { put("drawnCard", it.toJson()); put("drawnMatched", r.drawnCardMatchesRank) }
            put("newBooks", JSONArray(r.newBooksForAsker))
            put("yourHand", cardsToJson(asker.hand))
            put("yourBooks", JSONArray(asker.books))
            put("opponentHandSize", target.hand.size)
            put("opponentBooks", JSONArray(target.books))
            put("deckSize", engine.deckSize())
            put("yourTurn", r.nextPlayerId == r.askerId && !r.gameOver)
            put("gameOver", r.gameOver)
            if (r.gameOver) putGameOver(this, asker.books.size, target.books.size, r.winnerName)
        })

        // Target erhält eingeschränkte Informationen (gezogene Karte bleibt geheim)
        send(target.id, JSONObject().apply {
            put("type", Protocol.ASK_RESULT)
            put("rank", r.rank)
            put("askerIsYou", false)
            put("gotCards", r.cardsReceived.isNotEmpty())
            put("cardCount", r.cardsReceived.size)
            put("wentFishing", r.wentFishing)
            put("newBooks", JSONArray(r.newBooksForAsker))
            put("yourHand", cardsToJson(target.hand))
            put("yourBooks", JSONArray(target.books))
            put("opponentHandSize", asker.hand.size)
            put("opponentBooks", JSONArray(asker.books))
            put("deckSize", engine.deckSize())
            put("yourTurn", r.nextPlayerId == target.id && !r.gameOver)
            put("gameOver", r.gameOver)
            if (r.gameOver) putGameOver(this, target.books.size, asker.books.size, r.winnerName)
        })
    }

    private fun putGameOver(obj: JSONObject, myBks: Int, opBks: Int, winner: String?) {
        obj.put("myBooks", myBks)
        obj.put("opBooks", opBks)
        obj.put("winnerName", winner ?: "Unentschieden!")
    }

    private fun send(pid: Int, msg: JSONObject) = writers[pid]?.println(msg.toString())
    private fun cardsToJson(c: List<Card>) = JSONArray(c.map { it.toJson() })

    fun stop() { runCatching { serverSocket?.close() } }
    private fun log(msg: String) { Log.d(TAG, msg); onLog?.invoke(msg) }
}
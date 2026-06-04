package com.jbastudio.gofish

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import java.util.concurrent.ConcurrentHashMap

/**
 * Zentrale Verwaltung der Soundeffekte. Hält einen prozessweiten [SoundPool]
 * (analog zu [GameHolder]) mit den kurzen Spiel-Sounds sowie eine globale
 * Effekt-Lautstärke und einen Mute-Schalter, die ALLE Effekte betreffen.
 *
 * Zwei Wiedergabe-Wege:
 *  - [SoundPool] für kurze, latenzarme Effekte, die exakt zu einem Ereignis
 *    klingen müssen (Klick, Karten-Auswahl, „du bist dran", Buch, Go-Fish- und
 *    Angel-Sounds zur jeweiligen Animation).
 *  - [MediaPlayer] für die längeren End-Jingles [playWinner]/[playLoser]: Diese
 *    können den ~1 MB-Decodier-Grenzwert eines SoundPool-Samples überschreiten
 *    (und würden dort still ausfallen); als einmalige One-Shots am Spielende ist
 *    die etwas höhere Startlatenz unkritisch.
 *
 * Die kurzen Effekte:
 *  - [playBook]:       man komplettiert selbst ein Buch.
 *  - [playTurn]:       man ist am Zug.
 *  - [playClick]:      Tippen auf Menü-/Spiel-Buttons (außer Kartenauswahl).
 *  - [playGoFish]:     die „Go Fish"-Animation läuft.
 *  - [playSteal]:      man angelt 1–3 Karten vom Gegner (Sound je Anzahl).
 *  - [playCardSelect]: bei Auswahl einer Karte. Eine noch laufende Wiedergabe
 *    wird abgebrochen und neu gestartet (schnelles Durchtippen klingt sauber).
 *
 * Musik wird später getrennt ergänzt; deshalb regelt die Lautstärke hier
 * bewusst nur Soundeffekte.
 */
object GameSounds {

    private var pool: SoundPool? = null
    private var appContext: Context? = null

    // SoundPool-Samples (kurze, latenzarme Effekte)
    private var bookId = 0
    private var turnId = 0
    private var cardId = 0
    private var clickId = 0
    private var goFishId = 0
    private var steal1Id = 0
    private var steal2Id = 0
    private var steal3Id = 0

    /** Fertig geladene Sample-IDs (Laden ist asynchron). */
    private val loaded = ConcurrentHashMap.newKeySet<Int>()

    /** Zuletzt gestarteter Karten-Auswahl-Stream — wird bei erneutem Tippen gestoppt. */
    @Volatile private var cardStreamId = 0

    /** Aktiver End-Jingle (Sieg/Niederlage). Nur einer gleichzeitig. */
    private var oneShot: MediaPlayer? = null

    /** Globale Effekt-Lautstärke (0f..1f). */
    @Volatile
    var volume: Float = 1f
        private set

    /** Stummschaltung aller Effekte. */
    @Volatile
    var muted: Boolean = false
        private set

    /** Einmalige, idempotente Initialisierung. Lädt Prefs und die Sounds. */
    @Synchronized
    fun init(context: Context) {
        if (pool != null) return

        appContext = context.applicationContext

        val prefs = SoundPrefs(context)
        volume = prefs.loadVolume()
        muted  = prefs.loadMuted()

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val sp = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(attrs)
            .build()
        sp.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loaded.add(sampleId)
        }

        val appCtx = context.applicationContext
        bookId   = sp.load(appCtx, R.raw.book, 1)
        turnId   = sp.load(appCtx, R.raw.turn_indicator, 1)
        cardId   = sp.load(appCtx, R.raw.card_select, 1)
        clickId  = sp.load(appCtx, R.raw.click, 1)
        goFishId = sp.load(appCtx, R.raw.go_fish, 1)
        steal1Id = sp.load(appCtx, R.raw.one_card_angel, 1)
        steal2Id = sp.load(appCtx, R.raw.two_card_angel, 1)
        steal3Id = sp.load(appCtx, R.raw.three_card_angel, 1)
        pool = sp
    }

    private fun effectiveVolume(): Float = if (muted) 0f else volume.coerceIn(0f, 1f)

    private fun play(sampleId: Int): Int {
        val sp = pool ?: return 0
        if (muted || sampleId == 0 || sampleId !in loaded) return 0
        val v = effectiveVolume()
        if (v <= 0f) return 0
        return sp.play(sampleId, v, v, /* priority = */ 1, /* loop = */ 0, /* rate = */ 1f)
    }

    /** Buch komplettiert. */
    fun playBook() { play(bookId) }

    /** Man ist am Zug. */
    fun playTurn() { play(turnId) }

    /** Tippen auf einen Menü-/Spiel-Button (außer Kartenauswahl). */
    fun playClick() { play(clickId) }

    /** Die „Go Fish"-Animation läuft. */
    fun playGoFish() { play(goFishId) }

    /** Angel-Sound passend zur Anzahl der vom Gegner geangelten Karten (1..3). */
    fun playSteal(count: Int) {
        val id = when {
            count <= 1 -> steal1Id
            count == 2 -> steal2Id
            else       -> steal3Id
        }
        play(id)
    }

    /**
     * Karten-Auswahl. Bricht eine noch laufende Wiedergabe ab und startet neu,
     * sodass schnelles Durchtippen den Sound jeweils frisch beginnt.
     */
    fun playCardSelect() {
        val sp = pool ?: return
        if (cardStreamId != 0) sp.stop(cardStreamId)
        cardStreamId = play(cardId)
    }

    /** Sieg-Jingle (längerer One-Shot via MediaPlayer). */
    fun playWinner() { playOneShot(R.raw.winner) }

    /** Niederlage-Jingle (längerer One-Shot via MediaPlayer). */
    fun playLoser() { playOneShot(R.raw.loser) }

    /** Spielt einen längeren One-Shot; ein noch laufender wird vorher beendet. */
    private fun playOneShot(resId: Int) {
        val ctx = appContext ?: return
        if (muted) return
        val v = effectiveVolume()
        if (v <= 0f) return
        // vorherigen Jingle freigeben, damit nichts überlappt
        oneShot?.let { runCatching { it.release() } }
        oneShot = null
        val mp = MediaPlayer.create(ctx, resId) ?: return
        mp.setVolume(v, v)
        mp.setOnCompletionListener { p ->
            runCatching { p.release() }
            if (oneShot === p) oneShot = null
        }
        oneShot = mp
        mp.start()
    }

    /** Setzt die globale Effekt-Lautstärke (wirkt auch auf laufende Streams/Jingles). */
    fun setVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
        val v = effectiveVolume()
        if (cardStreamId != 0) pool?.setVolume(cardStreamId, v, v)
        oneShot?.let { runCatching { it.setVolume(v, v) } }
    }

    /** Schaltet alle Effekte stumm bzw. wieder laut. */
    fun setMuted(value: Boolean) {
        muted = value
        val v = effectiveVolume()
        if (value && cardStreamId != 0) pool?.stop(cardStreamId)
        oneShot?.let { runCatching { it.setVolume(v, v) } }
    }
}

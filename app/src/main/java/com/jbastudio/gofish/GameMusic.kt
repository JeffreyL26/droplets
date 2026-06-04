package com.jbastudio.gofish

import android.content.Context
import android.media.MediaPlayer

/**
 * Hintergrundmusik (Main-Theme), die über alle Menüs hinweg durchgehend läuft.
 * Prozessweiter Halter analog zu [GameSounds]/[GameHolder], mit eigenem
 * Lautstärke- und Stumm-Regler, der unabhängig von den Soundeffekten arbeitet
 * (Persistenz über [MusicPrefs]).
 *
 * Lebenszyklus: [MainActivity] ruft in onResume [resume] und in onPause [pause].
 * Dadurch spielt die Musik im Haupt- und in allen Untermenüs (Compose-Dialoge
 * pausieren die Activity nicht) und schweigt, sobald die [GameActivity] im
 * Vordergrund ist oder die App in den Hintergrund geht.
 *
 * Die Wiedergabe beginnt bei [START_OFFSET_MS] (0:29) und springt am Ende wieder
 * dorthin zurück — der Intro-Teil davor wird also nur einmal übersprungen und
 * nicht bei jeder Schleife wiederholt.
 */
object GameMusic {

    /** Startposition im Track (0:29) — zugleich Rücksprungpunkt beim Loopen. */
    private const val START_OFFSET_MS = 29_000

    /** Standard-Lautstärke (Musik etwas leiser als Effekte). */
    private const val DEFAULT_VOLUME = 0.5f

    private var appContext: Context? = null
    private var player: MediaPlayer? = null

    /** true, solange ein Menü im Vordergrund ist (Musik soll laufen). */
    private var inForeground = false

    /** Globale Musik-Lautstärke (0f..1f). */
    @Volatile
    var volume: Float = DEFAULT_VOLUME
        private set

    /** Stummschaltung der Musik. */
    @Volatile
    var muted: Boolean = false
        private set

    /** Lädt die persistierten Einstellungen. Mehrfachaufruf ist unschädlich. */
    @Synchronized
    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = MusicPrefs(context)
        volume = prefs.loadVolume()
        muted  = prefs.loadMuted()
        player?.let { applyVolume(it) }
    }

    /** Menü im Vordergrund — Musik (weiter)spielen. */
    @Synchronized
    fun resume() {
        inForeground = true
        ensurePlayer()
        updatePlayback()
    }

    /** Menü verlassen (Spielstart oder App im Hintergrund) — Musik anhalten. */
    @Synchronized
    fun pause() {
        inForeground = false
        player?.let { if (it.isPlaying) runCatching { it.pause() } }
    }

    /** Erstellt den Player beim ersten Bedarf und positioniert ihn auf 0:29. */
    private fun ensurePlayer() {
        if (player != null) return
        val ctx = appContext ?: return
        val mp = MediaPlayer.create(ctx, R.raw.main_theme) ?: return
        mp.isLooping = false   // manueller Loop auf START_OFFSET (Intro nicht wiederholen)
        mp.setOnCompletionListener {
            runCatching { it.seekTo(START_OFFSET_MS); it.start() }
        }
        mp.setOnErrorListener { _, _, _ ->
            runCatching { player?.release() }
            player = null
            true
        }
        runCatching { mp.seekTo(START_OFFSET_MS) }
        applyVolume(mp)
        player = mp
    }

    /** Startet bzw. pausiert je nach Vordergrund-Status und Lautstärke/Stumm. */
    private fun updatePlayback() {
        val p = player ?: return
        val active = inForeground && !muted && volume > 0f
        runCatching {
            if (active && !p.isPlaying) p.start()
            else if (!active && p.isPlaying) p.pause()
        }
        applyVolume(p)
    }

    private fun applyVolume(p: MediaPlayer) {
        val v = if (muted) 0f else volume.coerceIn(0f, 1f)
        runCatching { p.setVolume(v, v) }
    }

    /** Setzt die Musik-Lautstärke (analog zum Soundeffekt-Regler). */
    @Synchronized
    fun setVolume(value: Float) {
        volume = value.coerceIn(0f, 1f)
        updatePlayback()
    }

    /** Schaltet die Musik stumm bzw. wieder laut (analog zum Soundeffekt-Regler). */
    @Synchronized
    fun setMuted(value: Boolean) {
        muted = value
        updatePlayback()
    }
}

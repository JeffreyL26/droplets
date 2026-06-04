package com.jbastudio.gofish

import android.content.Context

/**
 * Persistiert die Musik-Einstellungen (Lautstärke + Stummschaltung) — bewusst
 * getrennt von [SoundPrefs], damit Hintergrundmusik und Soundeffekte unabhängig
 * voneinander geregelt werden.
 */
class MusicPrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_music", Context.MODE_PRIVATE)

    fun loadVolume(): Float = prefs.getFloat(KEY_VOLUME, DEFAULT_VOLUME).coerceIn(0f, 1f)
    fun loadMuted(): Boolean = prefs.getBoolean(KEY_MUTED, false)

    fun saveVolume(volume: Float) {
        prefs.edit().putFloat(KEY_VOLUME, volume.coerceIn(0f, 1f)).apply()
    }

    fun saveMuted(muted: Boolean) {
        prefs.edit().putBoolean(KEY_MUTED, muted).apply()
    }

    private companion object {
        const val KEY_VOLUME = "music_volume"
        const val KEY_MUTED  = "music_muted"
        const val DEFAULT_VOLUME = 0.5f
    }
}

package com.jbastudio.gofish

import android.content.Context

/**
 * Persistiert die Sound-Einstellungen (Effekt-Lautstärke + Stummschaltung) über
 * App-Restarts hinweg. Bewusst nur für Soundeffekte gedacht — ein separater
 * Musik-Regler folgt später und bekommt eine eigene Persistenz.
 */
class SoundPrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_sound", Context.MODE_PRIVATE)

    fun loadVolume(): Float = prefs.getFloat(KEY_VOLUME, DEFAULT_VOLUME).coerceIn(0f, 1f)
    fun loadMuted(): Boolean = prefs.getBoolean(KEY_MUTED, false)

    fun saveVolume(volume: Float) {
        prefs.edit().putFloat(KEY_VOLUME, volume.coerceIn(0f, 1f)).apply()
    }

    fun saveMuted(muted: Boolean) {
        prefs.edit().putBoolean(KEY_MUTED, muted).apply()
    }

    private companion object {
        const val KEY_VOLUME = "sfx_volume"
        const val KEY_MUTED  = "sfx_muted"
        const val DEFAULT_VOLUME = 0.8f
    }
}

package com.jbastudio.gofish

import android.content.Context

/** Persistiert den zuletzt gewählten Spielernamen über App-Restarts hinweg. */
class NamePrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_name", Context.MODE_PRIVATE)

    fun load(): String = prefs.getString(KEY_NAME, "") ?: ""

    fun save(name: String) {
        prefs.edit().putString(KEY_NAME, name).apply()
    }

    private companion object {
        const val KEY_NAME = "player_name"
    }
}

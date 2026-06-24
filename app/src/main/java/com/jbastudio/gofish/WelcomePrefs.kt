package com.jbastudio.gofish

import android.content.Context

/** Merkt sich, ob der Erstlauf-Willkommensdialog schon gezeigt wurde. */
class WelcomePrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_welcome", Context.MODE_PRIVATE)

    fun seen(): Boolean = prefs.getBoolean(KEY_SEEN, false)

    fun setSeen(value: Boolean) {
        prefs.edit().putBoolean(KEY_SEEN, value).apply()
    }

    private companion object {
        const val KEY_SEEN = "seen_welcome"
    }
}

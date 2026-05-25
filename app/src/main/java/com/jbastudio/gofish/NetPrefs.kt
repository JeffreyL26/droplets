package com.jbastudio.gofish

import android.content.Context

/** Persistiert die Adresse des Online-Relay-Servers über App-Restarts hinweg. */
class NetPrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_net", Context.MODE_PRIVATE)

    fun loadRelayUrl(): String = prefs.getString(KEY_RELAY_URL, "") ?: ""

    fun saveRelayUrl(url: String) {
        prefs.edit().putString(KEY_RELAY_URL, url).apply()
    }

    private companion object {
        const val KEY_RELAY_URL = "relay_url"
    }
}

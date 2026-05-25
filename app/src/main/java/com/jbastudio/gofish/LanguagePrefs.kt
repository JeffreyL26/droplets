package com.jbastudio.gofish

import android.content.Context
import com.jbastudio.gofish.i18n.Language
import com.jbastudio.gofish.i18n.languageFromTag

/** Persistiert die gewählte Sprache über App-Restarts hinweg. Standard: Deutsch. */
class LanguagePrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_lang", Context.MODE_PRIVATE)

    fun load(): Language = languageFromTag(prefs.getString(KEY_LANG, Language.DE.tag))

    fun save(language: Language) {
        prefs.edit().putString(KEY_LANG, language.tag).apply()
    }

    private companion object {
        const val KEY_LANG = "language"
    }
}

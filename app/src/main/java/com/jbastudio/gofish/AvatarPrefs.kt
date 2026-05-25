package com.jbastudio.gofish

import android.content.Context
import com.jbastudio.gofish.ui.components.AvatarChoice
import com.jbastudio.gofish.ui.components.AvatarColor
import com.jbastudio.gofish.ui.components.AvatarKind

/** Persistiert die Avatar-Wahl des Spielers über App-Restarts hinweg. */
class AvatarPrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_avatar", Context.MODE_PRIVATE)

    fun load(): AvatarChoice {
        val kindName  = prefs.getString(KEY_KIND,  AvatarKind.FISH.name)  ?: AvatarKind.FISH.name
        val colorName = prefs.getString(KEY_COLOR, AvatarColor.SUN.name) ?: AvatarColor.SUN.name
        val kind  = runCatching { AvatarKind.valueOf(kindName)   }.getOrDefault(AvatarKind.FISH)
        val color = runCatching { AvatarColor.valueOf(colorName) }.getOrDefault(AvatarColor.SUN)
        return AvatarChoice(kind, color)
    }

    fun save(choice: AvatarChoice) {
        prefs.edit()
            .putString(KEY_KIND,  choice.kind.name)
            .putString(KEY_COLOR, choice.color.name)
            .apply()
    }

    private companion object {
        const val KEY_KIND  = "avatar_kind"
        const val KEY_COLOR = "avatar_color"
    }
}

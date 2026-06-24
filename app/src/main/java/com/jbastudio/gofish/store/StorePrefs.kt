package com.jbastudio.gofish.store

import android.content.Context
import com.jbastudio.gofish.ui.components.AvatarKind

/**
 * Persistiert Kauf- und Unlock-Zustände über App-Restarts hinweg.
 * Folgt dem Muster der bestehenden *Prefs-Klassen (SharedPreferences, MODE_PRIVATE).
 *
 * Schlüssel-Schema:
 *   "skin_<KIND_NAME>"  → "UNLOCKED_AD" | "UNLOCKED_IAP"  (sonst LOCKED, FREE = nicht gespeichert)
 *   "ad_free"           → Boolean
 */
class StorePrefs(context: Context) {
    private val prefs = context
        .applicationContext
        .getSharedPreferences("gofish_store", Context.MODE_PRIVATE)

    // ── Ad-Free ──────────────────────────────────────────────────────────

    fun isAdFree(): Boolean = prefs.getBoolean(KEY_AD_FREE, false)

    fun setAdFree(value: Boolean) {
        prefs.edit().putBoolean(KEY_AD_FREE, value).apply()
    }

    // ── Skin-Unlock ───────────────────────────────────────────────────────

    /**
     * Gibt die gespeicherte Zugangsstufe zurück.
     * Skins mit defaultTier == FREE geben immer FREE zurück.
     * Gesperrte Skins ohne Eintrag geben LOCKED zurück.
     */
    fun tierFor(kind: AvatarKind): AvatarSkinTier {
        if (kind.defaultTier == AvatarSkinTier.FREE) return AvatarSkinTier.FREE
        val stored = prefs.getString(skinKey(kind), null)
        return when (stored) {
            AvatarSkinTier.UNLOCKED_AD.name  -> AvatarSkinTier.UNLOCKED_AD
            AvatarSkinTier.UNLOCKED_IAP.name -> AvatarSkinTier.UNLOCKED_IAP
            else                             -> AvatarSkinTier.LOCKED
        }
    }

    fun unlockByAd(kind: AvatarKind) {
        if (kind.defaultTier == AvatarSkinTier.FREE) return
        prefs.edit().putString(skinKey(kind), AvatarSkinTier.UNLOCKED_AD.name).apply()
    }

    fun unlockByIap(kind: AvatarKind) {
        if (kind.defaultTier == AvatarSkinTier.FREE) return
        prefs.edit().putString(skinKey(kind), AvatarSkinTier.UNLOCKED_IAP.name).apply()
    }

    /** Dev-only: alle Skins freischalten. */
    fun unlockAllDev() {
        val editor = prefs.edit()
        AvatarKind.values()
            .filter { it.defaultTier != AvatarSkinTier.FREE }
            .forEach { editor.putString(skinKey(it), AvatarSkinTier.UNLOCKED_IAP.name) }
        editor.apply()
    }

    /** Dev-only: alle Skins + Ad-Free zurücksetzen. */
    fun resetAllDev() {
        prefs.edit().clear().apply()
    }

    private fun skinKey(kind: AvatarKind) = "skin_${kind.name}"

    private companion object {
        const val KEY_AD_FREE = "ad_free"
    }
}

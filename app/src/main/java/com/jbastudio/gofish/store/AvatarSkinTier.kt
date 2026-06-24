package com.jbastudio.gofish.store

/**
 * Zugangsstufe eines Avatar-Skins.
 *
 * FREE          — immer verfügbar, keine Aktion nötig
 * LOCKED        — noch nicht freigeschaltet (zeigt Lock-Badge)
 * UNLOCKED_AD   — durch Rewarded-Video freigeschaltet (in StorePrefs gespeichert)
 * UNLOCKED_IAP  — durch Kauf freigeschaltet (in StorePrefs gespeichert)
 */
enum class AvatarSkinTier {
    FREE,
    LOCKED,
    UNLOCKED_AD,
    UNLOCKED_IAP
}

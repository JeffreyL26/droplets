package com.jbastudio.gofish.store

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jbastudio.gofish.ui.components.AvatarKind

/**
 * Prozessweite Quelle der Wahrheit für Kauf- und Unlock-Zustände.
 * Wird einmalig in MainActivity.onCreate() mit init(context) initialisiert.
 *
 * Die Laufzeit-Zustände liegen als Compose-Snapshot-State vor: Wer in einer
 * @Composable `isAdFree` oder `tierFor(kind)` liest, wird bei Änderungen
 * (Unlock per Video/Kauf, Dev-Reset) automatisch neu zusammengesetzt. Persistenz
 * läuft parallel über [StorePrefs]. Snapshot-State ist threadsafe schreibbar —
 * auch aus Billing-/Ad-Callbacks.
 */
object StoreManager {

    private var prefs: StorePrefs? = null

    // Beobachtbarer Laufzeit-Spiegel der Prefs.
    private var adFreeState by mutableStateOf(false)
    private val tierState   = mutableStateMapOf<AvatarKind, AvatarSkinTier>()

    /** Einmalige, idempotente Initialisierung. */
    fun init(context: Context) {
        if (prefs != null) return
        val p = StorePrefs(context)
        prefs = p
        adFreeState = p.isAdFree()
        AvatarKind.values().forEach { tierState[it] = p.tierFor(it) }
    }

    // ── Ad-Free ──────────────────────────────────────────────────────────

    val isAdFree: Boolean get() = adFreeState

    fun setAdFree(value: Boolean) {
        adFreeState = value
        prefs?.setAdFree(value)
    }

    // ── Skin-Tier ─────────────────────────────────────────────────────────

    fun tierFor(kind: AvatarKind): AvatarSkinTier =
        tierState[kind] ?: kind.defaultTier

    fun isUnlocked(kind: AvatarKind): Boolean =
        tierFor(kind) != AvatarSkinTier.LOCKED

    fun unlockByAd(kind: AvatarKind) {
        if (kind.defaultTier == AvatarSkinTier.FREE) return
        prefs?.unlockByAd(kind)
        tierState[kind] = AvatarSkinTier.UNLOCKED_AD
    }

    fun unlockByIap(kind: AvatarKind) {
        if (kind.defaultTier == AvatarSkinTier.FREE) return
        prefs?.unlockByIap(kind)
        tierState[kind] = AvatarSkinTier.UNLOCKED_IAP
    }

    // ── Dev-Options ───────────────────────────────────────────────────────

    fun devUnlockAll() {
        prefs?.unlockAllDev()
        AvatarKind.values()
            .filter { it.defaultTier != AvatarSkinTier.FREE }
            .forEach { tierState[it] = AvatarSkinTier.UNLOCKED_IAP }
    }

    fun devResetAll() {
        prefs?.resetAllDev()
        adFreeState = false
        AvatarKind.values().forEach { tierState[it] = it.defaultTier }
    }
}

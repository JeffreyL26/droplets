package com.jbastudio.gofish.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.jbastudio.gofish.store.StoreManager

/**
 * Prozessweiter Wrapper für AdMob-Ads.
 *
 * Interstitial: wird nach einem Spiel angezeigt — aber nur:
 *   - wenn der Nutzer NICHT Ad-Free ist
 *   - maximal einmal alle INTERSTITIAL_MIN_INTERVAL_MS (30 Sekunden Mindestabstand
 *     zwischen zwei Impressions, de facto ~1× pro 2 Spiele)
 *
 * Rewarded: wird auf Anfrage angezeigt (Avatar-Unlock).
 *
 * WICHTIG: Vor dem Release Test-IDs durch echte AdMob-IDs ersetzen.
 *   Test-Interstitial: ca-app-pub-3940256099942544/1033173712
 *   Test-Rewarded:     ca-app-pub-3940256099942544/5224354917
 */
object AdManager {

    private const val TAG = "AdManager"

    // ── Ad-Unit-IDs (Test) — vor Release ersetzen ─────────────────────────
    private const val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val REWARDED_ID     = "ca-app-pub-3940256099942544/5224354917"

    // Mindestabstand zwischen Interstitials in Millisekunden (30 Sek.)
    private const val INTERSTITIAL_MIN_INTERVAL_MS = 30_000L

    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var lastInterstitialTimeMs = 0L

    /** Einmalig in MainActivity.onCreate() aufrufen. */
    fun init(context: Context) {
        MobileAds.initialize(context) {
            preloadInterstitial(context)
            preloadRewarded(context)
        }
    }

    // ── Interstitial ──────────────────────────────────────────────────────

    /**
     * Zeigt das Interstitial, wenn:
     *   - Nutzer ist nicht Ad-Free
     *   - Mindestabstand seit letzter Einblendung eingehalten
     *   - eine geladene Anzeige bereit ist
     *
     * [onComplete] wird immer aufgerufen (mit oder ohne Anzeige), damit der
     * aufrufende Code (GameOver → Hauptmenü) weiterläuft.
     */
    fun showInterstitialIfEligible(activity: Activity, onComplete: () -> Unit) {
        val now = System.currentTimeMillis()
        val eligible = !StoreManager.isAdFree
                && (now - lastInterstitialTimeMs) >= INTERSTITIAL_MIN_INTERVAL_MS
                && interstitial != null

        if (!eligible) {
            onComplete()
            return
        }

        interstitial!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitial = null
                preloadInterstitial(activity)
                onComplete()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.w(TAG, "Interstitial show failed: ${error.message}")
                interstitial = null
                preloadInterstitial(activity)
                onComplete()
            }
        }

        lastInterstitialTimeMs = now
        interstitial!!.show(activity)
    }

    private fun preloadInterstitial(context: Context) {
        val req = AdRequest.Builder().build()
        InterstitialAd.load(context, INTERSTITIAL_ID, req, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad }
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.w(TAG, "Interstitial load failed: ${error.message}")
                interstitial = null
            }
        })
    }

    // ── Rewarded ──────────────────────────────────────────────────────────

    /**
     * Zeigt ein Rewarded-Video. [onRewarded] wird aufgerufen, wenn der Nutzer
     * das Video vollständig geschaut hat. [onComplete] immer (auch bei Abbruch).
     */
    fun showRewarded(
        activity: Activity,
        onRewarded: () -> Unit,
        onComplete: () -> Unit
    ) {
        val ad = rewarded ?: run {
            onComplete()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewarded = null
                preloadRewarded(activity)
                onComplete()
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewarded = null
                preloadRewarded(activity)
                onComplete()
            }
        }

        ad.show(activity) { _ -> onRewarded() }
    }

    fun isRewardedReady(): Boolean = rewarded != null

    private fun preloadRewarded(context: Context) {
        val req = AdRequest.Builder().build()
        RewardedAd.load(context, REWARDED_ID, req, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { rewarded = ad }
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.w(TAG, "Rewarded load failed: ${error.message}")
                rewarded = null
            }
        })
    }
}

package com.jbastudio.gofish.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.jbastudio.gofish.BuildConfig
import com.jbastudio.gofish.store.StoreManager
import java.util.concurrent.atomic.AtomicBoolean

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
 * DSGVO/EU: Vor dem ersten Laden echter Anzeigen MUSS der UMP-Consent-Flow
 * durchlaufen werden — siehe initWithConsent(). Das Mobile Ads SDK wird erst
 * initialisiert, wenn Anzeigen erlaubt sind (Einwilligung erteilt oder nicht
 * erforderlich).
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

    // Garantiert, dass MobileAds.initialize() prozessweit nur einmal läuft —
    // egal über welchen Consent-Pfad (Erst-Einwilligung oder Returning-User).
    private val mobileAdsInitialized = AtomicBoolean(false)

    // Nach initWithConsent() verfügbar; für den Datenschutzoptionen-Einstieg.
    private var consentInformation: ConsentInformation? = null

    /**
     * DSGVO-konformer Start (EU/EWR): Führt zuerst den UMP-Consent-Flow aus und
     * initialisiert das Mobile Ads SDK ERST, wenn Anzeigen erlaubt sind
     * (Einwilligung erteilt ODER nicht erforderlich). Vor der Einwilligung werden
     * keine Ad-Anfragen gestellt.
     *
     * Aus MainActivity.onCreate() aufrufen (statt des früheren init()):
     *     AdManager.initWithConsent(this)
     *
     * Es wird eine Activity benötigt, da das Consent-Formular als Dialog über ihr
     * erscheint. Der Aufruf ist idempotent.
     */
    fun initWithConsent(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        this.consentInformation = consentInformation

        val paramsBuilder = ConsentRequestParameters.Builder()
        debugConsentSettings(activity)?.let { paramsBuilder.setConsentDebugSettings(it) }
        val params = paramsBuilder.build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // Consent-Status aktualisiert → Formular zeigen, falls erforderlich.
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form error ${formError.errorCode}: ${formError.message}")
                    }
                    // Formular abgeschlossen oder nicht nötig → Ads laden, wenn erlaubt.
                    if (consentInformation.canRequestAds()) {
                        ensureMobileAdsInitialized(activity)
                    }
                }
            },
            { requestError ->
                Log.w(TAG, "Consent info update failed ${requestError.errorCode}: ${requestError.message}")
            }
        )

        // Returning User: gültige Einwilligung aus einer früheren Sitzung liegt
        // bereits vor → SDK sofort starten, ohne auf den Netzwerk-Callback zu warten.
        if (consentInformation.canRequestAds()) {
            ensureMobileAdsInitialized(activity)
        }
    }

    /**
     * True, wenn Google für diesen Nutzer einen dauerhaften
     * „Datenschutzoptionen"-Einstieg verlangt (EU/EWR). Dann sollte z. B. in den
     * Einstellungen ein Button showPrivacyOptionsForm() anbieten.
     */
    fun isPrivacyOptionsRequired(): Boolean =
        consentInformation?.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Zeigt das Datenschutzoptionen-Formular, damit Nutzer ihre Einwilligung
     * jederzeit ändern/widerrufen können (von Google für EU/EWR gefordert).
     * Erst nach initWithConsent() sinnvoll. Noch nicht in der UI verdrahtet — als
     * Einstieg aus den Einstellungen vorgesehen (s. MONETARISIERUNG.md).
     */
    fun showPrivacyOptionsForm(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.w(TAG, "Privacy options form error ${formError.errorCode}: ${formError.message}")
            }
        }
    }

    /**
     * Debug-Hilfe für den Consent-Test: erzwingt im Debug-Build die EWR-Region,
     * damit das Formular auch außerhalb der EU erscheint. Im Release-Build null
     * (echte Geolokalisierung durch Google).
     *
     * Logcat gibt beim ersten Lauf den Test-Geräte-Hash aus
     * ("addTestDeviceHashedId(...)"); diesen unten eintragen, damit das Gerät als
     * Testgerät behandelt wird.
     */
    private fun debugConsentSettings(context: Context): ConsentDebugSettings? {
        if (!BuildConfig.DEBUG) return null
        return ConsentDebugSettings.Builder(context)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            // .addTestDeviceHashedId("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")  // aus Logcat
            .build()
    }

    /**
     * Initialisiert das Mobile Ads SDK und lädt die ersten Anzeigen vor.
     * Idempotent (AtomicBoolean) — läuft prozessweit garantiert nur einmal.
     * Wird ausschließlich nach erteiltem oder nicht erforderlichem Consent aufgerufen.
     */
    private fun ensureMobileAdsInitialized(context: Context) {
        if (!mobileAdsInitialized.compareAndSet(false, true)) return
        val appContext = context.applicationContext
        MobileAds.initialize(appContext) {
            preloadInterstitial(appContext)
            preloadRewarded(appContext)
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

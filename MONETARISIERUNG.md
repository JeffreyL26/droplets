# Monetarisierung — Umsetzungsnotiz

Umgesetzt nach `../CLAUDE.md` (Plan), mit Anpassungen an den aktuellen Code-Stand
und einigen Verbesserungen. **Test-IDs sind aktiv** — echte Monetarisierung erst
nach dem manuellen Play-Console-/AdMob-Setup (s. u.).

## Neue Dateien
- `store/AvatarSkinTier.kt` — Enum FREE/LOCKED/UNLOCKED_AD/UNLOCKED_IAP
- `store/StorePrefs.kt` — Persistenz (SharedPreferences `gofish_store`)
- `store/StoreManager.kt` — prozessweite Quelle der Wahrheit
- `store/BillingManager.kt` — Google Play Billing 7.x (IAP)
- `ads/AdManager.kt` — AdMob Interstitial + Rewarded (Test-IDs)

## Abweichungen vom Plan / Verbesserungen
1. **`buildConfig = true`** in `app/build.gradle.kts` ergänzt — sonst gäbe es
   `BuildConfig.DEBUG` nicht (AGP 9 generiert es nicht mehr automatisch); der
   Dev-Options-Button hätte nicht kompiliert.
2. **StoreManager ist Compose-observierbar** (`mutableStateOf`/`mutableStateMapOf`).
   Die UI (Avatar-Kacheln, Ad-Free-Status) aktualisiert sich nach Unlock/Kauf
   automatisch — kein manuelles State-Hoisting nötig.
3. **Billing-Callbacks laufen auf den Main-Thread** (`Handler(Looper.getMainLooper())`),
   da die Library auf einem Hintergrund-Thread zurückruft. Zusätzlich löst der
   Start-Restore (`queryAndRestorePurchases`) keinen Kauf-Callback mehr aus.
4. **Interstitial nur am „Zum Hauptmenü"-Button** — eigener `onExitToMenu`-Callback.
   Zurück-Taste und Sitzungsende zeigen bewusst keine Werbung (UX/Play-Policy).
5. **Dependencies über den Version-Catalog** (`gradle/libs.versions.toml`) statt
   direkter Strings — passend zum Projektstil.
6. Dev-Options-Texte bewusst **nicht lokalisiert** (nur Debug-Build).

## Vor dem ersten echten Release (Checkliste)
- [ ] **DSGVO/Consent (UMP):** Für EU/DE ein Consent-Formular über die Google
      *User Messaging Platform* (`com.google.android.ump:user-messaging-platform`)
      einbinden und VOR dem Laden echter Ads anzeigen. **Fehlt aktuell bewusst**
      (Test-Ads brauchen keinen Consent).
- [ ] AdMob-App-ID in `AndroidManifest.xml` durch die echte ID ersetzen.
- [ ] Ad-Unit-IDs in `ads/AdManager.kt` (INTERSTITIAL_ID, REWARDED_ID) ersetzen.
- [ ] Play-Console-Produkte anlegen und auf **Aktiv** setzen:
      `gofish_ad_free`, `gofish_skin_whale`, `gofish_skin_dolphin`,
      `gofish_skin_puffer`, `gofish_skin_starfish`.
- [ ] Kauf mit echtem Testgerät + Play-Test-Account verifizieren.
- [ ] `isMinifyEnabled = true` für den Release-Build erwägen (ProGuard-Regeln für
      Billing + AdMob liegen bereits in `app/proguard-rules.pro`).
- [ ] DevOptions erscheinen nur im Debug-Build (`BuildConfig.DEBUG`) — kurz prüfen.

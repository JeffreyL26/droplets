package com.jbastudio.gofish

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jbastudio.gofish.i18n.Language
import com.jbastudio.gofish.i18n.LocalTexts
import com.jbastudio.gofish.i18n.Texts
import com.jbastudio.gofish.i18n.textsFor
import com.jbastudio.gofish.network.GameClient
import com.jbastudio.gofish.network.GameServer
import com.jbastudio.gofish.network.OnlineGameClient
import com.jbastudio.gofish.ui.components.Avatar
import com.jbastudio.gofish.ui.components.AvatarChoice
import com.jbastudio.gofish.ui.components.AvatarColor
import com.jbastudio.gofish.ui.components.AvatarKind
import com.jbastudio.gofish.ui.components.BubblePanel
import com.jbastudio.gofish.ui.components.FishMascot
import com.jbastudio.gofish.ui.components.GameIcon
import com.jbastudio.gofish.ui.components.GameIconKind
import com.jbastudio.gofish.ui.components.iconKindForEmoji
import com.jbastudio.gofish.ui.components.OceanBackground
import com.jbastudio.gofish.ui.theme.*
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : ComponentActivity() {

    /** Verbindungs-Modus der laufenden Aktion. */
    enum class Mode { HOST, JOIN, ONLINE }

    /** Welcher Bildschirm gerade sichtbar ist. */
    enum class Screen { MENU, LOCAL, ONLINE }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        GameSounds.init(this)
        GameMusic.init(this)
        val myIp = localIpAddress()
        val avatarPrefs = AvatarPrefs(this)
        val netPrefs    = NetPrefs(this)
        val langPrefs   = LanguagePrefs(this)
        val soundPrefs  = SoundPrefs(this)
        val musicPrefs  = MusicPrefs(this)
        val namePrefs   = NamePrefs(this)
        // Initialer Avatar aus Prefs in den Holder spielen
        GameHolder.myAvatar = avatarPrefs.load()

        setContent {
            GoFishTheme {
                // Gespeicherter Name → beim nächsten Öffnen Begrüßung statt leerem Feld.
                var name    by remember { mutableStateOf(namePrefs.load()) }
                var hostIp  by remember { mutableStateOf("") }
                var status  by remember { mutableStateOf("") }
                var isError by remember { mutableStateOf(false) }
                var busy    by remember { mutableStateOf(false) }
                var mode    by remember { mutableStateOf<Mode?>(null) }
                var avatarChoice  by remember { mutableStateOf(GameHolder.myAvatar) }
                var showAvatarDlg by remember { mutableStateOf(false) }
                var screen         by remember { mutableStateOf(Screen.MENU) }
                // Optionaler, vom Nutzer gesetzter Server (z. B. zum Testen). Leer = Standard.
                var serverOverride by remember { mutableStateOf(netPrefs.loadRelayUrl()) }
                var showServerDlg  by remember { mutableStateOf(false) }
                var showSettingsDlg by remember { mutableStateOf(false) }
                // Sound-Einstellungen (Quelle der Wahrheit ist GameSounds; hier nur gespiegelt)
                var soundVolume by remember { mutableStateOf(GameSounds.volume) }
                var soundMuted  by remember { mutableStateOf(GameSounds.muted) }
                var musicVolume by remember { mutableStateOf(GameMusic.volume) }
                var musicMuted  by remember { mutableStateOf(GameMusic.muted) }
                // Gewünschte Gegnerzahl (1–3). Standard 1 = klassisches 2-Spieler-Spiel.
                var opponentCount by remember { mutableStateOf(1) }
                // Aktuelle Sprache (live umschaltbar über die Flaggen im Hauptmenü)
                var lang by remember { mutableStateOf(langPrefs.load()) }
                val T: Texts = textsFor(lang)
                // Token-Container: jeder Connect-Versuch bekommt eine eigene ID;
                // wird sie inkrementiert, sind alle laufenden Callbacks "stale" und werden ignoriert.
                val attempt = remember { intArrayOf(0) }

                val cancel: () -> Unit = {
                    val cancelMsg = when (mode) {
                        Mode.HOST   -> T.hostingCancelled
                        Mode.JOIN   -> T.searchCancelled
                        Mode.ONLINE -> T.searchCancelled
                        else        -> T.operationCancelled
                    }
                    attempt[0]++                       // invalidiert offene Callbacks
                    GameHolder.client?.apply {
                        onConnected = null
                        onError     = null
                        onMessage   = null
                        disconnect()
                    }
                    GameHolder.server?.apply {
                        onLog = null
                        stop()
                    }
                    GameHolder.client = null
                    GameHolder.server = null
                    busy    = false
                    isError = false
                    mode    = null
                    status  = cancelMsg
                }

                // System-Zurück: aktive Verbindung abbrechen, sonst zurück ins Hauptmenü
                BackHandler(enabled = busy || screen != Screen.MENU) {
                    when {
                        busy -> cancel()
                        else -> { status = ""; isError = false; screen = Screen.MENU }
                    }
                }

                // Navigation vom Hauptmenü in einen Unterbildschirm
                val goTo: (Screen) -> Unit = { target ->
                    status = ""; isError = false; screen = target
                }
                // Zurück ins Hauptmenü (bricht ggf. eine laufende Aktion ab)
                val backToMenu: () -> Unit = {
                    if (busy) cancel() else { status = ""; isError = false }
                    screen = Screen.MENU
                }

                // Online-Matchmaking SOFORT starten — keine Eingabe nötig.
                // Adresse: optionaler Override, sonst die fest hinterlegte Standard-Adresse.
                // anyGame = true → „Beliebiges Spiel" (Relay sucht/füllt einen offenen Raum).
                val startMatchmaking: (Boolean) -> Unit = { anyGame ->
                    val url = serverOverride.ifBlank { DEFAULT_RELAY_URL }.trim()
                    isError = false
                    screen  = Screen.ONLINE
                    if (url.isEmpty()) {
                        // Noch kein Server hinterlegt → Hinweis statt Suche
                        busy    = false
                        mode    = null
                        status  = T.serverNotSetUp
                        isError = true
                    } else {
                        // Namenlose Spieler werden vom Host durchnummeriert → hier leer lassen.
                        val finalName = name.trim()
                        val myToken = ++attempt[0]
                        busy   = true
                        mode   = Mode.ONLINE
                        status = T.searchingOpponent
                        findOnlineMatch(
                            relayUrl = url,
                            name     = finalName,
                            desiredPlayers = if (anyGame) 0 else opponentCount + 1,
                            nameFallbackPrefix = T.defaultNamePlayer,
                            token    = myToken,
                            texts    = T,
                            onUpdate = { s, err ->
                                if (attempt[0] == myToken) runOnUiThread {
                                    status  = s
                                    isError = err
                                    if (err) { busy = false; mode = null }
                                }
                            },
                            onSessionStart = {
                                if (attempt[0] == myToken) { busy = false; mode = null; status = "" }
                            }
                        )
                    }
                }

                CompositionLocalProvider(LocalTexts provides T) {

                if (showAvatarDlg) {
                    AvatarSelectionDialog(
                        currentChoice = avatarChoice,
                        onChoiceChange = { newChoice ->
                            avatarChoice = newChoice
                            avatarPrefs.save(newChoice)
                            GameHolder.myAvatar = newChoice
                        },
                        onClose = { showAvatarDlg = false }
                    )
                }

                if (showSettingsDlg) {
                    SettingsDialog(
                        version = "Pre-Launch MP240626.3",
                        soundVolume = soundVolume,
                        soundMuted  = soundMuted,
                        onSoundVolumeChange = { v ->
                            soundVolume = v
                            GameSounds.setVolume(v)
                            soundPrefs.saveVolume(v)
                        },
                        onToggleSoundMute = {
                            val newMuted = !soundMuted
                            soundMuted = newMuted
                            GameSounds.setMuted(newMuted)
                            soundPrefs.saveMuted(newMuted)
                        },
                        musicVolume = musicVolume,
                        musicMuted  = musicMuted,
                        onMusicVolumeChange = { v ->
                            musicVolume = v
                            GameMusic.setVolume(v)
                            musicPrefs.saveVolume(v)
                        },
                        onToggleMusicMute = {
                            val newMuted = !musicMuted
                            musicMuted = newMuted
                            GameMusic.setMuted(newMuted)
                            musicPrefs.saveMuted(newMuted)
                        },
                        onClose = { showSettingsDlg = false }
                    )
                }

                if (showServerDlg) {
                    ServerDialog(
                        current = serverOverride,
                        defaultUrl = DEFAULT_RELAY_URL,
                        onSave = { url ->
                            serverOverride = url.trim()
                            netPrefs.saveRelayUrl(url.trim())
                            showServerDlg = false
                        },
                        onUseDefault = {
                            serverOverride = ""
                            netPrefs.saveRelayUrl("")
                            showServerDlg = false
                        },
                        onClose = { showServerDlg = false }
                    )
                }

                when (screen) {
                  Screen.MENU -> MainMenuScreen(
                      name          = name,
                      onNameChange  = { name = it; namePrefs.save(it) },
                      avatar        = avatarChoice,
                      onAvatarClick = { showAvatarDlg = true },
                      onFindGame    = { goTo(Screen.ONLINE) },
                      onLocalHost   = { goTo(Screen.LOCAL) },
                      language      = lang,
                      onLanguageChange = { lang = it; langPrefs.save(it) },
                      onSettings    = { showSettingsDlg = true }
                  )

                  Screen.ONLINE -> OnlineScreen(
                      status           = status,
                      isError          = isError,
                      busy             = busy,
                      serverConfigured = serverOverride.ifBlank { DEFAULT_RELAY_URL }.isNotBlank(),
                      opponentCount    = opponentCount,
                      onOpponentCountChange = { opponentCount = it },
                      onBack           = backToMenu,
                      onCancel         = cancel,
                      onFindAny        = { startMatchmaking(true) },
                      onRetry          = { startMatchmaking(false) },
                      onEditServer     = { showServerDlg = true }
                  )

                  Screen.LOCAL -> LobbyScreen(
                    name           = name,
                    onNameChange   = { name = it; namePrefs.save(it) },
                    hostIp         = hostIp,
                    onHostIpChange = { hostIp = it },
                    myIp           = myIp,
                    status         = status,
                    isError        = isError,
                    busy           = busy,
                    mode           = mode,
                    avatar         = avatarChoice,
                    onAvatarClick  = { showAvatarDlg = true },
                    onBack         = backToMenu,
                    onCancel       = cancel,
                    opponentCount  = opponentCount,
                    onOpponentCountChange = { opponentCount = it },
                    onHost = {
                        // Namenlose Spieler werden vom Host durchnummeriert → hier leer lassen.
                        val finalName = name.trim()
                        val myToken = ++attempt[0]
                        busy    = true
                        isError = false
                        mode    = Mode.HOST
                        status  = T.startingServer
                        // Server-Logs nur ins Logcat (nicht in die lokalisierte Status-Anzeige)
                        val server = GameServer().also { GameHolder.server = it }
                        server.start(opponentCount + 1, T.defaultNamePlayer)   // Host zählt mit → Gesamtspielerzahl
                        connectAsClient(
                            host        = "127.0.0.1",
                            name        = finalName,
                            token       = myToken,
                            currentMode = Mode.HOST,
                            texts       = T,
                            onUpdate = { s, err ->
                                if (attempt[0] == myToken) runOnUiThread {
                                    status  = s
                                    isError = err
                                    if (err) { busy = false; mode = null }
                                }
                            },
                            onSessionStart = {
                                if (attempt[0] == myToken) {
                                    busy = false; mode = null; status = ""
                                }
                            }
                        )
                    },
                    onJoin = {
                        // Namenlose Spieler werden vom Host durchnummeriert → hier leer lassen.
                        val finalName = name.trim()
                        val ip = hostIp.trim()
                        if (ip.isEmpty()) {
                            status = T.enterHostIp
                            isError = true
                        } else {
                            val myToken = ++attempt[0]
                            busy    = true
                            isError = false
                            mode    = Mode.JOIN
                            status  = T.connectingTo(ip)
                            connectAsClient(
                                host        = ip,
                                name        = finalName,
                                token       = myToken,
                                currentMode = Mode.JOIN,
                                texts       = T,
                                onUpdate = { s, err ->
                                    if (attempt[0] == myToken) runOnUiThread {
                                        status  = s
                                        isError = err
                                        if (err) { busy = false; mode = null }
                                    }
                                },
                                onSessionStart = {
                                    if (attempt[0] == myToken) {
                                        busy = false; mode = null; status = ""
                                    }
                                }
                            )
                        }
                    }
                  )
                } // when (screen)
                } // CompositionLocalProvider
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Menü im Vordergrund → Main-Theme (weiter)spielen.
        GameMusic.resume()
    }

    override fun onPause() {
        super.onPause()
        // Menü verlassen (Spielstart oder App im Hintergrund) → Musik anhalten.
        GameMusic.pause()
    }

    private fun connectAsClient(
        host: String,
        name: String,
        token: Int,
        currentMode: Mode,
        texts: Texts,
        onUpdate: (String, Boolean) -> Unit,
        onSessionStart: () -> Unit
    ) {
        val client = GameClient(host, name, GameHolder.myAvatar).also { GameHolder.client = it }
        client.onConnected = { onUpdate(texts.connectedWaiting, false) }
        client.onError     = { err -> onUpdate(friendlyError(err, currentMode, texts), true) }
        client.onMessage   = { msg ->
            when (msg.optString("type")) {
                "GAME_START" -> {
                    GameHolder.gameStartMsg = msg
                    runOnUiThread {
                        onSessionStart()
                        startActivity(Intent(this, GameActivity::class.java))
                    }
                }
                // Lobby-Fortschritt beim Warten auf weitere Spieler (3–4-Spieler-Host)
                "LOBBY" -> runOnUiThread {
                    onUpdate(texts.lobbyWaiting(msg.optInt("joined", 1), msg.optInt("expected", 2)), false)
                }
            }
        }
        client.connect()
    }

    /**
     * Startet das Online-Matchmaking über das Relay.
     *
     * Der [OnlineGameClient] verbindet sich, sucht einen Gegner und übernimmt je
     * nach zugewiesener Rolle die Host- oder Gast-Logik. Nach außen verhält er sich
     * wie ein normaler Client (GAME_START → [GameActivity] starten).
     */
    private fun findOnlineMatch(
        relayUrl: String,
        name: String,
        desiredPlayers: Int,
        nameFallbackPrefix: String,
        token: Int,
        texts: Texts,
        onUpdate: (String, Boolean) -> Unit,
        onSessionStart: () -> Unit
    ) {
        val client = OnlineGameClient(relayUrl, name, GameHolder.myAvatar, desiredPlayers, nameFallbackPrefix)
            .also { GameHolder.client = it }
        client.onConnected = { onUpdate(texts.opponentFound, false) }
        client.onError     = { err -> onUpdate(friendlyError(err, Mode.ONLINE, texts), true) }
        client.onMessage   = { msg ->
            if (msg.getString("type") == "GAME_START") {
                GameHolder.gameStartMsg = msg
                runOnUiThread {
                    onSessionStart()
                    startActivity(Intent(this, GameActivity::class.java))
                }
            }
        }
        client.connect()
    }

    /** Übersetzt rohe Socket-/IO-Meldungen in spielerfreundliche, lokalisierte Texte. */
    private fun friendlyError(raw: String, mode: Mode, t: Texts): String {
        if (raw == OnlineGameClient.ERR_MULTI_UNSUPPORTED) return t.errOnlineMultiUnavailable
        val r = raw.lowercase()
        return when {
            r.contains("socket closed")
                    || r.contains("socket is closed")
                    || r.contains("connection abort")
                    || r.contains("connection reset")
                    || r.contains("eof") ->
                when (mode) {
                    Mode.HOST   -> t.errHostingEnded
                    Mode.JOIN   -> t.errConnectionLost
                    Mode.ONLINE -> t.errServerConnLost
                }
            r.contains("unable to resolve host") || r.contains("nodename nor servname")
                    || r.contains("ungültige server-url") ->
                t.errInvalidServer
            r.contains("connection refused") || r.contains("econnrefused") ->
                if (mode == Mode.ONLINE) t.errServerUnreachableRelay
                else t.errServerUnreachableIp
            r.contains("timeout") || r.contains("etimedout") ->
                t.errTimeout
            r.contains("network is unreachable") || r.contains("enetunreach") ->
                t.errNoNetwork
            r.contains("no route to host") || r.contains("ehostunreach") ->
                t.errNoRoute
            r.contains("permission denied") ->
                t.errPermission
            else -> t.errGeneric(raw)
        }
    }

    /**
     * Liefert die für andere LAN-Spieler erreichbare IP-Adresse.
     *
     * Strategie:
     *  1. ConnectivityManager fragen, welches Netzwerk WLAN-Transport hat,
     *     und die zugehörige IPv4-Adresse zurückgeben.
     *  2. Wenn das nichts liefert (Hotspot-Modus, alte Geräte usw.), die
     *     NetworkInterfaces durchgehen und WLAN-/AP-Interfaces bevorzugen.
     *
     * Wichtig: Wir picken NICHT mehr einfach die erste verfügbare IP — sonst
     * landet eine mobile Daten- oder VPN-Adresse vorn und ist von außen nicht
     * erreichbar.
     */
    @Suppress("DEPRECATION")
    private fun localIpAddress(): String {
        // 1. WLAN-Netzwerk gezielt über ConnectivityManager
        try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            for (network in cm.allNetworks) {
                val caps = cm.getNetworkCapabilities(network) ?: continue
                if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) continue
                val props = cm.getLinkProperties(network) ?: continue
                val v4 = props.linkAddresses
                    .map { it.address }
                    .firstOrNull { it is Inet4Address && !it.isLoopbackAddress }
                if (v4 != null) {
                    v4.hostAddress?.let { return it }
                }
            }
        } catch (_: Exception) { /* ignorieren, Fallback unten */ }

        // 2. Fallback: NetworkInterfaces filtern (Hotspot/AP, ältere Wege)
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
                .toList()
                .filter { it.isUp && !it.isLoopback && !it.isVirtual }

            // Reihenfolge der Bevorzugung
            val preferred =
                interfaces.firstOrNull { it.name.startsWith("wlan") }
                ?: interfaces.firstOrNull { it.name.startsWith("ap")   }    // Hotspot
                ?: interfaces.firstOrNull { it.name.startsWith("eth")  }    // ethernet/Adapter
                ?: interfaces.firstOrNull()

            preferred?.inetAddresses?.toList()
                ?.filterIsInstance<Inet4Address>()
                ?.firstOrNull { !it.isLoopbackAddress && !it.isLinkLocalAddress }
                ?.hostAddress
                ?.let { return it }
        } catch (_: Exception) { /* ignorieren */ }

        return ""   // unbekannt → wird bei der Anzeige lokalisiert
    }
}

// ═════════════════════════════════════════════════════════════════════════
//  Hauptmenü
// ═════════════════════════════════════════════════════════════════════════

@Composable
private fun MainMenuScreen(
    name: String,
    onNameChange: (String) -> Unit,
    avatar: AvatarChoice,
    onAvatarClick: () -> Unit,
    onFindGame: () -> Unit,
    onLocalHost: () -> Unit,
    language: Language,
    onLanguageChange: (Language) -> Unit,
    onSettings: () -> Unit
) {
    val t = LocalTexts.current
    OceanBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text  = "Go Fish!",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 52.sp, fontWeight = FontWeight.Black
                ),
                color = DeepSea,
                textAlign = TextAlign.Center
            )
            Text(
                text = t.tagline,
                style = MaterialTheme.typography.titleMedium,
                color = SoftSeaText
            )
            Spacer(Modifier.height(8.dp))

            // Maskottchen-Reihe
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FishMascot(
                    modifier = Modifier.size(width = 70.dp, height = 50.dp),
                    body = LavenderDeep, bodyDeep = LavenderDeep,
                    facingRight = true
                )
                FishMascot(
                    modifier = Modifier.size(width = 110.dp, height = 80.dp),
                    body = CoralPink, bodyDeep = CoralDeep,
                    facingRight = true
                )
                FishMascot(
                    modifier = Modifier.size(width = 80.dp, height = 60.dp),
                    body = SunYellow, bodyDeep = SunDeep,
                    facingRight = false
                )
            }

            Spacer(Modifier.height(16.dp))

            // Name + Avatar (für beide Spielmodi verfügbar)
            PlayerCard(
                name          = name,
                onNameChange  = onNameChange,
                avatar        = avatar,
                onAvatarClick = onAvatarClick,
                enabled       = true
            )

            Spacer(Modifier.height(22.dp))

            // Zwei Wege ins Spiel
            PastelButton(
                text      = t.findGame,
                emoji     = "🌐",
                enabled   = true,
                container = SeafoamGreen,
                onClick   = onFindGame
            )
            Spacer(Modifier.height(14.dp))
            PastelButton(
                text      = t.hostLocal,
                emoji     = "🏠",
                enabled   = true,
                container = Lavender,
                onClick   = onLocalHost
            )
        }

        // Sprachauswahl unten rechts. Volle Breite + linker Freiraum (72dp) für
        // den Einstellungs-Button, damit die FlowRow bei Bedarf rechtsbündig
        // umbrechen kann, ohne ihn zu überlappen.
        LanguageSelector(
            current = language,
            onSelect = onLanguageChange,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .systemBarsPadding()
                .fillMaxWidth()
                .padding(start = 72.dp, end = 16.dp, bottom = 16.dp)
        )

        // Einstellungen unten links
        SettingsButton(
            onClick = onSettings,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .systemBarsPadding()
                .padding(16.dp)
        )
    }
}

/** Eigenes Zahnrad-Icon (Vektorgrafik) unten links im Hauptmenü. */
@Composable
private fun SettingsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val t = LocalTexts.current
    Surface(
        onClick = { GameSounds.playClick(); onClick() },
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = Foam.copy(alpha = 0.85f),
        contentColor = DeepSea,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = t.settingsTitle,
                tint = DeepSea,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

/** Flaggen-Sprachauswahl (Deutsch / Englisch / Spanisch / Französisch / Mandarin / Tagalog). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageSelector(
    current: Language,
    onSelect: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    // FlowRow statt Row: Bei zu wenig Breite (schmale Geräte) bricht die
    // Flaggenreihe automatisch um, statt mit dem Einstellungs-Button zu
    // kollidieren. Flaggen bleiben rechtsbündig; neue Reihen wachsen nach oben
    // (Container ist unten verankert).
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FlagButton(R.drawable.flag_de, selected = current == Language.DE) { onSelect(Language.DE) }
        FlagButton(R.drawable.flag_en, selected = current == Language.EN) { onSelect(Language.EN) }
        FlagButton(R.drawable.flag_es, selected = current == Language.ES) { onSelect(Language.ES) }
        FlagButton(R.drawable.flag_fr, selected = current == Language.FR) { onSelect(Language.FR) }
        FlagButton(R.drawable.flag_zh, selected = current == Language.ZH) { onSelect(Language.ZH) }
        FlagButton(R.drawable.flag_tl, selected = current == Language.TL) { onSelect(Language.TL) }
    }
}

@Composable
private fun FlagButton(flagRes: Int, selected: Boolean, onClick: () -> Unit) {
    Image(
        painter = painterResource(flagRes),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .size(width = 44.dp, height = 30.dp)
            .clip(RoundedCornerShape(7.dp))
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) DeepSea else DeepSea.copy(alpha = 0.30f),
                shape = RoundedCornerShape(7.dp)
            )
            .alpha(if (selected) 1f else 0.55f)
            .clickable { GameSounds.playClick(); onClick() }
    )
}

// ═════════════════════════════════════════════════════════════════════════
//  Online-Bildschirm (Matchmaking über das Relay)
// ═════════════════════════════════════════════════════════════════════════

/**
 * Fest hinterlegte Adresse des Online-Relay-Servers — analog zu Clash Royale,
 * wo die Server-Adresse in der App steckt und der Spieler nichts eingibt.
 *
 * HIER nach dem Deployen die öffentliche Adresse eintragen, z. B.:
 *   "wss://gofish-relay.onrender.com/ws"
 *
 * Leer = noch nicht eingerichtet (App zeigt dann einen Hinweis).
 */
private const val DEFAULT_RELAY_URL = "wss://gofish-relay.onrender.com/ws"

@Composable
private fun OnlineScreen(
    status: String,
    isError: Boolean,
    busy: Boolean,
    serverConfigured: Boolean,
    opponentCount: Int,
    onOpponentCountChange: (Int) -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onFindAny: () -> Unit,
    onRetry: () -> Unit,
    onEditServer: () -> Unit
) {
    val t = LocalTexts.current
    OceanBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hauptmenü bleibt während der Suche aktiv: onBack (= backToMenu)
            // bricht eine laufende Suche ab und kehrt zum Menü zurück.
            BackRow(text = t.mainMenu, enabled = true, onBack = onBack)

            Text(
                text  = t.findGameTitle,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp, fontWeight = FontWeight.Black
                ),
                color = DeepSea,
                textAlign = TextAlign.Center
            )
            Text(
                text = t.onlineSubtitle,
                style = MaterialTheme.typography.titleMedium,
                color = SoftSeaText,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            when {
                busy -> {
                    // Suche läuft — Maskottchen + Status + Abbrechen
                    BubblePanel(
                        modifier = Modifier.fillMaxWidth(),
                        background = SunYellow.copy(alpha = 0.92f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FishMascot(
                                modifier = Modifier.size(width = 110.dp, height = 80.dp),
                                body = SeafoamGreen, bodyDeep = SeafoamDeep,
                                facingRight = true
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = status.ifEmpty { t.searchingOpponent },
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = DeepSea,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                t.waitingForPlayer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                textAlign = TextAlign.Center,
                                color = SoftSeaText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(16.dp))
                            PastelButton(
                                text = t.cancelSearch,
                                emoji = "✋",
                                enabled = true,
                                container = CoralDeep,
                                onClick = onCancel
                            )
                        }
                    }
                }

                !serverConfigured -> {
                    // Kein Server hinterlegt → Setup-Hinweis
                    BubblePanel(
                        modifier = Modifier.fillMaxWidth(),
                        background = CoralPink.copy(alpha = 0.92f)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                t.serverNotConfiguredTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = DeepSea
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                t.serverNotConfiguredBody,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeepSea
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    PastelButton(
                        text = t.setServer,
                        emoji = "🛰️",
                        enabled = true,
                        container = SeafoamGreen,
                        onClick = onEditServer
                    )
                }

                else -> {
                    // Standard-Aktion: direkt in ein beliebiges offenes Spiel.
                    // Größer & andersfarbig (Koralle) als die Gegnerzahl-Auswahl darunter.
                    PastelButton(
                        text = t.anyGame,
                        emoji = "🎲",
                        enabled = true,
                        container = CoralPink,
                        onClick = onFindAny
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = t.orPickCount,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftSeaText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    // Alternativ: feste Gegnerzahl wählen und gezielt einen solchen Raum suchen
                    OpponentCountSelector(
                        count = opponentCount,
                        onCountChange = onOpponentCountChange
                    )
                    Spacer(Modifier.height(12.dp))
                    PastelButton(
                        text = t.findGameTitle,
                        emoji = "🔎",
                        enabled = true,
                        container = SeafoamGreen,
                        onClick = onRetry
                    )
                    if (status.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        BubblePanel(
                            modifier = Modifier.fillMaxWidth(),
                            background = if (isError) CoralPink.copy(alpha = 0.92f)
                                         else         Lavender.copy(alpha = 0.85f)
                        ) {
                            Text(
                                text = status,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                textAlign = TextAlign.Center,
                                color = DeepSea,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    // Dezente Option (Tests / eigener Server)
                    TextButton(onClick = { GameSounds.playClick(); onEditServer() }) {
                        Text(t.changeServer, color = SoftSeaText)
                    }
                }
            }
        }
    }
}

/** Optionaler Server-Override (Tests / eigener Server). Leer = Standard-Server der App. */
@Composable
private fun ServerDialog(
    current: String,
    defaultUrl: String,
    onSave: (String) -> Unit,
    onUseDefault: () -> Unit,
    onClose: () -> Unit
) {
    val t = LocalTexts.current
    var text by remember { mutableStateOf(current) }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BubblePanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            background = Foam,
            cornerRadius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(t.serverAddressTitle, style = MaterialTheme.typography.headlineLarge, color = DeepSea)
                Spacer(Modifier.height(6.dp))
                Text(
                    t.serverDialogBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftSeaText,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                PastelTextField(
                    value = text,
                    onChange = { text = it },
                    placeholder = "wss://dein-server.com/ws"
                )
                if (defaultUrl.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        t.defaultServerLabel(defaultUrl),
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftSeaText
                    )
                }
                Spacer(Modifier.height(20.dp))
                PastelButton(
                    text = t.applyBtn,
                    emoji = "✓",
                    enabled = text.isNotBlank(),
                    container = SeafoamGreen,
                    onClick = { onSave(text) }
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { GameSounds.playClick(); onUseDefault() }) {
                    Text(t.useDefaultBtn, color = SoftSeaText)
                }
            }
        }
    }
}

/** Einstellungen / Info: Sound-Regler, Version, Herausgeber und eine kleine Widmung. */
@Composable
private fun SettingsDialog(
    version: String,
    soundVolume: Float,
    soundMuted: Boolean,
    onSoundVolumeChange: (Float) -> Unit,
    onToggleSoundMute: () -> Unit,
    musicVolume: Float,
    musicMuted: Boolean,
    onMusicVolumeChange: (Float) -> Unit,
    onToggleMusicMute: () -> Unit,
    onClose: () -> Unit
) {
    val t = LocalTexts.current
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BubblePanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            background = Foam,
            cornerRadius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(t.settingsTitle, style = MaterialTheme.typography.headlineLarge, color = DeepSea)

                Spacer(Modifier.height(22.dp))
                Text(
                    text = version,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepSea
                )

                Spacer(Modifier.height(22.dp))
                SoundSetting(
                    label          = t.soundLabel,
                    volume         = soundVolume,
                    muted          = soundMuted,
                    onVolumeChange = onSoundVolumeChange,
                    onToggleMute   = onToggleSoundMute
                )

                Spacer(Modifier.height(16.dp))
                SoundSetting(
                    label          = t.musicLabel,
                    volume         = musicVolume,
                    muted          = musicMuted,
                    onVolumeChange = onMusicVolumeChange,
                    onToggleMute   = onToggleMusicMute
                )

                Spacer(Modifier.height(18.dp))
                Text(
                    text = t.publishedBy,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftSeaText
                )
                Text(
                    text = "jba~team",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepSea
                )

                Spacer(Modifier.height(28.dp))
                Text(
                    text = "For my love, Bianka",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic),
                    color = CoralDeep
                )

                Spacer(Modifier.height(22.dp))
                PastelButton(
                    text = t.backBtn,
                    emoji = "⬅️",
                    enabled = true,
                    container = SeafoamGreen,
                    onClick = onClose
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════
//  Sound-Regler (eigenständig — Musik folgt später separat)
// ═════════════════════════════════════════════════════════════════════════

/**
 * Eigenständiger Lautstärke-Regler für Soundeffekte: klassisches
 * Lautsprecher-Symbol (Antippen = stumm/laut) + „Sound"-Titel + Schieberegler,
 * der alle Effekte gemeinsam regelt. Bewusst in sich geschlossen, damit später
 * ein analoger „Musik"-Regler danebengestellt werden kann.
 */
@Composable
private fun SoundSetting(
    label: String,
    volume: Float,
    muted: Boolean,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Klassisches Sound-Symbol — Antippen schaltet alle Effekte stumm/laut
        Surface(
            onClick = onToggleMute,
            shape = CircleShape,
            color = OceanTop.copy(alpha = 0.6f),
            contentColor = DeepSea,
            modifier = Modifier.size(46.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                GameIcon(
                    if (muted) GameIconKind.SPEAKER_MUTED else GameIconKind.SPEAKER,
                    modifier = Modifier.size(26.dp),
                    tint = DeepSea
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = DeepSea
            )
            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                enabled = !muted,
                colors = SliderDefaults.colors(
                    thumbColor                 = SeafoamDeep,
                    activeTrackColor           = SeafoamDeep,
                    inactiveTrackColor         = OceanDeep.copy(alpha = 0.40f),
                    disabledThumbColor         = OceanDeep.copy(alpha = 0.55f),
                    disabledActiveTrackColor   = OceanDeep.copy(alpha = 0.30f),
                    disabledInactiveTrackColor = OceanDeep.copy(alpha = 0.20f)
                )
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════
//  Gemeinsame Bausteine
// ═════════════════════════════════════════════════════════════════════════

/** Karte mit Namensfeld + Avatar-Auswahl — auf Hauptmenü, Online- und Lokal-Screen. */
@Composable
private fun PlayerCard(
    name: String,
    onNameChange: (String) -> Unit,
    avatar: AvatarChoice,
    onAvatarClick: () -> Unit,
    enabled: Boolean
) {
    val t = LocalTexts.current
    // Ist bereits ein Name gesetzt, zeigt die Karte „Hallo [Name]!"; Antippen (Bleistift)
    // wechselt zurück ins Eingabefeld. Ohne Namen (frisch) startet sie direkt im Feld.
    var editing by remember { mutableStateOf(name.isBlank()) }
    BubblePanel(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            if (editing) {
                SectionLabel(t.yourName)
                Spacer(Modifier.height(6.dp))
                PastelTextField(
                    value = name,
                    onChange = onNameChange,
                    placeholder = t.namePlaceholder,
                    capWords = true,
                    enabled = enabled,
                    // „Fertig" auf der Tastatur → zurück zur Begrüßung (sofern ein Name da ist)
                    onImeDone = { if (name.isNotBlank()) editing = false }
                )
            } else {
                // Begrüßung mit Bleistift-Icon — Antippen führt zurück ins Eingabefeld.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(OceanTop.copy(alpha = 0.6f))
                        .clickable(enabled = enabled) { GameSounds.playClick(); editing = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = t.hello(name.trim()),
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepSea,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(10.dp))
                    GameIcon(GameIconKind.PENCIL, modifier = Modifier.size(20.dp), tint = DeepSea)
                }
            }
            Spacer(Modifier.height(14.dp))
            AvatarChooserRow(
                avatar  = avatar,
                enabled = enabled,
                onClick = onAvatarClick
            )
        }
    }
}

/** Zurück-Leiste oben links (führt zurück ins Hauptmenü). */
@Composable
private fun BackRow(text: String, enabled: Boolean, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = { GameSounds.playClick(); onBack() },
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            color = Foam.copy(alpha = 0.85f),
            contentColor = DeepSea
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("‹", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DeepSea)
                Spacer(Modifier.width(6.dp))
                Text(text, style = MaterialTheme.typography.labelLarge, color = DeepSea)
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun LobbyScreen(
    name: String,
    onNameChange: (String) -> Unit,
    hostIp: String,
    onHostIpChange: (String) -> Unit,
    myIp: String,
    status: String,
    isError: Boolean,
    busy: Boolean,
    mode: MainActivity.Mode?,
    avatar: AvatarChoice,
    onAvatarClick: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    opponentCount: Int,
    onOpponentCountChange: (Int) -> Unit,
    onHost: () -> Unit,
    onJoin: () -> Unit
) {
    val t = LocalTexts.current
    OceanBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Zurück ins Hauptmenü
            BackRow(text = t.mainMenu, enabled = !busy, onBack = onBack)

            // Titel
            Text(
                text  = t.localTitle,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp, fontWeight = FontWeight.Black
                ),
                color = DeepSea,
                textAlign = TextAlign.Center
            )
            Text(
                text = t.localSubtitle,
                style = MaterialTheme.typography.titleMedium,
                color = SoftSeaText,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))

            // Maskottchen-Reihe
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FishMascot(
                    modifier = Modifier.size(width = 70.dp, height = 50.dp),
                    body = LavenderDeep, bodyDeep = LavenderDeep,
                    facingRight = true
                )
                FishMascot(
                    modifier = Modifier.size(width = 110.dp, height = 80.dp),
                    body = CoralPink, bodyDeep = CoralDeep,
                    facingRight = true
                )
                FishMascot(
                    modifier = Modifier.size(width = 80.dp, height = 60.dp),
                    body = SunYellow, bodyDeep = SunDeep,
                    facingRight = false
                )
            }

            Spacer(Modifier.height(16.dp))

            // Karte: Spieler-Name + Avatar-Wahl
            PlayerCard(
                name          = name,
                onNameChange  = onNameChange,
                avatar        = avatar,
                onAvatarClick = onAvatarClick,
                enabled       = !busy
            )

            Spacer(Modifier.height(14.dp))

            // Karte: Hosten
            BubblePanel(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionLabel(t.hostGame)
                        Spacer(Modifier.weight(1f))
                        GameIcon(GameIconKind.HOME, modifier = Modifier.size(22.dp), tint = DeepSea)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        t.yourIp(myIp.ifBlank { t.unknownIp }),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftSeaText
                    )
                    Spacer(Modifier.height(12.dp))
                    OpponentCountSelector(
                        count = opponentCount,
                        onCountChange = onOpponentCountChange,
                        enabled = !busy
                    )
                    Spacer(Modifier.height(12.dp))
                    PastelButton(
                        text = t.startServer,
                        emoji = "🎣",
                        enabled = !busy,
                        container = SeafoamGreen,
                        onClick = onHost
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Karte: Beitreten
            BubblePanel(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionLabel(t.joinGame)
                        Spacer(Modifier.weight(1f))
                        GameIcon(GameIconKind.WAVE, modifier = Modifier.size(22.dp), tint = DeepSea)
                    }
                    Spacer(Modifier.height(6.dp))
                    PastelTextField(
                        value = hostIp,
                        onChange = onHostIpChange,
                        placeholder = t.hostIpPlaceholder,
                        enabled = !busy
                    )
                    Spacer(Modifier.height(12.dp))
                    PastelButton(
                        text = t.joinBtn,
                        emoji = "🐟",
                        enabled = !busy,
                        container = CoralPink,
                        onClick = onJoin
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Status-Bubble nur für JOIN-busy und für stillstehende Meldungen.
            // Für HOST-busy gibt es stattdessen den großen HostingDialog (s.u.).
            val showInlineStatus = status.isNotEmpty() && !(busy && mode == MainActivity.Mode.HOST)
            if (showInlineStatus) {
                BubblePanel(
                    modifier = Modifier.fillMaxWidth(),
                    background = when {
                        isError -> CoralPink.copy(alpha = 0.92f)
                        busy    -> SunYellow.copy(alpha = 0.92f)
                        else    -> Lavender.copy(alpha = 0.85f)
                    }
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            text = status,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = DeepSea,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (busy && mode == MainActivity.Mode.JOIN) {
                            Spacer(Modifier.height(12.dp))
                            PastelButton(
                                text = t.cancelConnection,
                                emoji = "✋",
                                enabled = true,
                                container = CoralDeep,
                                onClick = onCancel
                            )
                        }
                    }
                }
            }
        }
    }

    // HOST-Modus: großes Dialog-Fenster mit prominenter IP
    if (busy && mode == MainActivity.Mode.HOST) {
        HostingDialog(
            myIp     = myIp,
            status   = status,
            onCancel = onCancel
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = DeepSea
    )
}

/** Auswahl der Gegnerzahl (1–3) als drei antippbare Pillen. */
@Composable
private fun OpponentCountSelector(
    count: Int,
    onCountChange: (Int) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val t = LocalTexts.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            t.opponentsLabel,
            style = MaterialTheme.typography.titleMedium,
            color = DeepSea
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (n in 1..3) {
                val selected = n == count
                Surface(
                    onClick = { GameSounds.playClick(); onCountChange(n) },
                    enabled = enabled,
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) SeafoamGreen else OceanTop.copy(alpha = 0.7f),
                    contentColor = DeepSea,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$n",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = DeepSea
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PastelTextField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    capWords: Boolean = false,
    enabled: Boolean = true,
    onImeDone: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        placeholder = { Text(placeholder, color = SoftSeaText.copy(alpha = 0.6f)) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = if (capWords) KeyboardCapitalization.Words else KeyboardCapitalization.None,
            imeAction      = ImeAction.Done
        ),
        // Nur überschreiben, wenn ein onImeDone gesetzt ist — sonst Default (schließt Tastatur).
        keyboardActions = if (onImeDone != null) KeyboardActions(onDone = { onImeDone() })
                          else KeyboardActions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = SeafoamDeep,
            unfocusedBorderColor = OceanDeep,
            cursorColor          = CoralDeep,
            focusedTextColor     = DeepSea,
            unfocusedTextColor   = DeepSea,
            disabledTextColor    = DeepSea.copy(alpha = 0.45f),
            disabledBorderColor  = OceanDeep.copy(alpha = 0.4f),
            disabledContainerColor  = OceanTop.copy(alpha = 0.35f),
            focusedContainerColor   = OceanTop.copy(alpha = 0.7f),
            unfocusedContainerColor = OceanTop.copy(alpha = 0.5f)
        )
    )
}

@Composable
private fun PastelButton(
    text: String,
    emoji: String,
    enabled: Boolean,
    container: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = { GameSounds.playClick(); onClick() },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor   = DeepSea,
            disabledContainerColor = container.copy(alpha = 0.4f),
            disabledContentColor   = DeepSea.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        // Emoji → eigenes Icon (Fallback: Emoji-Text, falls unbekannt)
        val iconKind = iconKindForEmoji(emoji)
        if (iconKind != null) {
            GameIcon(iconKind, modifier = Modifier.size(24.dp), tint = DeepSea)
        } else {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = DeepSea
        )
    }
}

@Composable
private fun HostingDialog(
    myIp: String,
    status: String,
    onCancel: () -> Unit
) {
    val t = LocalTexts.current
    Dialog(
        onDismissRequest = { /* explizit Abbrechen — kein Dismiss durch Außenklick */ },
        properties = DialogProperties(
            dismissOnBackPress    = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        BubblePanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            background = Foam,
            cornerRadius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FishMascot(
                    modifier = Modifier.size(width = 96.dp, height = 70.dp),
                    body = SeafoamGreen, bodyDeep = SeafoamDeep,
                    facingRight = true
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    t.hostingDialogTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = DeepSea
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    t.shareIp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftSeaText,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))

                // Große IP-Anzeige
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(OceanMid)
                        .padding(vertical = 18.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = myIp.ifBlank { t.unknownIp },
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSea,
                            letterSpacing = 2.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = status.ifEmpty { t.waitingForCoPlayer },
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftSeaText,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(22.dp))

                PastelButton(
                    text     = t.cancelHosting,
                    emoji    = "✋",
                    enabled  = true,
                    container = CoralDeep,
                    onClick   = onCancel
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════
//  Avatar-Auswahl: Inline-Button + Modal-Dialog
// ═════════════════════════════════════════════════════════════════════════

@Composable
private fun AvatarChooserRow(
    avatar: AvatarChoice,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val t = LocalTexts.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(OceanTop.copy(alpha = 0.6f))
            .clickable(enabled = enabled) { GameSounds.playClick(); onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            choice      = avatar,
            modifier    = Modifier.size(width = 58.dp, height = 44.dp),
            facingRight = true,
            animated    = false
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                t.yourAvatar,
                style = MaterialTheme.typography.titleMedium,
                color = DeepSea
            )
            Text(
                t.avatarSummary(t.kindName(avatar.kind), t.colorName(avatar.color)),
                style = MaterialTheme.typography.bodyMedium,
                color = SoftSeaText
            )
        }
        GameIcon(GameIconKind.PENCIL, modifier = Modifier.size(20.dp), tint = DeepSea)
    }
}

@Composable
private fun AvatarSelectionDialog(
    currentChoice: AvatarChoice,
    onChoiceChange: (AvatarChoice) -> Unit,
    onClose: () -> Unit
) {
    val t = LocalTexts.current
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BubblePanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            background = Foam,
            cornerRadius = 28.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(t.chooseAvatarTitle, style = MaterialTheme.typography.headlineLarge, color = DeepSea)
                Spacer(Modifier.height(6.dp))
                Text(
                    t.chooseAvatarSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftSeaText
                )
                Spacer(Modifier.height(16.dp))

                // 2 x 3 Avatar-Grid
                val kinds = AvatarKind.values().toList()
                for (rowIdx in 0..1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (colIdx in 0..2) {
                            val idx = rowIdx * 3 + colIdx
                            AvatarTile(
                                kind     = kinds[idx],
                                color    = currentChoice.color,
                                selected = currentChoice.kind == kinds[idx],
                                onClick  = { onChoiceChange(currentChoice.copy(kind = kinds[idx])) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (rowIdx == 0) Spacer(Modifier.height(10.dp))
                }

                Spacer(Modifier.height(20.dp))
                Text(t.colorLabel, style = MaterialTheme.typography.titleLarge, color = DeepSea)
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AvatarColor.values().forEach { col ->
                        ColorSwatch(
                            color    = col,
                            selected = currentChoice.color == col,
                            onClick  = { onChoiceChange(currentChoice.copy(color = col)) }
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                PastelButton(
                    text      = t.doneBtn,
                    emoji     = "✓",
                    enabled   = true,
                    container = SeafoamGreen,
                    onClick   = onClose
                )
            }
        }
    }
}

@Composable
private fun AvatarTile(
    kind: AvatarKind,
    color: AvatarColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = LocalTexts.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) SunYellow.copy(alpha = 0.45f)
                else          OceanTop.copy(alpha = 0.55f)
            )
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) SunDeep else OceanDeep.copy(alpha = 0.30f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { GameSounds.playClick(); onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Avatar(
            choice      = AvatarChoice(kind, color),
            modifier    = Modifier.size(width = 80.dp, height = 60.dp),
            facingRight = true,
            animated    = selected
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = t.kindName(kind),
            color = DeepSea,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ColorSwatch(
    color: AvatarColor,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color.body)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) DeepSea else color.bodyDeep,
                shape = CircleShape
            )
            .clickable { GameSounds.playClick(); onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            GameIcon(GameIconKind.CHECK, modifier = Modifier.size(18.dp), tint = DeepSea)
        }
    }
}

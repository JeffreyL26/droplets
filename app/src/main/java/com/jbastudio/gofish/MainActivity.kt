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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jbastudio.gofish.network.GameClient
import com.jbastudio.gofish.network.GameServer
import com.jbastudio.gofish.ui.components.Avatar
import com.jbastudio.gofish.ui.components.AvatarChoice
import com.jbastudio.gofish.ui.components.AvatarColor
import com.jbastudio.gofish.ui.components.AvatarKind
import com.jbastudio.gofish.ui.components.BubblePanel
import com.jbastudio.gofish.ui.components.FishMascot
import com.jbastudio.gofish.ui.components.OceanBackground
import com.jbastudio.gofish.ui.theme.*
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : ComponentActivity() {

    enum class Mode { HOST, JOIN }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val myIp = localIpAddress()
        val avatarPrefs = AvatarPrefs(this)
        // Initialer Avatar aus Prefs in den Holder spielen
        GameHolder.myAvatar = avatarPrefs.load()

        setContent {
            GoFishTheme {
                var name    by remember { mutableStateOf("") }
                var hostIp  by remember { mutableStateOf("") }
                var status  by remember { mutableStateOf("") }
                var isError by remember { mutableStateOf(false) }
                var busy    by remember { mutableStateOf(false) }
                var mode    by remember { mutableStateOf<Mode?>(null) }
                var avatarChoice  by remember { mutableStateOf(GameHolder.myAvatar) }
                var showAvatarDlg by remember { mutableStateOf(false) }
                // Token-Container: jeder Connect-Versuch bekommt eine eigene ID;
                // wird sie inkrementiert, sind alle laufenden Callbacks "stale" und werden ignoriert.
                val attempt = remember { intArrayOf(0) }

                val cancel: () -> Unit = {
                    val cancelMsg = when (mode) {
                        Mode.HOST -> "Hosting abgebrochen."
                        Mode.JOIN -> "Suche abgebrochen."
                        else      -> "Vorgang abgebrochen."
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

                // System-Zurück bei aktiver Verbindung → Cancel statt App beenden
                BackHandler(enabled = busy) { cancel() }

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

                LobbyScreen(
                    name           = name,
                    onNameChange   = { name = it },
                    hostIp         = hostIp,
                    onHostIpChange = { hostIp = it },
                    myIp           = myIp,
                    status         = status,
                    isError        = isError,
                    busy           = busy,
                    mode           = mode,
                    avatar         = avatarChoice,
                    onAvatarClick  = { showAvatarDlg = true },
                    onCancel       = cancel,
                    onHost = {
                        val finalName = name.trim().ifEmpty { "Host" }
                        val myToken = ++attempt[0]
                        busy    = true
                        isError = false
                        mode    = Mode.HOST
                        status  = "Server wird gestartet …"
                        val server = GameServer().also { GameHolder.server = it }
                        server.onLog = { msg ->
                            if (attempt[0] == myToken) runOnUiThread { status = msg }
                        }
                        server.start()
                        connectAsClient(
                            host        = "127.0.0.1",
                            name        = finalName,
                            token       = myToken,
                            currentMode = Mode.HOST,
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
                        val finalName = name.trim().ifEmpty { "Gast" }
                        val ip = hostIp.trim()
                        if (ip.isEmpty()) {
                            status = "Bitte Host-IP eingeben"
                            isError = true
                        } else {
                            val myToken = ++attempt[0]
                            busy    = true
                            isError = false
                            mode    = Mode.JOIN
                            status  = "Verbinde mit $ip …"
                            connectAsClient(
                                host        = ip,
                                name        = finalName,
                                token       = myToken,
                                currentMode = Mode.JOIN,
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
            }
        }
    }

    private fun connectAsClient(
        host: String,
        name: String,
        token: Int,
        currentMode: Mode,
        onUpdate: (String, Boolean) -> Unit,
        onSessionStart: () -> Unit
    ) {
        val client = GameClient(host, name, GameHolder.myAvatar).also { GameHolder.client = it }
        client.onConnected = { onUpdate("Verbunden! Warte auf Spielstart …", false) }
        client.onError     = { err -> onUpdate(friendlyError(err, currentMode), true) }
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

    /** Übersetzt rohe Socket-/IO-Meldungen in spielerfreundliches Deutsch. */
    private fun friendlyError(raw: String, mode: Mode): String {
        val r = raw.lowercase()
        return when {
            r.contains("socket closed")
                    || r.contains("socket is closed")
                    || r.contains("connection abort")
                    || r.contains("connection reset")
                    || r.contains("eof") ->
                when (mode) {
                    Mode.HOST -> "Hosting beendet."
                    Mode.JOIN -> "Verbindung wurde getrennt."
                }
            r.contains("connection refused") || r.contains("econnrefused") ->
                "Server nicht erreichbar — IP korrekt eingegeben?"
            r.contains("timeout") || r.contains("etimedout") ->
                "Zeitüberschreitung — Server antwortet nicht."
            r.contains("network is unreachable") || r.contains("enetunreach") ->
                "Kein Netzwerk verfügbar."
            r.contains("no route to host") || r.contains("ehostunreach") ->
                "Server unter dieser IP nicht erreichbar."
            r.contains("permission denied") ->
                "Zugriff verweigert. Hat die App Netzwerk-Rechte?"
            else -> "Fehler: $raw"
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

        return "Unbekannt"
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
    onCancel: () -> Unit,
    onHost: () -> Unit,
    onJoin: () -> Unit
) {
    OceanBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titel
            Text(
                text  = "Go Fish!",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 52.sp, fontWeight = FontWeight.Black
                ),
                color = DeepSea,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Schnapp dir den Fang!",
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

            // Karte: Spieler-Name + Avatar-Wahl
            BubblePanel(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    SectionLabel("Dein Name")
                    Spacer(Modifier.height(6.dp))
                    PastelTextField(
                        value = name,
                        onChange = onNameChange,
                        placeholder = "z. B. Käpt'n Nemo",
                        capWords = true,
                        enabled = !busy
                    )
                    Spacer(Modifier.height(14.dp))
                    AvatarChooserRow(
                        avatar  = avatar,
                        enabled = !busy,
                        onClick = onAvatarClick
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Karte: Hosten
            BubblePanel(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionLabel("Spiel hosten")
                        Spacer(Modifier.weight(1f))
                        Text("🏠", fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Deine IP: $myIp",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftSeaText
                    )
                    Spacer(Modifier.height(12.dp))
                    PastelButton(
                        text = "Server starten",
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
                        SectionLabel("Spiel beitreten")
                        Spacer(Modifier.weight(1f))
                        Text("🌊", fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    PastelTextField(
                        value = hostIp,
                        onChange = onHostIpChange,
                        placeholder = "Host-IP (z. B. 192.168.0.42)",
                        enabled = !busy
                    )
                    Spacer(Modifier.height(12.dp))
                    PastelButton(
                        text = "Beitreten",
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
                                text = "Verbindung abbrechen",
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

@Composable
private fun PastelTextField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    capWords: Boolean = false,
    enabled: Boolean = true
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
        onClick = onClick,
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
        Text(emoji, fontSize = 22.sp)
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
                    "Spiel hosten",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DeepSea
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Gib diese IP an deinen Mitspieler weiter:",
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
                        text = myIp,
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
                    text = status.ifEmpty { "Warte auf Mitspieler …" },
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftSeaText,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(22.dp))

                PastelButton(
                    text     = "Hosting abbrechen",
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(OceanTop.copy(alpha = 0.6f))
            .clickable(enabled = enabled) { onClick() }
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
                "Dein Avatar",
                style = MaterialTheme.typography.titleMedium,
                color = DeepSea
            )
            Text(
                "${avatar.kind.displayName} · ${avatar.color.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftSeaText
            )
        }
        Text("✏️", fontSize = 20.sp)
    }
}

@Composable
private fun AvatarSelectionDialog(
    currentChoice: AvatarChoice,
    onChoiceChange: (AvatarChoice) -> Unit,
    onClose: () -> Unit
) {
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
                Text("Avatar wählen", style = MaterialTheme.typography.headlineLarge, color = DeepSea)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Such dir ein Meeresbewohner aus.",
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
                Text("Farbe", style = MaterialTheme.typography.titleLarge, color = DeepSea)
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
                    text      = "Fertig",
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
            .clickable { onClick() }
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
            text  = kind.displayName,
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
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text("✓", color = DeepSea, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}

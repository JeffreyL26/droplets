package com.jbastudio.gofish

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbastudio.gofish.i18n.LocalTexts
import com.jbastudio.gofish.i18n.Texts
import com.jbastudio.gofish.i18n.textsFor
import com.jbastudio.gofish.model.Card
import com.jbastudio.gofish.ui.components.ANIMATION_DURATION_MS
import com.jbastudio.gofish.ui.components.AnimationCue
import com.jbastudio.gofish.ui.components.Avatar
import com.jbastudio.gofish.ui.components.AvatarChoice
import com.jbastudio.gofish.ui.components.AvatarColor
import com.jbastudio.gofish.ui.components.AvatarKind
import com.jbastudio.gofish.ui.components.BubblePanel
import com.jbastudio.gofish.ui.components.GameAnimationOverlay
import com.jbastudio.gofish.ui.components.HIGHLIGHT_DURATION_MS
import com.jbastudio.gofish.ui.components.OceanBackground
import com.jbastudio.gofish.ui.components.PlayingCardView
import com.jbastudio.gofish.ui.theme.*
import kotlinx.coroutines.delay
import org.json.JSONObject

/** Sprachneutraler Sieger-Wert vom Server für ein Unentschieden (siehe GameAuthority). */
private const val TIE_SENTINEL = "Unentschieden!"

class GameActivity : ComponentActivity() {

    private val state = GameUiState()

    /** Sprache der laufenden Partie (bei Spielstart festgelegt). */
    private lateinit var T: Texts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val client = GameHolder.client ?: run { finish(); return }

        // Sprache laden (während einer Partie unveränderlich)
        T = textsFor(LanguagePrefs(this).load())

        // Eigener Avatar aus dem persistenten Holder
        state.myAvatar = GameHolder.myAvatar
        state.opponentName = T.opponentDefault

        // gecachte GAME_START verarbeiten
        GameHolder.gameStartMsg?.let { handleMessage(it) }

        client.onMessage = { msg -> runOnUiThread { handleMessage(msg) } }
        client.onError   = { err -> runOnUiThread {
            // Während des Spiels = Netzwerkabbruch → Sitzung beenden
            if (state.sessionEndedMessage == null) {
                state.sessionEndedMessage = T.connectionLostSession
            }
        }}

        setContent {
            GoFishTheme {
                CompositionLocalProvider(LocalTexts provides T) {
                    GameScreen(
                        state  = state,
                        onExit = { finish() },
                        onAsk  = { rank ->
                            if (!state.myTurn) return@GameScreen
                            when {
                                rank !in Card.RANKS ->
                                    toast(T.toastNoSuchCard)
                                state.myHand.none { it.rank == rank } ->
                                    toast(T.toastMustHold(rank))
                                else -> {
                                    // WICHTIG: erst sperren, DANN senden. Beim Online-Host wird die
                                    // ASK_RESULT-Antwort synchron (inline) verarbeitet — würde myTurn
                                    // hier NACH dem Senden gesetzt, überschriebe diese Zeile den
                                    // korrekten Extra-Zug (yourTurn=true) wieder mit false → Deadlock.
                                    state.selectedRank = null
                                    state.myTurn = false  // gesperrt bis Antwort kommt
                                    GameHolder.client?.sendAsk(rank)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun handleMessage(msg: JSONObject) {
        when (msg.getString("type")) {
            "GAME_START"    -> handleGameStart(msg)
            "ASK_RESULT"    -> handleAskResult(msg)
            "OPPONENT_LEFT" -> {
                state.sessionEndedMessage = T.opponentLeft(state.opponentName)
            }
        }
    }

    private fun handleGameStart(msg: JSONObject) {
        state.opponentName     = msg.getString("opponentName")
        state.opponentAvatar   = parseAvatar(
            kindStr  = msg.optString("opponentAvatarKind",  "FISH"),
            colorStr = msg.optString("opponentAvatarColor", "SUN")
        )
        state.myHand.clear()
        state.myHand.addAll(Card.listFromJson(msg.getJSONArray("yourHand")))
        state.myTurn           = msg.getBoolean("yourTurn")
        state.deckSize         = msg.getInt("deckSize")
        state.opponentHandSize = msg.getInt("opponentHandSize")
        state.opponentBooks    = emptyList()
        state.myBooks          = emptyList()
        state.gameOver         = false
        state.winnerName       = ""
        state.appendSystem(T.gameStartedAs(GameHolder.client?.playerName.orEmpty()))
    }

    private fun parseAvatar(kindStr: String, colorStr: String): AvatarChoice {
        val kind  = runCatching { AvatarKind.valueOf(kindStr)   }.getOrDefault(AvatarKind.FISH)
        val color = runCatching { AvatarColor.valueOf(colorStr) }.getOrDefault(AvatarColor.SUN)
        return AvatarChoice(kind, color)
    }

    private fun handleAskResult(msg: JSONObject) {
        val rank       = msg.getString("rank")
        val askerIsYou = msg.getBoolean("askerIsYou")
        val gotCards   = msg.getBoolean("gotCards")
        val wentFish   = msg.getBoolean("wentFishing")
        val newBooks   = msg.getJSONArray("newBooks").let { a -> (0 until a.length()).map { a.getString(it) } }
        val gameOver   = msg.getBoolean("gameOver")

        state.myHand.clear()
        state.myHand.addAll(Card.listFromJson(msg.getJSONArray("yourHand")))
        state.myTurn = msg.getBoolean("yourTurn") && !gameOver
        state.myBooks       = msg.getJSONArray("yourBooks").let { a -> (0 until a.length()).map { a.getString(it) } }
        state.opponentBooks = msg.getJSONArray("opponentBooks").let { a -> (0 until a.length()).map { a.getString(it) } }
        state.deckSize         = msg.getInt("deckSize")
        state.opponentHandSize = msg.getInt("opponentHandSize")

        // Sammelt die Animations-Sequenz für diesen Spielzug (Main-Cue → optional Books)
        val cueSequence = mutableListOf<AnimationCue>()

        if (askerIsYou) {
            if (gotCards) {
                val cards = Card.listFromJson(msg.getJSONArray("cardsReceived"))
                cueSequence.add(AnimationCue(
                    kind  = AnimationCue.Kind.STEAL,
                    rank  = rank,
                    cards = cards
                ))
                state.appendAction(isMe = true, name = T.you,
                    text = T.youAskedGot(state.opponentName, rank, cards.size))
            } else {
                state.appendAction(isMe = true, name = T.you,
                    text = T.youAskedGoFish(rank))
                val drawn: Card? =
                    if (msg.has("drawnCard")) Card.fromJson(msg.getJSONObject("drawnCard")) else null
                cueSequence.add(AnimationCue(
                    kind      = AnimationCue.Kind.GO_FISH,
                    rank      = rank,
                    drawnCard = drawn
                ))
                if (drawn != null) {
                    val matched = msg.getBoolean("drawnMatched")
                    state.appendSystem(if (matched) T.drawnCardHit(drawn.toString()) else T.drawnCard(drawn.toString()))
                } else {
                    state.appendSystem(T.deckEmpty)
                }
            }
            // BOOK-Celebrations folgen direkt im Anschluss (eigene Cues in der Queue)
            for (b in newBooks) {
                cueSequence.add(AnimationCue(kind = AnimationCue.Kind.BOOK, rank = b))
            }
        } else {
            if (gotCards) {
                val n = msg.getInt("cardCount")
                state.appendAction(isMe = false, name = state.opponentName,
                    text = T.oppAskedGot(rank, n))
            } else {
                state.appendAction(isMe = false, name = state.opponentName,
                    text = T.oppAskedGoFish(rank, wentFish))
            }
        }
        for (b in newBooks) {
            state.appendBook(
                isMe = askerIsYou,
                name = if (askerIsYou) T.you else state.opponentName,
                text = if (askerIsYou) T.youBook(b) else T.oppBook(b)
            )
        }

        if (gameOver) {
            state.gameOver   = true
            state.winnerName = msg.getString("winnerName")
            val myBks  = msg.getInt("myBooks")
            val opBks  = msg.getInt("opBooks")
            val myName = GameHolder.client?.playerName.orEmpty()
            state.gameResult = when {
                state.winnerName.equals(TIE_SENTINEL, ignoreCase = true) ->
                    GameResult.Tie(myBks, opBks)
                state.winnerName == myName ->
                    GameResult.Win(myBks, opBks)
                else ->
                    GameResult.Lose(state.winnerName, myBks, opBks)
            }
            state.appendSystem(T.gameOverLog)
        }

        // Animations-Sequenz starten (falls eigene Aktion → STEAL/GO_FISH + ggf. BOOKs)
        if (cueSequence.isNotEmpty()) {
            state.playCueSequence(cueSequence)
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            GameHolder.client?.disconnect()
            GameHolder.server?.stop()
            GameHolder.client = null
            GameHolder.server = null
            GameHolder.gameStartMsg = null
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  UI State (haltbar in der Activity)
// ─────────────────────────────────────────────────────────────────────────

class GameUiState {
    var opponentName     by mutableStateOf("Gegner")
    var opponentAvatar   by mutableStateOf(AvatarChoice())
    var myAvatar         by mutableStateOf(AvatarChoice())
    var opponentHandSize by mutableStateOf(0)
    var opponentBooks    by mutableStateOf<List<String>>(emptyList())
    var deckSize         by mutableStateOf(0)
    val myHand: SnapshotStateList<Card> = mutableStateListOf()
    var myBooks          by mutableStateOf<List<String>>(emptyList())
    var myTurn           by mutableStateOf(false)
    var gameOver         by mutableStateOf(false)
    var winnerName       by mutableStateOf("")
    val logEntries: SnapshotStateList<LogEntry> = mutableStateListOf()
    var selectedRank: String? by mutableStateOf(null)

    /** Wenn != null, ist die Sitzung beendet (z.B. Mitspieler hat verlassen). */
    var sessionEndedMessage: String? by mutableStateOf(null)
    /** Wenn != null, ist das Spiel regulär zu Ende — Popup zeigt das Ergebnis. */
    var gameResult: GameResult? by mutableStateOf(null)
    /** Aktuell gespielter Cue (kann sich während einer Sequenz ändern). */
    var cue: AnimationCue? by mutableStateOf(null)
    /** Folge-Cues, die nach `cue` abgespielt werden (z. B. BOOK nach STEAL). */
    val pendingCues: SnapshotStateList<AnimationCue> = mutableStateListOf()
    /** Trigger für den LaunchedEffect — hochzählen, wenn eine neue Cue-Sequenz beginnt. */
    var cueGeneration: Int by mutableStateOf(0)
    /** Steuert die Overlay-Sichtbarkeit unabhängig vom Cue, damit der LE-Coroutine sich nicht selbst kanzelt. */
    var overlayVisible: Boolean by mutableStateOf(false)
    /** Karten in der Hand, die kurz nach einer Animation aufleuchten sollen (Identifier = card.toString()). */
    var highlightedCards: Set<String> by mutableStateOf(emptySet())

    /** Startet eine Cue-Sequenz: erster wird aktiv, Rest in die Queue, Generation hoch. */
    fun playCueSequence(seq: List<AnimationCue>) {
        if (seq.isEmpty()) return
        cue = seq.first()
        pendingCues.clear()
        pendingCues.addAll(seq.drop(1))
        cueGeneration++
    }

    fun appendAction(isMe: Boolean, name: String, text: String) {
        logEntries.add(LogEntry.Action(isMe, name, text))
    }
    fun appendBook(isMe: Boolean, name: String, text: String) {
        logEntries.add(LogEntry.Book(isMe, name, text))
    }
    fun appendSystem(text: String) {
        logEntries.add(LogEntry.System(text))
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Log-Einträge
// ─────────────────────────────────────────────────────────────────────────

/** Ergebnis am Spielende — steuert das GameOver-Popup. */
sealed class GameResult {
    abstract val myBooks: Int
    abstract val opBooks: Int
    data class Win (override val myBooks: Int, override val opBooks: Int) : GameResult()
    data class Lose(val winnerName: String, override val myBooks: Int, override val opBooks: Int) : GameResult()
    data class Tie (override val myBooks: Int, override val opBooks: Int) : GameResult()
}

sealed class LogEntry {
    /** Spieler-Aktion (Frage, Antwort etc.) — bekommt Mini-Fisch + fetten Namen. */
    data class Action(val isMe: Boolean, val name: String, val text: String) : LogEntry()
    /** Spieler hat ein Buch abgelegt — bekommt 📚-Icon + fetten Namen. */
    data class Book(val isMe: Boolean, val name: String, val text: String) : LogEntry()
    /** System-Meldung — neutraler Text, kein Avatar. */
    data class System(val text: String) : LogEntry()
}

// ─────────────────────────────────────────────────────────────────────────
//  Hauptbildschirm
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun GameScreen(state: GameUiState, onExit: () -> Unit, onAsk: (String) -> Unit) {
    var showExitDialog by remember { mutableStateOf(false) }
    val sessionEnded = state.sessionEndedMessage
    val gameResult   = state.gameResult

    // System-Zurück: bei beendetem Spiel/Sitzung direkt raus, sonst Bestätigungsdialog
    BackHandler(
        enabled = !showExitDialog && sessionEnded == null && gameResult == null
    ) { showExitDialog = true }

    if (showExitDialog) {
        ExitConfirmDialog(
            onConfirm = { showExitDialog = false; onExit() },
            onDismiss = { showExitDialog = false }
        )
    }

    if (sessionEnded != null) {
        SessionEndedDialog(message = sessionEnded, onConfirm = onExit)
    }

    if (gameResult != null && sessionEnded == null) {
        GameOverDialog(
            result       = gameResult,
            opponentName = state.opponentName,
            onExit       = onExit,
            onDismiss    = { state.gameResult = null }
        )
    }

    // Animation-Sequenz: erst aktueller Cue, dann pendingCues abarbeiten.
    // LaunchedEffect keyed auf cueGeneration, damit sich die Coroutine NICHT durch
    // eigene state.cue-Updates innerhalb der Sequenz selbst kanzelt.
    val cue = state.cue
    LaunchedEffect(state.cueGeneration) {
        var c = state.cue ?: return@LaunchedEffect
        try {
            while (true) {
                state.overlayVisible = true
                delay(ANIMATION_DURATION_MS)
                state.overlayVisible = false
                val candidateIds: Set<String> = when (c.kind) {
                    AnimationCue.Kind.GO_FISH -> c.drawnCard?.let { setOf(it.toString()) } ?: emptySet()
                    AnimationCue.Kind.STEAL   -> c.cards.map { it.toString() }.toSet()
                    AnimationCue.Kind.BOOK    -> emptySet()
                }
                // Nur tatsächlich noch in der Hand befindliche Karten hervorheben.
                // (Bei einem 3-Karten-Steal sind die Karten z.B. schon in einem Buch.)
                val actualIds = candidateIds.intersect(
                    state.myHand.map { it.toString() }.toSet()
                )
                if (actualIds.isNotEmpty()) {
                    state.highlightedCards = actualIds
                    delay(HIGHLIGHT_DURATION_MS)
                    state.highlightedCards = emptySet()
                }
                if (state.pendingCues.isEmpty()) break
                c = state.pendingCues.removeAt(0)
                state.cue = c   // löst Re-Render des Overlays mit dem nächsten Cue aus
            }
        } finally {
            state.overlayVisible = false
            state.highlightedCards = emptySet()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OceanBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                // Header — Titel + Exit-Button rechts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "🎣 Go Fish",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepSea
                    )
                    Spacer(Modifier.weight(1f))
                    ExitButton(onClick = { showExitDialog = true })
                }

                Spacer(Modifier.height(8.dp))

                // Gegner-Panel
                OpponentPanel(
                    name      = state.opponentName,
                    avatar    = state.opponentAvatar,
                    handSize  = state.opponentHandSize,
                    books     = state.opponentBooks
                )

                Spacer(Modifier.height(10.dp))

                // Deck + Eigene Bücher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DeckBadge(deckSize = state.deckSize, modifier = Modifier.weight(1f))
                    BooksBadge(
                        title = LocalTexts.current.yourBooks,
                        books = state.myBooks,
                        color = SeafoamGreen,
                        modifier = Modifier.weight(2f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Log (nimmt allen freien Platz)
                LogPanel(
                    entries        = state.logEntries,
                    myAvatar       = state.myAvatar,
                    opponentAvatar = state.opponentAvatar,
                    modifier       = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(Modifier.height(10.dp))

                // Status / Turn Indicator
                TurnStatusBar(
                    myTurn       = state.myTurn,
                    gameOver     = state.gameOver,
                    winnerName   = state.winnerName,
                    opponentName = state.opponentName
                )

                Spacer(Modifier.height(10.dp))

                // Hand — 6 Spalten Grid, vertikal scrollbar
                HandGrid(
                    cards            = state.myHand,
                    selectedRank     = state.selectedRank,
                    highlightedCards = state.highlightedCards,
                    enabled          = state.myTurn && !state.overlayVisible,
                    onSelect         = { rank -> state.selectedRank = rank }
                )

                Spacer(Modifier.height(10.dp))

                // Frage-Button
                AskButton(
                    selectedRank = state.selectedRank,
                    enabled      = state.myTurn && state.selectedRank != null && !state.overlayVisible,
                    onAsk        = { state.selectedRank?.let(onAsk) }
                )
            }
        }

        // Overlay über allem
        if (state.overlayVisible && cue != null) {
            GameAnimationOverlay(cue = cue)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Teil-Komponenten
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun OpponentPanel(name: String, avatar: AvatarChoice, handSize: Int, books: List<String>) {
    BubblePanel(
        modifier = Modifier.fillMaxWidth(),
        background = LavenderDeep.copy(alpha = 0.85f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                choice = avatar,
                modifier = Modifier.size(width = 78.dp, height = 60.dp),
                facingRight = true,
                animated = true
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Foam
                )
                Text(
                    LocalTexts.current.handAndBooks(handSize, books.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Foam.copy(alpha = 0.92f)
                )
                if (books.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    BookChips(books, chipColor = SunYellow)
                }
            }
        }
    }
}

@Composable
private fun DeckBadge(deckSize: Int, modifier: Modifier = Modifier) {
    BubblePanel(modifier = modifier, background = OceanDeep) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(LocalTexts.current.deckBadge, style = MaterialTheme.typography.titleMedium, color = Foam)
            Text(
                "$deckSize",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 28.sp),
                color = Foam,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun BooksBadge(
    title: String,
    books: List<String>,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    BubblePanel(modifier = modifier, background = color) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepSea
                )
                Spacer(Modifier.weight(1f))
                Text("📚 ${books.size}", color = DeepSea, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            if (books.isEmpty()) {
                Text("—", color = SoftSeaText, style = MaterialTheme.typography.bodyMedium)
            } else {
                BookChips(books, chipColor = Foam)
            }
        }
    }
}

@Composable
private fun BookChips(books: List<String>, chipColor: androidx.compose.ui.graphics.Color) {
    // LazyRow → horizontal scrollbar, wenn mehr Bücher als Platz da sind
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(books) { rank ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(chipColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    rank,
                    color = DeepSea,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun LogPanel(
    entries: List<LogEntry>,
    myAvatar: AvatarChoice,
    opponentAvatar: AvatarChoice,
    modifier: Modifier = Modifier
) {
    BubblePanel(modifier = modifier, background = Foam.copy(alpha = 0.92f)) {
        val scroll = rememberScrollState()
        LaunchedEffect(entries.size) {
            scroll.animateScrollTo(scroll.maxValue)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (entries.isEmpty()) {
                Text(
                    LocalTexts.current.logEmpty,
                    color = SoftSeaText,
                    fontSize = 12.sp
                )
            }
            entries.forEach { entry -> LogRow(entry, myAvatar, opponentAvatar) }
        }
    }
}

@Composable
private fun LogRow(entry: LogEntry, myAvatar: AvatarChoice, opponentAvatar: AvatarChoice) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        when (entry) {
            is LogEntry.Action -> {
                Avatar(
                    choice      = if (entry.isMe) myAvatar else opponentAvatar,
                    modifier    = Modifier
                        .padding(top = 1.dp)
                        .size(width = 30.dp, height = 22.dp),
                    facingRight = !entry.isMe,  // Spieler-Avatare schauen sich gegenseitig an
                    animated    = false
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = DeepSea)) {
                            append(entry.name)
                        }
                        append(" ")
                        append(entry.text)
                    },
                    color = DeepSea,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            is LogEntry.Book -> {
                Box(
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(width = 30.dp, height = 22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📚", fontSize = 16.sp)
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = DeepSea)) {
                            append(entry.name)
                        }
                        append(" ")
                        append(entry.text)
                    },
                    color = DeepSea,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            is LogEntry.System -> {
                Text(
                    text = entry.text,
                    color = SoftSeaText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TurnStatusBar(
    myTurn: Boolean,
    gameOver: Boolean,
    winnerName: String,
    opponentName: String
) {
    val t = LocalTexts.current
    val winnerDisplay = if (winnerName.equals(TIE_SENTINEL, ignoreCase = true)) t.drawWord else winnerName
    val (bg, fg, text) = when {
        gameOver -> Triple(SunYellow, DeepSea, t.turnGameOver(winnerDisplay))
        myTurn   -> Triple(SeafoamGreen, DeepSea, t.turnYou)
        else     -> Triple(Lavender, DeepSea, t.turnWaiting(opponentName))
    }
    BubblePanel(
        modifier = Modifier.fillMaxWidth(),
        background = bg
    ) {
        Text(
            text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 14.dp),
            textAlign = TextAlign.Center,
            color = fg,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun HandGrid(
    cards: List<Card>,
    selectedRank: String?,
    highlightedCards: Set<String>,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {
    val sorted = cards.sortedWith(compareBy { Card.RANKS.indexOf(it.rank) })

    BubblePanel(
        modifier = Modifier.fillMaxWidth(),
        background = Foam.copy(alpha = 0.92f)
    ) {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    LocalTexts.current.yourHand,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepSea
                )
                Spacer(Modifier.weight(1f))
                if (sorted.size > 12) {
                    Text(
                        LocalTexts.current.scrollHint,
                        color = SoftSeaText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    "🃏 ${cards.size}",
                    color = DeepSea,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))

            if (sorted.isEmpty()) {
                Text(
                    LocalTexts.current.noCardsLeft,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftSeaText,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                // Höhe = 2 sichtbare Reihen. Bei >12 Karten scrollt das Grid vertikal.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement   = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    items(sorted, key = { it.toString() }) { card ->
                        PlayingCardView(
                            rank        = card.rank,
                            suit        = card.suit,
                            selected    = enabled && card.rank == selectedRank,
                            highlighted = card.toString() in highlightedCards,
                            modifier    = Modifier.aspectRatio(0.70f),
                            onClick     = if (enabled) {
                                { onSelect(card.rank) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AskButton(
    selectedRank: String?,
    enabled: Boolean,
    onAsk: () -> Unit
) {
    val t = LocalTexts.current
    val label = if (selectedRank != null) t.askFor(selectedRank) else t.chooseCard

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit  = fadeOut()
    ) {
        Button(
            onClick = onAsk,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = CoralPink,
                contentColor           = DeepSea,
                disabledContainerColor = CoralPink.copy(alpha = 0.35f),
                disabledContentColor   = DeepSea.copy(alpha = 0.4f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text("🎣", fontSize = 22.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = DeepSea
            )
        }
    }
}

@Composable
private fun ExitButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = CoralPink,
            contentColor   = DeepSea
        )
    ) {
        Text(
            "✕",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun ExitConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val t = LocalTexts.current
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Foam,
        titleContentColor = DeepSea,
        textContentColor  = DeepSea,
        title = {
            Text(
                t.exitTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = DeepSea
            )
        },
        text = {
            Text(
                t.exitBody,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftSeaText
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralDeep,
                    contentColor   = Foam
                )
            ) {
                Text(t.leaveBtn, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = DeepSea)
            ) {
                Text(t.stayBtn, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun SessionEndedDialog(message: String, onConfirm: () -> Unit) {
    val t = LocalTexts.current
    AlertDialog(
        onDismissRequest = onConfirm,    // jeder Weg führt zurück zur Lobby
        shape = RoundedCornerShape(24.dp),
        containerColor = Foam,
        titleContentColor = DeepSea,
        textContentColor  = DeepSea,
        title = {
            Text(
                t.sessionEndedTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = DeepSea
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = SoftSeaText
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SeafoamDeep,
                    contentColor   = Foam
                )
            ) {
                Text(t.toLobbyBtn, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// ═════════════════════════════════════════════════════════════════════════
//  GameOver-Popup (Sieg / Niederlage / Unentschieden)
// ═════════════════════════════════════════════════════════════════════════

private data class ResultStyle(
    val emoji: String,
    val accent: Color,
    val accentDeep: Color,
    val panelBg: Color
)

private fun styleFor(result: GameResult): ResultStyle = when (result) {
    is GameResult.Win -> ResultStyle(
        emoji = "🏆",
        accent = SunYellow, accentDeep = SunDeep,
        panelBg = SunYellow.copy(alpha = 0.30f)
    )
    is GameResult.Lose -> ResultStyle(
        emoji = "🪝",
        accent = Lavender, accentDeep = LavenderDeep,
        panelBg = Lavender.copy(alpha = 0.35f)
    )
    is GameResult.Tie -> ResultStyle(
        emoji = "🤝",
        accent = SeafoamGreen, accentDeep = SeafoamDeep,
        panelBg = SeafoamGreen.copy(alpha = 0.30f)
    )
}

@Composable
private fun GameOverDialog(
    result: GameResult,
    opponentName: String,
    onExit: () -> Unit,
    onDismiss: () -> Unit
) {
    val style = styleFor(result)
    val t = LocalTexts.current
    val title = when (result) {
        is GameResult.Win  -> t.winTitle
        is GameResult.Lose -> t.loseTitle
        is GameResult.Tie  -> t.tieTitle
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress    = true,
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
                // Großes Emoji
                Text(
                    style.emoji,
                    fontSize = 72.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = style.accentDeep.copy(alpha = 0.45f),
                            offset = Offset(0f, 8f),
                            blurRadius = 16f
                        )
                    )
                )
                Spacer(Modifier.height(8.dp))

                // Comic-Style Titel
                GameOverTitle(
                    text       = title,
                    fillColor  = style.accent,
                    strokeColor = DeepSea
                )

                Spacer(Modifier.height(10.dp))

                // Subtitle (nur bei Niederlage zeigt den Sieger-Namen)
                if (result is GameResult.Lose) {
                    Text(
                        text  = t.loseSubtitle(result.winnerName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftSeaText,
                        textAlign = TextAlign.Center
                    )
                } else if (result is GameResult.Win) {
                    Text(
                        text  = t.winSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftSeaText,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text  = t.tieSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftSeaText,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Score-Panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(style.panelBg)
                        .padding(vertical = 16.dp, horizontal = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScoreCell(label = t.you,        count = result.myBooks)
                        VerticalDivider()
                        ScoreCell(label = opponentName, count = result.opBooks)
                    }
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape  = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = style.accentDeep,
                        contentColor   = Foam
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text("🏠", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        t.toMainMenu,
                        style = MaterialTheme.typography.labelLarge,
                        color = Foam,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GameOverTitle(text: String, fillColor: Color, strokeColor: Color) {
    val baseStyle = TextStyle(
        fontSize      = 46.sp,
        fontWeight    = FontWeight.Black,
        fontFamily    = FontFamily.SansSerif,
        fontStyle     = FontStyle.Italic,
        letterSpacing = 2.sp,
        textAlign     = TextAlign.Center
    )
    Box {
        // Schatten / Tiefe
        Text(
            text  = text,
            color = strokeColor.copy(alpha = 0.6f),
            style = baseStyle.copy(
                shadow = Shadow(
                    color = strokeColor.copy(alpha = 0.45f),
                    offset = Offset(4f, 8f),
                    blurRadius = 10f
                )
            ),
            modifier = Modifier.padding(start = 2.dp, top = 2.dp)
        )
        // Füllung
        Text(text = text, color = fillColor, style = baseStyle)
    }
}

@Composable
private fun ScoreCell(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = label,
            color      = DeepSea,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines   = 1
        )
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📚", fontSize = 22.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                text       = count.toString(),
                color      = DeepSea,
                fontSize   = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(54.dp)
            .background(DeepSea.copy(alpha = 0.25f))
    )
}

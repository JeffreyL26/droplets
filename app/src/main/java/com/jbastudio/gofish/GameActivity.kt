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
import androidx.compose.foundation.clickable
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
import com.jbastudio.gofish.ui.components.GameIcon
import com.jbastudio.gofish.ui.components.GameIconKind
import com.jbastudio.gofish.ui.components.IconText
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
        GameSounds.init(this)

        val client = GameHolder.client ?: run { finish(); return }

        // Sprache laden (während einer Partie unveränderlich)
        T = textsFor(LanguagePrefs(this).load())

        // Eigener Avatar aus dem persistenten Holder
        state.myAvatar = GameHolder.myAvatar

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
                        onAsk  = { attemptAsk() }
                    )
                }
            }
        }
    }

    private fun handleMessage(msg: JSONObject) {
        when (msg.getString("type")) {
            "GAME_START"    -> handleGameStart(msg)
            "ASK_RESULT"    -> handleAskResult(msg)
            "PLAYER_LEFT"   -> handlePlayerLeft(msg)
            "OPPONENT_LEFT" -> {
                state.sessionEndedMessage =
                    T.opponentLeft(state.opponents.firstOrNull()?.name ?: T.opponentDefault)
            }
        }
    }

    private fun handleGameStart(msg: JSONObject) {
        state.myId = msg.optInt("yourId", GameHolder.client?.playerId ?: -1)
        state.myHand.clear()
        state.myHand.addAll(Card.listFromJson(msg.getJSONArray("yourHand")))
        state.myTurn   = msg.getBoolean("yourTurn")
        state.deckSize = msg.getInt("deckSize")
        state.myBooks  = emptyList()
        state.gameOver = false
        state.winnerName = ""
        state.selectedRank = null
        state.selectedTargetId = null
        parsePlayers(msg)
        state.appendSystem(T.gameStartedAs(GameHolder.client?.playerName.orEmpty()))
    }

    private fun parseAvatar(kindStr: String, colorStr: String): AvatarChoice {
        val kind  = runCatching { AvatarKind.valueOf(kindStr)   }.getOrDefault(AvatarKind.FISH)
        val color = runCatching { AvatarColor.valueOf(colorStr) }.getOrDefault(AvatarColor.SUN)
        return AvatarChoice(kind, color)
    }

    /** Übernimmt die Spielerliste aus einer Nachricht (alle außer mir = Gegner) + aktuellen Zug. */
    private fun parsePlayers(msg: JSONObject) {
        val arr = msg.optJSONArray("players") ?: return
        val list = mutableListOf<OpponentInfo>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val id = o.getInt("id")
            if (id == state.myId) continue
            list.add(
                OpponentInfo(
                    id       = id,
                    name     = o.getString("name"),
                    avatar   = parseAvatar(o.optString("avatarKind", "FISH"), o.optString("avatarColor", "SUN")),
                    handSize = o.optInt("handSize", 0),
                    books    = o.getJSONArray("books").let { b -> (0 until b.length()).map { b.getString(it) } },
                    active   = o.optBoolean("active", true)
                )
            )
        }
        state.opponents = list
        if (msg.has("currentPlayerId")) state.currentPlayerId = msg.getInt("currentPlayerId")
    }

    private fun handleAskResult(msg: JSONObject) {
        val rank        = msg.getString("rank")
        val askerId     = msg.optInt("askerId", -1)
        val targetId    = msg.optInt("targetId", -1)
        val askerIsYou  = msg.optBoolean("askerIsYou", false)
        val targetIsYou = msg.optBoolean("targetIsYou", false)
        val gotCards    = msg.getBoolean("gotCards")
        val wentFish    = msg.getBoolean("wentFishing")
        val newBooks    = msg.getJSONArray("newBooks").let { a -> (0 until a.length()).map { a.getString(it) } }
        val gameOver    = msg.getBoolean("gameOver")

        // Karten, die mir gerade abgenommen werden (für die „abgegeben"-Animation) —
        // VOR dem Hand-Update abgreifen, sonst sind sie schon weg.
        val cardsTakenFromMe: List<Card> =
            if (targetIsYou && gotCards) state.myHand.filter { it.rank == rank } else emptyList()

        state.myHand.clear()
        state.myHand.addAll(Card.listFromJson(msg.getJSONArray("yourHand")))
        state.myTurn   = msg.getBoolean("yourTurn") && !gameOver
        state.myBooks  = msg.getJSONArray("yourBooks").let { a -> (0 until a.length()).map { a.getString(it) } }
        state.deckSize = msg.getInt("deckSize")
        parsePlayers(msg)

        // Namensnennung der Ziele erst nötig, wenn es mehr als einen aktiven Gegner gibt.
        val multi       = state.activeOpponents.size >= 2
        val askerName   = if (askerIsYou) T.you else state.opponentById(askerId)?.name ?: T.opponentDefault
        val askerAvatar = if (askerIsYou) state.myAvatar else state.opponentById(askerId)?.avatar ?: AvatarChoice()
        val targetName  = if (targetIsYou) T.youObject else state.opponentById(targetId)?.name ?: T.opponentDefault

        val cueSequence = mutableListOf<AnimationCue>()

        if (askerIsYou) {
            if (gotCards) {
                val cards = Card.listFromJson(msg.getJSONArray("cardsReceived"))
                cueSequence.add(AnimationCue(AnimationCue.Kind.STEAL, rank, cards = cards))
                state.appendAction(true, T.you, state.myAvatar, T.youAskedGot(targetName, rank, cards.size))
            } else {
                state.appendAction(true, T.you, state.myAvatar, T.youAskedGoFish(rank))
                val drawn: Card? =
                    if (msg.has("drawnCard")) Card.fromJson(msg.getJSONObject("drawnCard")) else null
                cueSequence.add(AnimationCue(AnimationCue.Kind.GO_FISH, rank, drawnCard = drawn))
                if (drawn != null) {
                    val matched = msg.optBoolean("drawnMatched", false)
                    state.appendSystem(if (matched) T.drawnCardHit(drawn.toString()) else T.drawnCard(drawn.toString()))
                } else {
                    state.appendSystem(T.deckEmpty)
                }
            }
            for (b in newBooks) cueSequence.add(AnimationCue(AnimationCue.Kind.BOOK, rank = b))
        } else {
            if (gotCards) {
                val n = msg.getInt("cardCount")
                state.appendAction(false, askerName, askerAvatar,
                    if (multi) T.oppAskedGotAt(targetName, rank, n) else T.oppAskedGot(rank, n))
                // Nur wenn MIR Karten abgenommen wurden: „abgegeben"-Flug nach oben.
                if (targetIsYou && cardsTakenFromMe.isNotEmpty()) {
                    cueSequence.add(AnimationCue(AnimationCue.Kind.GIVE, rank, cards = cardsTakenFromMe))
                }
            } else {
                state.appendAction(false, askerName, askerAvatar,
                    if (multi) T.oppAskedGoFishAt(targetName, rank) else T.oppAskedGoFish(rank, wentFish))
                if (msg.optBoolean("drawnMatched", false)) state.appendSystem(T.oppDrawnHit(rank))
            }
        }

        for (b in newBooks) {
            state.appendBook(
                isMe = askerIsYou,
                name = askerName,
                text = if (askerIsYou) T.youBook(b) else T.oppBook(b)
            )
        }

        if (gameOver) handleGameOver(msg)

        if (cueSequence.isNotEmpty()) state.playCueSequence(cueSequence)
    }

    private fun handleGameOver(msg: JSONObject) {
        state.gameOver   = true
        state.winnerName = msg.optString("winnerName", TIE_SENTINEL)
        val standings = buildStandings(msg)
        val myScore   = standings.firstOrNull { it.isMe }?.books ?: 0
        val top       = standings.maxOfOrNull { it.books } ?: 0
        val uniqueTop = standings.count { it.books == top } == 1
        state.gameResult = when {
            myScore >= top && uniqueTop -> GameResult.Win(standings)
            myScore >= top              -> GameResult.Tie(standings)
            else                        -> GameResult.Lose(state.winnerName, standings)
        }
        state.appendSystem(T.gameOverLog)
    }

    /** Endstand aller noch aktiven Spieler aus der Spielerliste. */
    private fun buildStandings(msg: JSONObject): List<PlayerScore> {
        val arr = msg.optJSONArray("players")
        if (arr != null) {
            return (0 until arr.length()).mapNotNull { i ->
                val o = arr.getJSONObject(i)
                if (!o.optBoolean("active", true)) return@mapNotNull null
                val id = o.getInt("id")
                val isMe = id == state.myId
                PlayerScore(
                    name  = if (isMe) T.you else o.getString("name"),
                    books = o.getJSONArray("books").length(),
                    isMe  = isMe
                )
            }
        }
        return listOf(
            PlayerScore(T.you, msg.optInt("myBooks", state.myBooks.size), true),
            PlayerScore(state.opponents.firstOrNull()?.name ?: T.opponentDefault, msg.optInt("opBooks", 0), false)
        )
    }

    private fun handlePlayerLeft(msg: JSONObject) {
        val leftId   = msg.optInt("playerId", -1)
        val leftName = msg.optString("playerName", T.opponentDefault)
        if (msg.has("yourHand")) {
            state.myHand.clear()
            state.myHand.addAll(Card.listFromJson(msg.getJSONArray("yourHand")))
        }
        state.myTurn   = msg.optBoolean("yourTurn", state.myTurn) && !msg.optBoolean("gameOver", false)
        state.deckSize = msg.optInt("deckSize", state.deckSize)
        parsePlayers(msg)
        if (state.selectedTargetId == leftId) state.selectedTargetId = null
        state.appendSystem(T.playerLeftReshuffle(leftName))
    }

    /** Validiert die Auswahl und schickt die Frage; zeigt sonst eine Fehlermeldung. */
    private fun attemptAsk() {
        if (!state.myTurn) return
        val rank = state.selectedRank
        val targetId = state.selectedTargetId ?: state.activeOpponents.singleOrNull()?.id
        when {
            rank == null ->
                toast(T.toastChooseCard)
            rank !in Card.RANKS ->
                toast(T.toastNoSuchCard)
            state.myHand.none { it.rank == rank } ->
                toast(T.toastMustHold(rank))
            targetId == null ->
                toast(T.toastChooseOpponent)
            else -> {
                // WICHTIG: erst sperren, DANN senden (Online-Host verarbeitet inline).
                state.selectedRank = null
                state.selectedTargetId = null
                state.myTurn = false
                GameHolder.client?.sendAsk(rank, targetId)
            }
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

/** Ein Gegner am Tisch (für die N-Spieler-fähige UI). */
data class OpponentInfo(
    val id: Int,
    val name: String,
    val avatar: AvatarChoice,
    val handSize: Int,
    val books: List<String>,
    val active: Boolean
)

class GameUiState {
    var myAvatar         by mutableStateOf(AvatarChoice())
    var myId             by mutableStateOf(-1)
    /** Alle Gegner (1–3), in Sitzreihenfolge. */
    var opponents        by mutableStateOf<List<OpponentInfo>>(emptyList())
    /** Wer ist gerade am Zug (Spieler-ID). */
    var currentPlayerId  by mutableStateOf(-1)
    var deckSize         by mutableStateOf(0)
    val myHand: SnapshotStateList<Card> = mutableStateListOf()
    var myBooks          by mutableStateOf<List<String>>(emptyList())
    var myTurn           by mutableStateOf(false)
    var gameOver         by mutableStateOf(false)
    var winnerName       by mutableStateOf("")
    val logEntries: SnapshotStateList<LogEntry> = mutableStateListOf()
    var selectedRank: String? by mutableStateOf(null)
    /** Gewählter Frage-Gegner (nur nötig, wenn es mehr als einen aktiven Gegner gibt). */
    var selectedTargetId: Int? by mutableStateOf(null)

    /** Wenn != null, ist die Sitzung beendet (z.B. letzter Mitspieler hat verlassen). */
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
    /** Stammen die hervorgehobenen Karten aus einem Steal (grün) statt aus Go Fish (gelb)? */
    var highlightFromSteal: Boolean by mutableStateOf(false)

    /** Noch aktive Gegner (verlassene ausgenommen). */
    val activeOpponents: List<OpponentInfo> get() = opponents.filter { it.active }

    fun opponentById(id: Int): OpponentInfo? = opponents.firstOrNull { it.id == id }

    /** Startet eine Cue-Sequenz: erster wird aktiv, Rest in die Queue, Generation hoch. */
    fun playCueSequence(seq: List<AnimationCue>) {
        if (seq.isEmpty()) return
        cue = seq.first()
        pendingCues.clear()
        pendingCues.addAll(seq.drop(1))
        cueGeneration++
    }

    fun appendAction(isMe: Boolean, name: String, avatar: AvatarChoice, text: String) {
        logEntries.add(LogEntry.Action(isMe, name, avatar, text))
    }
    fun appendBook(isMe: Boolean, name: String, text: String) {
        logEntries.add(LogEntry.Book(isMe, name, text))
    }
    fun appendSystem(text: String) {
        logEntries.add(LogEntry.System(text))
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Log-Einträge & Ergebnis
// ─────────────────────────────────────────────────────────────────────────

/** Punktestand eines Spielers im Endbildschirm. */
data class PlayerScore(val name: String, val books: Int, val isMe: Boolean)

/** Ergebnis am Spielende — steuert das GameOver-Popup. */
sealed class GameResult {
    abstract val standings: List<PlayerScore>
    data class Win (override val standings: List<PlayerScore>) : GameResult()
    data class Lose(val winnerName: String, override val standings: List<PlayerScore>) : GameResult()
    data class Tie (override val standings: List<PlayerScore>) : GameResult()
}

sealed class LogEntry {
    /** Spieler-Aktion (Frage, Antwort etc.) — bekommt Avatar + fetten Namen. */
    data class Action(val isMe: Boolean, val name: String, val avatar: AvatarChoice, val text: String) : LogEntry()
    /** Spieler hat ein Buch abgelegt — bekommt 📚-Icon + fetten Namen. */
    data class Book(val isMe: Boolean, val name: String, val text: String) : LogEntry()
    /** System-Meldung — neutraler Text, kein Avatar. */
    data class System(val text: String) : LogEntry()
}

// ─────────────────────────────────────────────────────────────────────────
//  Hauptbildschirm
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun GameScreen(state: GameUiState, onExit: () -> Unit, onAsk: () -> Unit) {
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
            result    = gameResult,
            onExit    = onExit,
            onDismiss = { state.gameResult = null }
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
                // Sound passend zur Animation: GO_FISH/STEAL/BOOK entstehen nur für
                // eigene Aktionen. GIVE (eigene Karten werden geangelt) bleibt stumm.
                when (c.kind) {
                    AnimationCue.Kind.GO_FISH -> GameSounds.playGoFish()
                    AnimationCue.Kind.STEAL   -> GameSounds.playSteal(c.cards.size)
                    AnimationCue.Kind.BOOK    -> GameSounds.playBook()
                    AnimationCue.Kind.GIVE    -> {}
                }
                delay(ANIMATION_DURATION_MS)
                state.overlayVisible = false
                val candidateIds: Set<String> = when (c.kind) {
                    AnimationCue.Kind.GO_FISH -> c.drawnCard?.let { setOf(it.toString()) } ?: emptySet()
                    AnimationCue.Kind.STEAL   -> c.cards.map { it.toString() }.toSet()
                    AnimationCue.Kind.BOOK    -> emptySet()
                    AnimationCue.Kind.GIVE    -> emptySet()
                }
                // Nur tatsächlich noch in der Hand befindliche Karten hervorheben.
                // (Bei einem 3-Karten-Steal sind die Karten z.B. schon in einem Buch.)
                val actualIds = candidateIds.intersect(
                    state.myHand.map { it.toString() }.toSet()
                )
                if (actualIds.isNotEmpty()) {
                    state.highlightFromSteal = c.kind == AnimationCue.Kind.STEAL
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

    // Sound: „Du bist dran" — spielt, sobald der eigene Zug (neu) beginnt.
    LaunchedEffect(state.myTurn) {
        if (state.myTurn) GameSounds.playTurn()
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
                    GameIcon(
                        GameIconKind.ROD,
                        modifier = Modifier.size(26.dp),
                        tint = DeepSea
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text  = "Go Fish",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepSea
                    )
                    Spacer(Modifier.weight(1f))
                    ExitButton(onClick = { showExitDialog = true })
                }

                Spacer(Modifier.height(8.dp))

                // Gegner-Panel(s) — ab 2 Gegnern als antippbare Auswahl
                OpponentArea(
                    opponents        = state.opponents,
                    selectedTargetId = state.selectedTargetId,
                    selectable       = state.myTurn && !state.overlayVisible && state.activeOpponents.size >= 2,
                    onSelect         = { id -> GameSounds.playClick(); state.selectedTargetId = id }
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
                    entries  = state.logEntries,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(Modifier.height(10.dp))

                // Status / Turn Indicator
                TurnStatusBar(
                    myTurn            = state.myTurn,
                    gameOver          = state.gameOver,
                    winnerName        = state.winnerName,
                    currentPlayerName = state.opponentById(state.currentPlayerId)?.name ?: ""
                )

                Spacer(Modifier.height(10.dp))

                // Hand — 6 Spalten Grid, vertikal scrollbar
                HandGrid(
                    cards            = state.myHand,
                    selectedRank     = state.selectedRank,
                    highlightedCards = state.highlightedCards,
                    highlightStolen  = state.highlightFromSteal,
                    enabled          = state.myTurn && !state.overlayVisible,
                    onSelect         = { rank ->
                        GameSounds.playCardSelect()
                        state.selectedRank = rank
                    }
                )

                Spacer(Modifier.height(10.dp))

                // Frage-Button
                AskButton(
                    selectedRank = state.selectedRank,
                    enabled      = state.myTurn && !state.overlayVisible,
                    onAsk        = onAsk
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

/** Bereich oben: 1 Gegner = volle Breite; 2–3 Gegner = nebeneinander, je antippbar. */
@Composable
private fun OpponentArea(
    opponents: List<OpponentInfo>,
    selectedTargetId: Int?,
    selectable: Boolean,
    onSelect: (Int) -> Unit
) {
    when {
        opponents.isEmpty() -> Unit
        opponents.size == 1 -> OpponentPanel(
            opp = opponents.first(),
            selected = false,
            selectable = false,
            onClick = {}
        )
        else -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opponents.forEach { o ->
                OpponentPanel(
                    opp = o,
                    selected = selectable && o.id == selectedTargetId,
                    selectable = selectable && o.active,
                    onClick = { onSelect(o.id) },
                    compact = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OpponentPanel(
    opp: OpponentInfo,
    selected: Boolean,
    selectable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val t = LocalTexts.current
    val bg = when {
        !opp.active -> OceanDeep.copy(alpha = 0.40f)          // verlassen → ausgegraut
        selected    -> SeafoamDeep                            // ausgewähltes Ziel
        else        -> LavenderDeep.copy(alpha = 0.85f)
    }
    val fg = Foam.copy(alpha = if (opp.active) 1f else 0.55f)
    val panelMod = (if (compact) modifier else modifier.fillMaxWidth())
        .then(if (selectable) Modifier.clickable { onClick() } else Modifier)

    BubblePanel(modifier = panelMod, background = bg) {
        if (compact) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Avatar(
                    choice = opp.avatar,
                    modifier = Modifier.size(width = 54.dp, height = 42.dp),
                    facingRight = true,
                    animated = opp.active && selected
                )
                Spacer(Modifier.height(4.dp))
                Text(opp.name, style = MaterialTheme.typography.titleSmall, color = fg, maxLines = 1)
                IconText(
                    t.handAndBooks(opp.handSize, opp.books.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = fg
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    choice = opp.avatar,
                    modifier = Modifier.size(width = 78.dp, height = 60.dp),
                    facingRight = true,
                    animated = opp.active
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(opp.name, style = MaterialTheme.typography.headlineMedium, color = fg)
                    IconText(
                        t.handAndBooks(opp.handSize, opp.books.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = fg
                    )
                    if (opp.books.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        BookChips(opp.books, chipColor = SunYellow)
                    }
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
            IconText(LocalTexts.current.deckBadge, style = MaterialTheme.typography.titleMedium, color = Foam)
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
                IconText("📚 ${books.size}", color = DeepSea, style = TextStyle(fontWeight = FontWeight.Bold))
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
                IconText(
                    LocalTexts.current.logEmpty,
                    color = SoftSeaText,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
            entries.forEach { entry -> LogRow(entry) }
        }
    }
}

@Composable
private fun LogRow(entry: LogEntry) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        when (entry) {
            is LogEntry.Action -> {
                Avatar(
                    choice      = entry.avatar,
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
                    GameIcon(GameIconKind.BOOKS, modifier = Modifier.size(18.dp), tint = DeepSea)
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
                IconText(
                    text = entry.text,
                    color = SoftSeaText,
                    style = TextStyle(fontSize = 12.sp, lineHeight = 16.sp)
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
    currentPlayerName: String
) {
    val t = LocalTexts.current
    val winnerDisplay = if (winnerName.equals(TIE_SENTINEL, ignoreCase = true)) t.drawWord else winnerName
    val (bg, fg, text) = when {
        gameOver -> Triple(SunYellow, DeepSea, t.turnGameOver(winnerDisplay))
        myTurn   -> Triple(SeafoamGreen, DeepSea, t.turnYou)
        else     -> Triple(Lavender, DeepSea, t.turnWaiting(currentPlayerName))
    }
    BubblePanel(
        modifier = Modifier.fillMaxWidth(),
        background = bg
    ) {
        IconText(
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
    highlightStolen: Boolean,
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
                    IconText(
                        LocalTexts.current.scrollHint,
                        color = SoftSeaText,
                        style = TextStyle(fontSize = 11.sp),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                IconText(
                    "🃏 ${cards.size}",
                    color = DeepSea,
                    style = TextStyle(fontWeight = FontWeight.Bold)
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
                            rank            = card.rank,
                            suit            = card.suit,
                            selected        = enabled && card.rank == selectedRank,
                            highlighted     = card.toString() in highlightedCards,
                            highlightStolen = highlightStolen,
                            modifier        = Modifier.aspectRatio(0.70f),
                            onClick         = if (enabled) {
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
            onClick = { GameSounds.playClick(); onAsk() },
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
            GameIcon(GameIconKind.ROD, modifier = Modifier.size(24.dp), tint = DeepSea)
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
        onClick = { GameSounds.playClick(); onClick() },
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = CoralPink,
            contentColor   = DeepSea
        )
    ) {
        GameIcon(GameIconKind.CLOSE, modifier = Modifier.size(18.dp), tint = DeepSea)
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
                onClick = { GameSounds.playClick(); onConfirm() },
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
                onClick = { GameSounds.playClick(); onDismiss() },
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
            IconText(
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
                onClick = { GameSounds.playClick(); onConfirm() },
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
    val icon: GameIconKind,
    val accent: Color,
    val accentDeep: Color,
    val panelBg: Color
)

private fun styleFor(result: GameResult): ResultStyle = when (result) {
    is GameResult.Win -> ResultStyle(
        icon = GameIconKind.TROPHY,
        accent = SunYellow, accentDeep = SunDeep,
        panelBg = SunYellow.copy(alpha = 0.30f)
    )
    is GameResult.Lose -> ResultStyle(
        icon = GameIconKind.HOOK,
        accent = Lavender, accentDeep = LavenderDeep,
        panelBg = Lavender.copy(alpha = 0.35f)
    )
    is GameResult.Tie -> ResultStyle(
        icon = GameIconKind.HANDSHAKE,
        accent = SeafoamGreen, accentDeep = SeafoamDeep,
        panelBg = SeafoamGreen.copy(alpha = 0.30f)
    )
}

@Composable
private fun GameOverDialog(
    result: GameResult,
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

    // Ergebnis-Jingle exakt beim Erscheinen des Popups (Unentschieden bleibt still).
    LaunchedEffect(Unit) {
        when (result) {
            is GameResult.Win  -> GameSounds.playWinner()
            is GameResult.Lose -> GameSounds.playLoser()
            is GameResult.Tie  -> {}
        }
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
                // Großes Ergebnis-Icon
                GameIcon(
                    style.icon,
                    modifier = Modifier.size(76.dp),
                    tint = style.accentDeep
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
                        result.standings.forEachIndexed { i, ps ->
                            if (i > 0) VerticalDivider()
                            ScoreCell(label = ps.name, count = ps.books)
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = { GameSounds.playClick(); onExit() },
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
                    GameIcon(GameIconKind.HOME, modifier = Modifier.size(22.dp), tint = Foam)
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
            GameIcon(GameIconKind.BOOKS, modifier = Modifier.size(24.dp), tint = DeepSea)
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

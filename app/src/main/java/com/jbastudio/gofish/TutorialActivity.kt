package com.jbastudio.gofish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jbastudio.gofish.i18n.LocalTexts
import com.jbastudio.gofish.i18n.Texts
import com.jbastudio.gofish.i18n.textsFor
import com.jbastudio.gofish.model.Card
import com.jbastudio.gofish.game.AskResult
import com.jbastudio.gofish.game.GameEngine
import com.jbastudio.gofish.ui.components.ANIMATION_DURATION_MS
import com.jbastudio.gofish.ui.components.AnimationCue
import com.jbastudio.gofish.ui.components.GameAnimationOverlay
import com.jbastudio.gofish.ui.components.HIGHLIGHT_DURATION_MS
import com.jbastudio.gofish.ui.components.Avatar
import com.jbastudio.gofish.ui.components.AvatarChoice
import com.jbastudio.gofish.ui.components.AvatarColor
import com.jbastudio.gofish.ui.components.AvatarKind
import com.jbastudio.gofish.ui.components.BubblePanel
import com.jbastudio.gofish.ui.components.FishMascot
import com.jbastudio.gofish.ui.components.GameIcon
import com.jbastudio.gofish.ui.components.GameIconKind
import com.jbastudio.gofish.ui.components.IconText
import com.jbastudio.gofish.ui.components.OceanBackground
import com.jbastudio.gofish.ui.components.PlayingCardView
import com.jbastudio.gofish.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Vollständig geskriptetes, deterministisches Tutorial gegen den vorprogrammierten
 * Gegner „Fisherman". Kein echtes Spiel/Engine/Netzwerk — jede Szene ist ein
 * fest gebauter Brett-Schnappschuss + Coach-Text + Interaktion. So sind alle
 * Ereignisse (Angeln/Go Fish, 1/2/3 Karten angeln, Buch, Gegnerzug) garantiert
 * vorführbar. Reine, öffentliche UI-Komponenten werden wiederverwendet
 * (PlayingCardView, Avatar, BubblePanel, …), damit es wie das echte Spiel aussieht.
 *
 * Danach (nach „Besiege deinen Gegner!") folgt eine FREIE Partie gegen den
 * Fisherman auf Basis der echten GameEngine inkl. der Spiel-Animationen
 * (GameAnimationOverlay); jederzeit über das X oben rechts verlassbar.
 */
class TutorialActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        GameSounds.init(this)
        val texts = textsFor(LanguagePrefs(this).load())
        // Gegner-Avatar zufällig, Name fest „Fisherman".
        val fisherman = AvatarChoice(AvatarKind.values().random(), AvatarColor.values().random())
        val playerName = NamePrefs(this).load()
        setContent {
            GoFishTheme {
                CompositionLocalProvider(LocalTexts provides texts) {
                    TutorialScreen(fisherman = fisherman, playerName = playerName, onExit = { finish() })
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Skript-Modell
// ─────────────────────────────────────────────────────────────────────────

/** Was der Spieler in dieser Szene tun muss, um fortzufahren. */
private sealed interface Gate {
    /** „Weiter"-Button. */
    object Advance : Gate
    /** Markierte Karte dieses Werts antippen. */
    data class TapCard(val rank: String) : Gate
    /** Den „Fragen"-Button antippen (Wert ist vorausgewählt). */
    data class TapAsk(val rank: String) : Gate
    /** Abschluss — „Verlassen"-Button. */
    object End : Gate
}

private class Board(
    val myHand: List<Card>,
    val oppHandCount: Int,
    val myBooks: List<String>,
    val oppBooks: List<String>,
    val deckCount: Int,
)

private class Scene(
    val board: Board,
    val coach: (Texts) -> String,
    val gate: Gate,
)

/** Baut die feste Szenenfolge. Karten sind eindeutig (Wert+Farbe), Ablauf deterministisch. */
private fun buildScenes(): List<Scene> {
    fun cd(r: String, s: String) = Card(r, s)

    // Hand-Entwicklung über den Tutorial-Verlauf:
    val h0  = listOf(cd("7","♠"), cd("7","♥"), cd("9","♠"), cd("4","♠"),
                     cd("Q","♠"), cd("K","♠"), cd("K","♥"), cd("J","♠"))      // Start (8)
    val h1  = h0 + cd("7","♦")                                                // +1 gestohlene 7
    val h2  = h1 + cd("9","♥") + cd("9","♦")                                  // +2 gestohlene 9er
    val h3a = h2 + cd("4","♥") + cd("4","♦") + cd("4","♣")                    // +3 gestohlene 4er → vier 4er
    val h3b = h3a.filterNot { it.rank == "4" }                               // Buch 4 abgelegt
    val h4  = h3b + cd("2","♣")                                               // Go Fish: eine 2 gezogen
    val h5  = h4.filterNot { it == cd("K","♠") || it == cd("K","♥") }         // Gegner nimmt 2 Könige

    val noBooks = emptyList<String>()
    val book4   = listOf("4")

    return listOf(
        // Einführung & Ziel
        Scene(Board(h0, 8, noBooks, noBooks, 36), { it.tutorialIntro },        Gate.Advance),
        Scene(Board(h0, 8, noBooks, noBooks, 36), { it.tutorialAskIntro },     Gate.Advance),
        // Angeln nach 7ern → 1 Karte gestohlen
        Scene(Board(h0, 8, noBooks, noBooks, 36), { it.tutorialPickCard },     Gate.TapCard("7")),
        Scene(Board(h0, 8, noBooks, noBooks, 36), { it.tutorialPressAsk },     Gate.TapAsk("7")),
        Scene(Board(h1, 7, noBooks, noBooks, 36), { it.tutorialStole1("7") },  Gate.Advance),
        // Angeln nach 9ern → 2 Karten gestohlen
        Scene(Board(h1, 7, noBooks, noBooks, 36), { it.tutorialPickCard },     Gate.TapCard("9")),
        Scene(Board(h1, 7, noBooks, noBooks, 36), { it.tutorialPressAsk },     Gate.TapAsk("9")),
        Scene(Board(h2, 5, noBooks, noBooks, 36), { it.tutorialStole2("9") },  Gate.Advance),
        // Angeln nach 4ern → 3 Karten gestohlen → Buch
        Scene(Board(h2, 5, noBooks, noBooks, 36), { it.tutorialPickCard },     Gate.TapCard("4")),
        Scene(Board(h2, 5, noBooks, noBooks, 36), { it.tutorialPressAsk },     Gate.TapAsk("4")),
        Scene(Board(h3a, 2, noBooks, noBooks, 36), { it.tutorialStole3("4") }, Gate.Advance),
        Scene(Board(h3b, 2, book4, noBooks, 36),  { it.tutorialBook("4") },    Gate.Advance),
        // Go Fish nach Damen
        Scene(Board(h3b, 2, book4, noBooks, 36),  { it.tutorialGoFishIntro },  Gate.Advance),
        Scene(Board(h3b, 2, book4, noBooks, 36),  { it.tutorialPickCard },     Gate.TapCard("Q")),
        Scene(Board(h3b, 2, book4, noBooks, 36),  { it.tutorialPressAsk },     Gate.TapAsk("Q")),
        Scene(Board(h4, 2, book4, noBooks, 35),   { it.tutorialGoFish("Q") },  Gate.Advance),
        // Gegnerzug + Übergabe
        Scene(Board(h5, 4, book4, noBooks, 35),   { it.tutorialOppTurn },      Gate.Advance),
        Scene(Board(h5, 4, book4, noBooks, 35),   { it.tutorialFinish },       Gate.End),
    )
}

// ─────────────────────────────────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun TutorialScreen(fisherman: AvatarChoice, playerName: String, onExit: () -> Unit) {
    // Erst das geskriptete Tutorial; nach „Besiege deinen Gegner!" geht es ins freie Spiel.
    var freePlay by remember { mutableStateOf(false) }
    if (freePlay) {
        FreePlayGame(fisherman = fisherman, playerName = playerName, onExit = onExit)
    } else {
        ScriptedTutorial(fisherman = fisherman, onExit = onExit, onDone = { freePlay = true })
    }
}

@Composable
private fun ScriptedTutorial(fisherman: AvatarChoice, onExit: () -> Unit, onDone: () -> Unit) {
    val t = LocalTexts.current
    val scenes = remember { buildScenes() }
    var index by remember { mutableStateOf(0) }
    val scene = scenes[index]
    val board = scene.board
    val gate = scene.gate
    val advance: () -> Unit = { if (index < scenes.lastIndex) index += 1 }

    // System-Zurück verlässt das Tutorial direkt (frei verlassbar).
    BackHandler { onExit() }

    val tapRank = (gate as? Gate.TapCard)?.rank
    val askRank = (gate as? Gate.TapAsk)?.rank

    OceanBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Kopfzeile: Titel + Schließen
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    t.tutorialBtn,
                    style = MaterialTheme.typography.headlineLarge,
                    color = DeepSea,
                    modifier = Modifier.weight(1f)
                )
                BubblePanel(background = CoralPink, cornerRadius = 14.dp, onClick = { GameSounds.playClick(); onExit() }) {
                    Box(Modifier.padding(10.dp)) {
                        GameIcon(GameIconKind.CLOSE, modifier = Modifier.size(22.dp), tint = DeepSea)
                    }
                }
            }

            // Gegner
            TutOpponent(avatar = fisherman, handCount = board.oppHandCount, books = board.oppBooks)

            // Stapel + eigene Bücher
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TutBadge(title = t.deckBadge, value = board.deckCount.toString(), bg = OceanDeep)
                TutBadge(title = t.yourBooks, value = "📚 ${board.myBooks.size}", bg = SeafoamDeep)
            }

            // Coach-Karte (die Führung)
            BubblePanel(background = Foam, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FishMascot(
                            modifier = Modifier.size(width = 66.dp, height = 50.dp),
                            body = SeafoamGreen, bodyDeep = SeafoamDeep, facingRight = true
                        )
                        Spacer(Modifier.width(12.dp))
                        IconText(
                            scene.coach(t),
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepSea,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    when (gate) {
                        is Gate.Advance -> {
                            Spacer(Modifier.height(12.dp))
                            TutCoachButton(t.tutorialNext, SeafoamGreen, onClick = advance)
                        }
                        is Gate.End -> {
                            Spacer(Modifier.height(12.dp))
                            TutCoachButton(t.tutorialPlayFree, SunYellow, onClick = onDone)
                        }
                        else -> Unit  // TapCard/TapAsk: die markierte Karte bzw. „Fragen" führt weiter
                    }
                }
            }

            // Eigene Hand (markierte Karte ist antippbar, sonst gesperrt)
            val sorted = board.myHand.sortedBy { Card.RANKS.indexOf(it.rank) }
            Text(
                t.yourHand,
                style = MaterialTheme.typography.titleMedium,
                color = DeepSea
            )
            sorted.chunked(6).forEach { rowCards ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    rowCards.forEach { card ->
                        val isTap = card.rank == tapRank
                        val isSel = card.rank == askRank
                        PlayingCardView(
                            rank = card.rank,
                            suit = card.suit,
                            modifier = Modifier.weight(1f).aspectRatio(0.70f),
                            selected = isSel,
                            highlighted = isTap,
                            onClick = if (isTap) ({ GameSounds.playClick(); advance() }) else null
                        )
                    }
                    repeat(6 - rowCards.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            // „Fragen"-Button — nur in den Frage-Szenen aktiv & hervorgehoben
            TutAskButton(
                label = if (askRank != null) t.askFor(askRank) else t.chooseCard,
                enabled = askRank != null,
                highlighted = askRank != null,
                onClick = advance
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Bausteine (eigen, da PastelButton in MainActivity privat ist)
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun TutOpponent(avatar: AvatarChoice, handCount: Int, books: List<String>) {
    val t = LocalTexts.current
    BubblePanel(background = LavenderDeep, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                choice = avatar,
                modifier = Modifier.size(width = 78.dp, height = 60.dp),
                facingRight = true
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Fisherman", style = MaterialTheme.typography.headlineMedium, color = Foam)
                IconText(
                    t.handAndBooks(handCount, books.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Foam
                )
            }
        }
    }
}

@Composable
private fun RowScope.TutBadge(title: String, value: String, bg: androidx.compose.ui.graphics.Color) {
    BubblePanel(background = bg, modifier = Modifier.weight(1f)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconText(title, style = MaterialTheme.typography.bodyMedium, color = Foam)
            IconText(value, style = MaterialTheme.typography.titleLarge, color = Foam)
        }
    }
}

@Composable
private fun TutCoachButton(label: String, container: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    BubblePanel(background = container, cornerRadius = 16.dp, onClick = { GameSounds.playClick(); onClick() }) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = DeepSea)
        }
    }
}

@Composable
private fun TutAskButton(label: String, enabled: Boolean, highlighted: Boolean, onClick: () -> Unit) {
    Button(
        onClick = { GameSounds.playClick(); onClick() },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .then(if (highlighted) Modifier.border(3.dp, SunDeep, RoundedCornerShape(18.dp)) else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CoralPink,
            contentColor = DeepSea,
            disabledContainerColor = CoralPink.copy(alpha = 0.4f),
            disabledContentColor = DeepSea.copy(alpha = 0.4f)
        )
    ) {
        GameIcon(GameIconKind.ROD, modifier = Modifier.size(22.dp), tint = DeepSea)
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = DeepSea, textAlign = TextAlign.Center)
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Freies Spiel gegen den Fisherman — echte GameEngine + die Spiel-Animationen
//  (GameAnimationOverlay). Start nach „Besiege deinen Gegner!". Verlassen jederzeit
//  über das X oben rechts.
// ─────────────────────────────────────────────────────────────────────────

private const val TUT_ME = 0
private const val TUT_BOT = 1

/** Compose-beobachtbarer Brett-Zustand (gespiegelt aus der GameEngine). */
private class FreeGameUi {
    var myHand by mutableStateOf<List<Card>>(emptyList())
    var oppCount by mutableStateOf(0)
    var myBooks by mutableStateOf<List<String>>(emptyList())
    var oppBooks by mutableStateOf<List<String>>(emptyList())
    var deckCount by mutableStateOf(0)
    var myTurn by mutableStateOf(true)
    var selectedRank by mutableStateOf<String?>(null)
    var cue by mutableStateOf<AnimationCue?>(null)
    var overlayVisible by mutableStateOf(false)
    var highlighted by mutableStateOf<Set<String>>(emptySet())
    var highlightStolen by mutableStateOf(false)
    var busy by mutableStateOf(false)
    var finished by mutableStateOf(false)
    var iWon by mutableStateOf(false)
    var tie by mutableStateOf(false)
}

/** Brett aus der Engine in den Compose-State spiegeln. */
private fun FreeGameUi.snapshot(e: GameEngine) {
    myHand    = e.getPlayer(TUT_ME).hand.sortedBy { Card.RANKS.indexOf(it.rank) }
    oppCount  = e.getPlayer(TUT_BOT).hand.size
    oppBooks  = e.getPlayer(TUT_BOT).books.toList()
    myBooks   = e.getPlayer(TUT_ME).books.toList()
    deckCount = e.deckSize()
    myTurn    = !e.gameOver && e.currentPlayerId == TUT_ME
}

/** Spielt genau einen Cue mit denselben Timings/Sounds/Highlights wie das echte Spiel. */
private suspend fun FreeGameUi.runCue(c: AnimationCue) {
    cue = c
    overlayVisible = true
    when (c.kind) {
        AnimationCue.Kind.GO_FISH -> GameSounds.playGoFish()
        AnimationCue.Kind.STEAL   -> GameSounds.playSteal(c.cards.size)
        AnimationCue.Kind.BOOK    -> GameSounds.playBook()
        AnimationCue.Kind.GIVE    -> {}
    }
    delay(ANIMATION_DURATION_MS)
    overlayVisible = false
    val ids = when (c.kind) {
        AnimationCue.Kind.GO_FISH -> c.drawnCard?.let { setOf(it.toString()) } ?: emptySet()
        AnimationCue.Kind.STEAL   -> c.cards.map { it.toString() }.toSet()
        else                      -> emptySet()
    }.intersect(myHand.map { it.toString() }.toSet())
    if (ids.isNotEmpty()) {
        highlightStolen = c.kind == AnimationCue.Kind.STEAL
        highlighted = ids
        delay(HIGHLIGHT_DURATION_MS)
        highlighted = emptySet()
    }
}

@Composable
private fun FreePlayGame(fisherman: AvatarChoice, playerName: String, onExit: () -> Unit) {
    val t = LocalTexts.current
    val engine = remember {
        GameEngine().apply {
            addPlayer(TUT_ME, playerName.ifBlank { "Du" })
            addPlayer(TUT_BOT, "Fisherman")
            startGame()
        }
    }
    val ui = remember { FreeGameUi().also { it.snapshot(engine) } }
    val scope = rememberCoroutineScope()

    BackHandler { onExit() }

    fun markIfOver(res: AskResult) {
        if (res.gameOver) {
            ui.finished = true
            ui.tie = res.winnerName == null
            ui.iWon = res.winnerName == engine.getPlayer(TUT_ME).name
        }
    }

    fun onAsk() {
        val rank = ui.selectedRank ?: return
        if (ui.busy || !ui.myTurn || ui.finished) return
        ui.busy = true
        ui.selectedRank = null
        scope.launch {
            // ── Mein Zug ──
            val res = engine.processAsk(TUT_ME, rank)
            val cues = buildList {
                if (res.cardsReceived.isNotEmpty())
                    add(AnimationCue(AnimationCue.Kind.STEAL, rank, cards = res.cardsReceived))
                else
                    add(AnimationCue(AnimationCue.Kind.GO_FISH, rank, drawnCard = res.drawnCard))
                res.newBooksForAsker.forEach { add(AnimationCue(AnimationCue.Kind.BOOK, rank = it)) }
            }
            ui.snapshot(engine)
            cues.forEach { ui.runCue(it) }
            markIfOver(res)

            // ── Fisherman-Züge (solange er am Zug bleibt) ──
            while (!ui.finished && !engine.gameOver && engine.currentPlayerId == TUT_BOT) {
                delay(600)                                   // kurze „Denkpause"
                val botHand = engine.getPlayer(TUT_BOT).hand
                if (botHand.isEmpty()) break
                val botRank = botHand.map { it.rank }.distinct().random()
                val takenFromMe = engine.getPlayer(TUT_ME).hand.filter { it.rank == botRank }
                val r = engine.processAsk(TUT_BOT, botRank)
                ui.snapshot(engine)
                if (r.cardsReceived.isNotEmpty() && takenFromMe.isNotEmpty())
                    ui.runCue(AnimationCue(AnimationCue.Kind.GIVE, botRank, cards = takenFromMe))
                else
                    delay(350)
                markIfOver(r)
            }
            ui.busy = false
        }
    }

    // „Du bist dran"-Sound, sobald der eigene Zug (neu) beginnt.
    LaunchedEffect(ui.myTurn) { if (ui.myTurn && !ui.finished) GameSounds.playTurn() }

    val canTap = ui.myTurn && !ui.busy && !ui.overlayVisible && !ui.finished

    Box(Modifier.fillMaxSize()) {
        OceanBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Kopfzeile mit X (jederzeit verlassbar)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        t.tutorialBtn,
                        style = MaterialTheme.typography.headlineLarge,
                        color = DeepSea,
                        modifier = Modifier.weight(1f)
                    )
                    BubblePanel(background = CoralPink, cornerRadius = 14.dp, onClick = { GameSounds.playClick(); onExit() }) {
                        Box(Modifier.padding(10.dp)) {
                            GameIcon(GameIconKind.CLOSE, modifier = Modifier.size(22.dp), tint = DeepSea)
                        }
                    }
                }

                TutOpponent(avatar = fisherman, handCount = ui.oppCount, books = ui.oppBooks)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TutBadge(title = t.deckBadge, value = ui.deckCount.toString(), bg = OceanDeep)
                    TutBadge(title = t.yourBooks, value = "📚 ${ui.myBooks.size}", bg = SeafoamDeep)
                }

                // Zug-Hinweis
                BubblePanel(background = Foam, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FishMascot(
                            modifier = Modifier.size(width = 60.dp, height = 46.dp),
                            body = SeafoamGreen, bodyDeep = SeafoamDeep, facingRight = true
                        )
                        Spacer(Modifier.width(12.dp))
                        IconText(
                            if (ui.myTurn) t.turnYou else t.turnWaiting("Fisherman"),
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepSea,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Eigene Hand
                Text(t.yourHand, style = MaterialTheme.typography.titleMedium, color = DeepSea)
                ui.myHand.chunked(6).forEach { rowCards ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        rowCards.forEach { card ->
                            PlayingCardView(
                                rank = card.rank,
                                suit = card.suit,
                                modifier = Modifier.weight(1f).aspectRatio(0.70f),
                                selected = card.rank == ui.selectedRank,
                                highlighted = card.toString() in ui.highlighted,
                                highlightStolen = ui.highlightStolen,
                                onClick = if (canTap) ({ GameSounds.playClick(); ui.selectedRank = card.rank }) else null
                            )
                        }
                        repeat(6 - rowCards.size) { Spacer(Modifier.weight(1f)) }
                    }
                }

                // „Fragen"-Button
                TutAskButton(
                    label = if (ui.selectedRank != null) t.askFor(ui.selectedRank!!) else t.chooseCard,
                    enabled = canTap && ui.selectedRank != null,
                    highlighted = ui.selectedRank != null,
                    onClick = { onAsk() }
                )
            }
        }

        // Spiel-Animation über allem (identisch zum echten Spiel)
        val cue = ui.cue
        if (ui.overlayVisible && cue != null) {
            GameAnimationOverlay(cue = cue)
        }

        // Ergebnis
        if (ui.finished) {
            FreePlayResult(iWon = ui.iWon, tie = ui.tie, onLeave = onExit)
        }
    }
}

@Composable
private fun FreePlayResult(iWon: Boolean, tie: Boolean, onLeave: () -> Unit) {
    val t = LocalTexts.current
    val (title, subtitle, container) = when {
        tie  -> Triple(t.tieTitle,  t.tieSubtitle,               Lavender)
        iWon -> Triple(t.winTitle,  t.winSubtitle,               SunYellow)
        else -> Triple(t.loseTitle, t.loseSubtitle("Fisherman"), CoralPink)
    }
    Box(
        modifier = Modifier.fillMaxSize().background(DeepSea.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        BubblePanel(
            background = Foam,
            cornerRadius = 28.dp,
            modifier = Modifier.fillMaxWidth().padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(title, style = MaterialTheme.typography.headlineLarge, color = DeepSea)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftSeaText,
                    textAlign = TextAlign.Center
                )
                TutCoachButton(t.tutorialLeave, container, onClick = onLeave)
            }
        }
    }
}

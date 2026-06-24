package com.jbastudio.gofish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

/**
 * Vollständig geskriptetes, deterministisches Tutorial gegen den vorprogrammierten
 * Gegner „Fisherman". Kein echtes Spiel/Engine/Netzwerk — jede Szene ist ein
 * fest gebauter Brett-Schnappschuss + Coach-Text + Interaktion. So sind alle
 * Ereignisse (Angeln/Go Fish, 1/2/3 Karten angeln, Buch, Gegnerzug) garantiert
 * vorführbar. Reine, öffentliche UI-Komponenten werden wiederverwendet
 * (PlayingCardView, Avatar, BubblePanel, …), damit es wie das echte Spiel aussieht.
 */
class TutorialActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        GameSounds.init(this)
        val texts = textsFor(LanguagePrefs(this).load())
        // Gegner-Avatar zufällig, Name fest „Fisherman".
        val fisherman = AvatarChoice(AvatarKind.values().random(), AvatarColor.values().random())
        setContent {
            GoFishTheme {
                CompositionLocalProvider(LocalTexts provides texts) {
                    TutorialScreen(fisherman = fisherman, onExit = { finish() })
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
private fun TutorialScreen(fisherman: AvatarChoice, onExit: () -> Unit) {
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
                            TutCoachButton(t.tutorialLeave, SunYellow, onClick = onExit)
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

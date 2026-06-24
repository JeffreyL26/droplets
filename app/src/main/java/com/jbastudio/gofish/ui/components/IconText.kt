package com.jbastudio.gofish.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.jbastudio.gofish.ui.theme.DeepSea

/**
 * Zuordnung Emoji → selbst gezeichnetes [GameIconKind].
 *
 * Reihenfolge: Varianten MIT Variation-Selector (️, U+FE0F) zuerst, damit sie
 * vor den blanken Formen greifen.
 */
private val EMOJI_ICONS: List<Pair<String, GameIconKind>> = listOf(
    "🛰️" to GameIconKind.SATELLITE, "🛰" to GameIconKind.SATELLITE,
    "✏️" to GameIconKind.PENCIL,     "✏" to GameIconKind.PENCIL,
    "⬆️" to GameIconKind.ARROW_UP,   "⬆" to GameIconKind.ARROW_UP,
    "⬅️" to GameIconKind.ARROW_LEFT, "⬅" to GameIconKind.ARROW_LEFT, "←" to GameIconKind.ARROW_LEFT,
    "↕️" to GameIconKind.SCROLL,     "↕" to GameIconKind.SCROLL,
    "🌐" to GameIconKind.GLOBE,
    "🏠" to GameIconKind.HOME,
    "🎣" to GameIconKind.ROD,
    "🌊" to GameIconKind.WAVE,
    "🐟" to GameIconKind.FISH,
    "✋" to GameIconKind.HAND,
    "🔎" to GameIconKind.SEARCH,
    "✓" to GameIconKind.CHECK,
    "✕" to GameIconKind.CLOSE,
    "🎴" to GameIconKind.DECK,
    "🃏" to GameIconKind.CARD,
    "📚" to GameIconKind.BOOKS,
    "⏳" to GameIconKind.HOURGLASS,
    "🏆" to GameIconKind.TROPHY,
    "🪝" to GameIconKind.HOOK,
    "🤝" to GameIconKind.HANDSHAKE,
    "🎬" to GameIconKind.CLAPPER,
    "🏁" to GameIconKind.FLAG_CHECKERED,
    "🎲" to GameIconKind.DICE,
    "🎓" to GameIconKind.GRAD_CAP
)

/** Liefert das passende [GameIconKind] zu einem (einzelnen) Emoji, oder null. */
fun iconKindForEmoji(emoji: String): GameIconKind? {
    val e = emoji.trim()
    return EMOJI_ICONS.firstOrNull { it.first == e }?.second
        ?: EMOJI_ICONS.firstOrNull { it.first == e.replace("️", "") }?.second
}

/**
 * Wie [Text], ersetzt aber bekannte Emojis im Text durch passende, inline
 * eingebettete [GameIcon]s gleicher Größe (skaliert an der Schriftgröße).
 *
 * So bleiben die (mehrsprachigen) Strings unverändert — die Icons erscheinen
 * an genau der Stelle, an der das Emoji stand.
 */
@Composable
fun IconText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = DeepSea,
    textAlign: TextAlign? = null,
    iconScale: Float = 1.05f
) {
    val fontSize: TextUnit = if (style.fontSize != TextUnit.Unspecified) style.fontSize else 14.sp
    val iconSize = fontSize * iconScale

    val annotated = buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val match = EMOJI_ICONS.firstOrNull { text.startsWith(it.first, i) }
            if (match != null) {
                appendInlineContent(match.first, match.first)
                i += match.first.length
            } else {
                append(text[i]); i++
            }
        }
    }

    val inlineContent = EMOJI_ICONS.associate { (emoji, kind) ->
        emoji to InlineTextContent(
            Placeholder(iconSize, iconSize, PlaceholderVerticalAlign.Center)
        ) {
            GameIcon(kind, modifier = Modifier.fillMaxSize(), tint = color)
        }
    }

    Text(
        text = annotated,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        inlineContent = inlineContent
    )
}

package com.jbastudio.gofish.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbastudio.gofish.i18n.LocalTexts
import com.jbastudio.gofish.model.Card
import com.jbastudio.gofish.ui.theme.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

const val ANIMATION_DURATION_MS = 2300L
const val HIGHLIGHT_DURATION_MS = 1400L

private val SmoothEasing = CubicBezierEasing(0.22f, 1.00f, 0.36f, 1.00f)

// ─────────────────────────────────────────────────────────────────────────
//  Public API
// ─────────────────────────────────────────────────────────────────────────

data class AnimationCue(
    val kind: Kind,
    val rank: String,
    val cards: List<Card> = emptyList(),
    val drawnCard: Card? = null,
    val nonce: Long = System.nanoTime()
) {
    enum class Kind { GO_FISH, STEAL, BOOK, GIVE }
}

@Composable
fun GameAnimationOverlay(cue: AnimationCue) {
    val progress = remember(cue.nonce) { Animatable(0f) }
    LaunchedEffect(cue.nonce) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = ANIMATION_DURATION_MS.toInt(),
                easing = SmoothEasing
            )
        )
    }
    val measurer = rememberTextMeasurer()
    val t = LocalTexts.current
    val p = progress.value
    val envelope = fadeEnvelope(p)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stage = stageFor(size.width, size.height)

            // Vignette — kräftiges Abdunkeln, klarer Fokus auf die Szene
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, DeepSea.copy(alpha = 0.78f * envelope)),
                    center = Offset(stage.centerX, stage.centerY),
                    radius = max(size.width, size.height) * 0.85f
                )
            )

            when (cue.kind) {
                AnimationCue.Kind.GO_FISH -> drawAnglerScene(cue, p, stage, measurer)
                AnimationCue.Kind.STEAL   -> drawPovRodScene(cue, p, stage, measurer)
                AnimationCue.Kind.BOOK    -> drawBookScene(cue, p, stage, measurer)
                AnimationCue.Kind.GIVE    -> drawGiveScene(cue, p, stage, measurer)
            }
        }

        // Splash-Texte — pro Cue eigener Stil
        when (cue.kind) {
            AnimationCue.Kind.GO_FISH -> {
                val subtitle = if (cue.drawnCard != null) t.animDrawCard else t.animDeckEmpty
                ComicGoFishSplash(
                    subtitle = subtitle,
                    progress = p,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-90).dp)
                )
            }
            AnimationCue.Kind.STEAL -> {
                // Unter der Angel-Animation; eigener Stil + Decorations nach Kartenzahl
                StealSplash(
                    rank     = cue.rank,
                    count    = cue.cards.size,
                    progress = p,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 130.dp)
                )
            }
            AnimationCue.Kind.BOOK -> {
                BookSplash(
                    rank     = cue.rank,
                    progress = p,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 130.dp)
                )
            }
            AnimationCue.Kind.GIVE -> {
                GiveSplash(
                    rank     = cue.rank,
                    count    = cue.cards.size,
                    progress = p,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 130.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Comic-Splash "Go Fish!" — outlined, getiltet, mit Pop-in
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun ComicGoFishSplash(
    subtitle: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val p = progress
    // Pop-in mit Overshoot, dann stabil, am Ende fade-out
    val popScale = when {
        p < 0.15f -> easeOutBack(p / 0.15f)
        p > 0.92f -> 1f - ((p - 0.92f) / 0.08f) * 0.15f
        else      -> 1f
    }
    val splashAlpha = when {
        p < 0.06f -> p / 0.06f
        p > 0.92f -> ((1f - p) / 0.08f).coerceAtLeast(0f)
        else      -> 1f
    }

    Column(
        modifier = modifier
            .graphicsLayer(
                scaleX    = popScale,
                scaleY    = popScale,
                rotationZ = -4f,
                alpha     = splashAlpha
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComicText(
            text        = "Go Fish!",
            fontSize    = 72.sp,
            fillColor   = SunYellow,
            strokeColor = DeepSea,
            strokeWidth = 14f
        )
        Spacer(Modifier.height(10.dp))
        // Untertitel — kleiner, mit eigenem Schatten
        Text(
            text = subtitle,
            color = Foam,
            style = TextStyle(
                fontSize    = 22.sp,
                fontWeight  = FontWeight.Black,
                fontFamily  = FontFamily.SansSerif,
                fontStyle   = FontStyle.Italic,
                letterSpacing = 1.sp,
                shadow = Shadow(
                    color = DeepSea.copy(alpha = 0.85f),
                    offset = Offset(3f, 5f),
                    blurRadius = 8f
                )
            )
        )
    }
}

/**
 * Text in „Comic-Book"-Optik: dicker dunkler Stroke-Outline mit Drop-Shadow,
 * darüber die helle Füllung.
 */
@Composable
private fun ComicText(
    text: String,
    fontSize: TextUnit,
    fillColor: Color,
    strokeColor: Color,
    strokeWidth: Float
) {
    val baseStyle = TextStyle(
        fontSize      = fontSize,
        fontWeight    = FontWeight.Black,
        fontFamily    = FontFamily.SansSerif,
        fontStyle     = FontStyle.Italic,
        letterSpacing = 3.sp,
        textAlign     = TextAlign.Center
    )
    Box {
        // Outline-Layer mit Drop-Shadow
        Text(
            text  = text,
            color = strokeColor,
            style = baseStyle.copy(
                drawStyle = Stroke(
                    width = strokeWidth,
                    join  = StrokeJoin.Round,
                    miter = 10f
                ),
                shadow = Shadow(
                    color = DeepSea.copy(alpha = 0.55f),
                    offset = Offset(6f, 12f),
                    blurRadius = 14f
                )
            )
        )
        // Fülllayer (über dem Stroke)
        Text(
            text  = text,
            color = fillColor,
            style = baseStyle
        )
    }
}

private fun easeOutBack(t: Float): Float {
    val c1 = 1.70158f
    val c3 = c1 + 1f
    val tm = t - 1f
    return 1f + c3 * tm * tm * tm + c1 * tm * tm
}

// ─────────────────────────────────────────────────────────────────────────
//  Stage — zentrierte, kleinere Bühne für die Cutscene
// ─────────────────────────────────────────────────────────────────────────

private data class Stage(
    val left: Float, val top: Float,
    val width: Float, val height: Float
) {
    val right    get() = left + width
    val bottom   get() = top + height
    val centerX  get() = left + width / 2f
    val centerY  get() = top + height / 2f
    fun x(t: Float) = left + width * t
    fun y(t: Float) = top  + height * t
    fun unit()      = min(width, height)
}

private fun stageFor(canvasW: Float, canvasH: Float): Stage {
    val sw = min(canvasW * 0.78f, canvasH * 0.45f * 1.6f)   // begrenzt durch beide Achsen
    val sh = sw * 0.62f
    val sl = (canvasW - sw) / 2f
    val st = (canvasH - sh) / 2f - canvasH * 0.04f          // leicht über Mittelpunkt
    return Stage(sl, st, sw, sh)
}

// ─────────────────────────────────────────────────────────────────────────
//  Szene 1: Angler fischt eine Karte
// ─────────────────────────────────────────────────────────────────────────

private const val GF_CAST_END   = 0.22f
private const val GF_SPLASH_END = 0.36f
private const val GF_HOOK_END   = 0.52f
private const val GF_REEL_END   = 0.78f

private fun DrawScope.drawAnglerScene(cue: AnimationCue, p: Float, stage: Stage, measurer: TextMeasurer) {
    val s = stage.unit() * 0.10f                              // Skalierungs-Einheit (Figurengröße)

    val anglerCx = stage.x(0.78f)
    val anglerCy = stage.y(0.30f)
    val rodTip   = anglerRodTip(anglerCx, anglerCy, s)

    val waterPos = Offset(stage.x(0.30f), stage.y(0.74f))
    val deliver  = Offset(stage.x(0.52f), stage.y(0.96f))     // Karte landet am unteren Stage-Rand

    val hookPos: Offset = when {
        p < GF_CAST_END -> {
            val t = easeOutCubic(p / GF_CAST_END)
            Offset(lerp(rodTip.x, waterPos.x, t), lerp(rodTip.y, waterPos.y, t))
        }
        p < GF_HOOK_END -> {
            val wob = if (p > GF_SPLASH_END) sin((p - GF_SPLASH_END) * 80f) * 2f else 0f
            Offset(waterPos.x + wob, waterPos.y + wob * 0.5f)
        }
        p < GF_REEL_END -> {
            val t = easeInOutCubic((p - GF_HOOK_END) / (GF_REEL_END - GF_HOOK_END))
            Offset(
                lerp(waterPos.x, rodTip.x - s * 0.3f, t),
                lerp(waterPos.y, rodTip.y + s * 0.6f, t)
            )
        }
        else -> {
            val t = easeInOutCubic((p - GF_REEL_END) / (1f - GF_REEL_END))
            Offset(
                lerp(rodTip.x - s * 0.3f, deliver.x, t),
                lerp(rodTip.y + s * 0.6f, deliver.y, t)
            )
        }
    }

    // 1. Deck-Stack
    val deckAlpha = when {
        p < GF_SPLASH_END -> 1f
        p < GF_HOOK_END   -> 1f - (p - GF_SPLASH_END) / (GF_HOOK_END - GF_SPLASH_END)
        else              -> 0f
    }
    if (deckAlpha > 0f) {
        drawDeckIcon(waterPos, s * 1.1f, deckAlpha)
    }

    // 2. Splash + Tropfen
    if (p in GF_CAST_END..(GF_REEL_END - 0.02f)) {
        val sp = ((p - GF_CAST_END) / (GF_REEL_END - GF_CAST_END)).coerceIn(0f, 1f)
        drawSplash(waterPos, sp, s * 1.5f, OceanTop)
        if (p < GF_HOOK_END) drawDroplets(waterPos, sp, s * 0.10f)
    }

    // 3. Leine
    if (p < GF_REEL_END + 0.02f) {
        drawSaggingLine(rodTip, hookPos, sagFactor = 0.18f)
        drawHookGlint(hookPos, s * 0.06f)
    }

    // 4. Karte am Haken / in der Luft / am Ziel
    val card = cue.drawnCard
    if (card != null && p > GF_SPLASH_END) {
        val fadeIn = ((p - GF_SPLASH_END) / 0.10f).coerceIn(0f, 1f)
        // beim Delivery zum Ende hin ausfaden
        val fadeOut = if (p > 0.92f) (1f - (p - 0.92f) / 0.08f).coerceIn(0f, 1f) else 1f
        val cardAlpha = fadeIn * fadeOut

        val baseW = s * 1.4f
        val baseH = baseW * 1.42f
        val scale = lerp(0.55f, 1f, fadeIn)
        val sway = if (p in GF_HOOK_END..GF_REEL_END)
            sin((p - GF_HOOK_END) * 14f) * (s * 0.10f) else 0f
        val rot  = sway * 0.6f
        val cw = baseW * scale
        val ch = baseH * scale
        val tl = Offset(hookPos.x - cw / 2f + sway, hookPos.y + s * 0.1f)
        drawMiniCard(tl, Size(cw, ch), card.rank, card.suit, measurer,
            alpha = cardAlpha, rotation = rot)
    } else if (card == null && p > GF_SPLASH_END && p < GF_REEL_END) {
        val puffP = ((p - GF_SPLASH_END) / 0.40f).coerceIn(0f, 1f)
        drawPuff(hookPos, puffP, s * 0.18f)
    }

    // 5. Angler im Vordergrund
    drawAngler(anglerCx, anglerCy, s)
}

// ─────────────────────────────────────────────────────────────────────────
//  Szene 2: POV-Angel — Spieler klaut Karten
// ─────────────────────────────────────────────────────────────────────────

private const val ST_CAST_END = 0.22f
private const val ST_SNAG_END = 0.36f
private const val ST_HOOK_END = 0.50f

private fun DrawScope.drawPovRodScene(cue: AnimationCue, p: Float, stage: Stage, measurer: TextMeasurer) {
    val s = stage.unit() * 0.10f

    // POV-Rute: Handle unten-rechts (knapp im Stage), Tip in der Mitte
    val rodHandle = Offset(stage.x(1.05f), stage.y(1.10f))
    val rodTip    = Offset(stage.x(0.42f), stage.y(0.60f))

    val opponentPos = Offset(stage.x(0.50f), stage.y(0.22f))
    val deliver     = Offset(stage.x(0.45f), stage.y(0.98f))

    val hookPos: Offset = when {
        p < ST_CAST_END -> {
            val t = easeOutCubic(p / ST_CAST_END)
            Offset(lerp(rodTip.x, opponentPos.x, t), lerp(rodTip.y, opponentPos.y, t))
        }
        p < ST_HOOK_END -> {
            val wob = if (p > ST_SNAG_END) sin((p - ST_SNAG_END) * 80f) * 2f else 0f
            Offset(opponentPos.x + wob, opponentPos.y + wob * 0.5f)
        }
        else -> {
            val t = easeInOutCubic((p - ST_HOOK_END) / (1f - ST_HOOK_END))
            Offset(lerp(opponentPos.x, deliver.x, t), lerp(opponentPos.y, deliver.y, t))
        }
    }

    // 1. Gegner-Fisch
    val oppAlpha = when {
        p < ST_SNAG_END -> 1f
        p < ST_HOOK_END -> 1f - (p - ST_SNAG_END) / (ST_HOOK_END - ST_SNAG_END)
        else            -> 0f
    }
    if (oppAlpha > 0f) {
        drawOpponentFish(opponentPos, s * 1.1f, oppAlpha)
    }

    // 2. Splash bei Snag
    if (p in ST_CAST_END..0.65f) {
        val sp = ((p - ST_CAST_END) / 0.43f).coerceIn(0f, 1f)
        drawSplash(opponentPos, sp, s * 1.35f, Lavender)
        if (p < ST_HOOK_END) drawDroplets(opponentPos, sp, s * 0.08f)
    }

    // 3. Leine
    drawSaggingLine(rodTip, hookPos, sagFactor = 0.10f)
    drawHookGlint(hookPos, s * 0.06f)

    // 4. Karten (gefächert, wachsen leicht beim Heranziehen — POV-Zoom)
    if (p > ST_SNAG_END && cue.cards.isNotEmpty()) {
        val fadeIn = ((p - ST_SNAG_END) / 0.10f).coerceIn(0f, 1f)
        val fadeOut = if (p > 0.92f) (1f - (p - 0.92f) / 0.08f).coerceIn(0f, 1f) else 1f
        val cardAlpha = fadeIn * fadeOut

        val zoom = if (p > ST_HOOK_END) {
            val t = (p - ST_HOOK_END) / (1f - ST_HOOK_END)
            1f + t * 0.45f
        } else 1f
        val baseW = s * 1.3f * fadeIn * zoom
        val baseH = baseW * 1.42f
        val spread = baseW * 0.55f
        val n = cue.cards.size
        cue.cards.forEachIndexed { i, c ->
            val ox = (i - (n - 1) / 2f) * spread
            val tilt = (i - (n - 1) / 2f) * 4f
            val tl = Offset(hookPos.x - baseW / 2f + ox, hookPos.y + s * 0.08f)
            drawMiniCard(tl, Size(baseW, baseH), c.rank, c.suit, measurer,
                alpha = cardAlpha, rotation = tilt)
        }
    }

    // 5. POV-Rute im Vordergrund
    drawPovRod(rodHandle, rodTip, s)
}

// ─────────────────────────────────────────────────────────────────────────
//  Szene: Eigene Karten werden vom Gegner geangelt — sie fliegen nach oben weg
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawGiveScene(cue: AnimationCue, p: Float, stage: Stage, measurer: TextMeasurer) {
    val s = stage.unit() * 0.10f

    // Start unten (eigene Hand), Ziel oben (Gegner-Panel)
    val from = Offset(stage.x(0.5f), stage.y(1.04f))
    val to   = Offset(stage.x(0.5f), stage.y(-0.06f))

    val cards = cue.cards
    val n = cards.size.coerceAtLeast(1)
    val baseW = s * 1.4f
    val baseH = baseW * 1.42f
    val spread = baseW * 0.62f

    cards.forEachIndexed { i, c ->
        // leicht gestaffelter Start, damit mehrere Karten nacheinander wegziehen
        val delay = i * 0.07f
        val t = ((p - delay) / (1f - delay)).coerceIn(0f, 1f)
        val eased = easeInOutCubic(t)

        // Fächer am Start, der sich Richtung Gegner zusammenzieht
        val ox = (i - (n - 1) / 2f) * spread * (1f - eased * 0.7f)
        val cx = lerp(from.x, to.x, eased) + ox
        val cy = lerp(from.y, to.y, eased)

        val scale = lerp(1f, 0.5f, eased)            // schrumpft Richtung Gegner
        val fadeIn  = (p / 0.08f).coerceIn(0f, 1f)
        val fadeOut = if (t > 0.82f) (1f - (t - 0.82f) / 0.18f).coerceIn(0f, 1f) else 1f
        val alpha = fadeIn * fadeOut
        val rot = (i - (n - 1) / 2f) * 8f * (1f - eased) - eased * 12f

        // Bewegungs-Trail hinter der Karte (Sog nach oben)
        if (alpha > 0.2f && eased in 0.08f..0.92f) {
            val tailY = lerp(from.y, to.y, (eased - 0.10f).coerceIn(0f, 1f))
            drawLine(
                color = Foam.copy(alpha = alpha * 0.22f),
                start = Offset(cx, cy),
                end   = Offset(cx, tailY),
                strokeWidth = s * 0.05f,
                cap = StrokeCap.Round
            )
        }

        val cw = baseW * scale
        val ch = baseH * scale
        drawMiniCard(
            Offset(cx - cw / 2f, cy - ch / 2f), Size(cw, ch),
            c.rank, c.suit, measurer, alpha = alpha, rotation = rot
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Figuren — Angler
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawAngler(cx: Float, cy: Float, s: Float) {
    val headCx = cx
    val headCy = cy + s * 0.18f
    val headR  = s * 0.58f

    // ─── KÖRPER (Pullover mit Vertikal-Gradient) ───
    val bodyTL = Offset(cx - s * 0.78f, cy + s * 0.62f)
    val bodySz = Size(s * 1.56f, s * 1.95f)
    drawRoundRect(
        brush = Brush.verticalGradient(0f to SeafoamDeep, 1f to SuitDark),
        topLeft = bodyTL,
        size = bodySz,
        cornerRadius = CornerRadius(s * 0.40f, s * 0.40f)
    )
    // V-Halsausschnitt (dunkel)
    val collar = Path().apply {
        moveTo(cx - s * 0.32f, bodyTL.y)
        lineTo(cx, bodyTL.y + s * 0.32f)
        lineTo(cx + s * 0.32f, bodyTL.y)
        close()
    }
    drawPath(collar, color = SuitDark)
    // Streifen
    drawRoundRect(
        color = Foam.copy(alpha = 0.85f),
        topLeft = Offset(bodyTL.x, cy + s * 1.28f),
        size = Size(bodySz.width, s * 0.22f)
    )
    drawRoundRect(
        color = Foam.copy(alpha = 0.42f),
        topLeft = Offset(bodyTL.x, cy + s * 1.62f),
        size = Size(bodySz.width, s * 0.10f)
    )

    // ─── HALS (zwischen Body und Kopf) ───
    drawRoundRect(
        color = SandyBeige,
        topLeft = Offset(cx - s * 0.20f, cy + s * 0.50f),
        size = Size(s * 0.40f, s * 0.22f),
        cornerRadius = CornerRadius(s * 0.05f, s * 0.05f)
    )

    // ─── KOPF ───
    drawCircle(SandyBeige, radius = headR, center = Offset(headCx, headCy))
    // Highlight oben links
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam.copy(alpha = 0.55f), Color.Transparent),
            center = Offset(headCx - s * 0.18f, headCy - s * 0.22f),
            radius = s * 0.32f
        ),
        radius = headR,
        center = Offset(headCx, headCy)
    )
    // sanfter Schatten rechts unten
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, CoralDeep.copy(alpha = 0.20f)),
            center = Offset(headCx + s * 0.22f, headCy + s * 0.12f),
            radius = headR
        ),
        radius = headR,
        center = Offset(headCx, headCy)
    )

    // ─── BEANIE (rote Wollmütze mit weißer Stulpe & Bommel) ───
    // Stulpe muss klar über den Augenbrauen sitzen.
    val beanieBottom = headCy - headR * 0.60f
    val beanieTop    = headCy - headR * 1.42f
    val beanieW      = headR * 1.95f
    val beaniePath = Path().apply {
        moveTo(headCx - beanieW / 2f, beanieBottom)
        // linke Seite hoch
        cubicTo(
            headCx - beanieW / 2f - headR * 0.08f, beanieBottom - headR * 0.40f,
            headCx - beanieW / 2f + headR * 0.10f, beanieTop + headR * 0.15f,
            headCx - headR * 0.10f, beanieTop
        )
        // oben (leicht nach rechts geneigt für Charakter)
        quadraticBezierTo(
            headCx + headR * 0.05f, beanieTop - headR * 0.08f,
            headCx + headR * 0.20f, beanieTop + headR * 0.05f
        )
        // rechte Seite runter
        cubicTo(
            headCx + beanieW / 2f - headR * 0.10f, beanieTop + headR * 0.15f,
            headCx + beanieW / 2f + headR * 0.08f, beanieBottom - headR * 0.40f,
            headCx + beanieW / 2f, beanieBottom
        )
        close()
    }
    drawPath(beaniePath, brush = Brush.verticalGradient(
        0f to CoralDeep, 1f to SuitRed
    ))
    // Strick-Andeutung: drei waagerechte Linien
    listOf(0.30f, 0.55f, 0.80f).forEach { t ->
        val ly = lerp(beanieTop + headR * 0.05f, beanieBottom, t)
        drawLine(
            color = SuitDark.copy(alpha = 0.30f),
            start = Offset(headCx - beanieW / 2f + headR * 0.10f, ly),
            end   = Offset(headCx + beanieW / 2f - headR * 0.10f, ly),
            strokeWidth = s * 0.025f
        )
    }
    // Highlight auf der Mütze (oben links)
    drawPath(beaniePath, brush = Brush.radialGradient(
        colors = listOf(Foam.copy(alpha = 0.40f), Color.Transparent),
        center = Offset(headCx - headR * 0.30f, beanieTop + headR * 0.40f),
        radius = headR * 0.55f
    ))
    // Stulpe (weißer Rand am unteren Ende)
    drawRoundRect(
        brush = Brush.verticalGradient(0f to Foam, 1f to OceanTop),
        topLeft = Offset(headCx - beanieW / 2f, beanieBottom - headR * 0.20f),
        size = Size(beanieW, headR * 0.32f),
        cornerRadius = CornerRadius(s * 0.06f, s * 0.06f)
    )
    // Stulpen-Schatten
    drawLine(
        color = LavenderDeep.copy(alpha = 0.20f),
        start = Offset(headCx - beanieW / 2f + headR * 0.05f, beanieBottom - headR * 0.05f),
        end   = Offset(headCx + beanieW / 2f - headR * 0.05f, beanieBottom - headR * 0.05f),
        strokeWidth = s * 0.03f
    )
    // Bommel (weiß, oben drauf)
    val pomCenter = Offset(headCx + headR * 0.10f, beanieTop - headR * 0.10f)
    drawCircle(Foam, radius = headR * 0.22f, center = pomCenter)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, LavenderDeep.copy(alpha = 0.25f)),
            center = Offset(pomCenter.x + headR * 0.08f, pomCenter.y + headR * 0.08f),
            radius = headR * 0.22f
        ),
        radius = headR * 0.22f, center = pomCenter
    )
    // Bommel-Highlight
    drawCircle(Foam, radius = headR * 0.06f,
        center = Offset(pomCenter.x - headR * 0.08f, pomCenter.y - headR * 0.08f))

    // ─── BART (weißer Vollbart) ───
    val beardPath = Path().apply {
        moveTo(headCx - headR * 0.78f, headCy + s * 0.06f)
        // links runter zur Mitte
        quadraticBezierTo(
            headCx - headR * 0.92f, headCy + headR * 0.55f,
            headCx - headR * 0.45f, headCy + headR * 1.10f
        )
        // unten breit
        quadraticBezierTo(
            headCx, headCy + headR * 1.25f,
            headCx + headR * 0.45f, headCy + headR * 1.10f
        )
        // rechts hoch
        quadraticBezierTo(
            headCx + headR * 0.92f, headCy + headR * 0.55f,
            headCx + headR * 0.78f, headCy + s * 0.06f
        )
        // obere Kante mit Senke unter dem Schnurrbart
        quadraticBezierTo(
            headCx + headR * 0.50f, headCy + s * 0.18f,
            headCx + headR * 0.18f, headCy + s * 0.16f
        )
        quadraticBezierTo(
            headCx, headCy + s * 0.10f,
            headCx - headR * 0.18f, headCy + s * 0.16f
        )
        quadraticBezierTo(
            headCx - headR * 0.50f, headCy + s * 0.18f,
            headCx - headR * 0.78f, headCy + s * 0.06f
        )
        close()
    }
    drawPath(beardPath, color = Foam)
    // Bart-Schatten (Textur)
    drawPath(beardPath, color = LavenderDeep.copy(alpha = 0.18f),
        style = Stroke(width = s * 0.025f))
    // Bart-Strähnen (drei feine Linien)
    listOf(-0.20f, 0.0f, 0.20f).forEach { off ->
        drawLine(
            color = LavenderDeep.copy(alpha = 0.18f),
            start = Offset(headCx + headR * off, headCy + headR * 0.45f),
            end   = Offset(headCx + headR * off * 0.5f, headCy + headR * 1.05f),
            strokeWidth = s * 0.02f,
            cap = StrokeCap.Round
        )
    }

    // ─── SCHNURRBART ───
    val mustache = Path().apply {
        moveTo(headCx - headR * 0.36f, headCy + s * 0.12f)
        // linker Bogen mit Aufschwung am Ende
        quadraticBezierTo(
            headCx - headR * 0.55f, headCy - s * 0.02f,
            headCx - headR * 0.62f, headCy + s * 0.10f
        )
        quadraticBezierTo(
            headCx - headR * 0.30f, headCy + s * 0.22f,
            headCx, headCy + s * 0.18f
        )
        // rechter Bogen
        quadraticBezierTo(
            headCx + headR * 0.30f, headCy + s * 0.22f,
            headCx + headR * 0.62f, headCy + s * 0.10f
        )
        quadraticBezierTo(
            headCx + headR * 0.55f, headCy - s * 0.02f,
            headCx + headR * 0.36f, headCy + s * 0.12f
        )
        close()
    }
    drawPath(mustache, color = Foam)
    drawPath(mustache, color = LavenderDeep.copy(alpha = 0.25f),
        style = Stroke(width = s * 0.022f))

    // ─── AUGENBRAUEN ───
    val browStroke = Stroke(width = s * 0.065f, cap = StrokeCap.Round)
    val leftBrow = Path().apply {
        moveTo(headCx - s * 0.32f, headCy - s * 0.20f)
        quadraticBezierTo(
            headCx - s * 0.22f, headCy - s * 0.30f,
            headCx - s * 0.10f, headCy - s * 0.20f
        )
    }
    drawPath(leftBrow, color = DeepSea, style = browStroke)
    val rightBrow = Path().apply {
        moveTo(headCx + s * 0.02f, headCy - s * 0.20f)
        quadraticBezierTo(
            headCx + s * 0.13f, headCy - s * 0.30f,
            headCx + s * 0.24f, headCy - s * 0.20f
        )
    }
    drawPath(rightBrow, color = DeepSea, style = browStroke)

    // ─── AUGEN (beide, mit Weißem, Pupille, Highlight) ───
    val eyeY        = headCy - s * 0.07f
    val eyeWhiteR   = s * 0.13f
    val pupilR      = s * 0.065f
    val highlightR  = s * 0.030f

    val leftEye  = Offset(headCx - s * 0.20f, eyeY)
    val rightEye = Offset(headCx + s * 0.14f, eyeY)

    // Schatten unter den Augen (Augenhöhle)
    drawCircle(SandyBeige.copy(alpha = 0.4f), radius = eyeWhiteR + s * 0.02f,
        center = Offset(leftEye.x + s * 0.01f, leftEye.y + s * 0.02f))
    drawCircle(SandyBeige.copy(alpha = 0.4f), radius = eyeWhiteR + s * 0.02f,
        center = Offset(rightEye.x + s * 0.01f, rightEye.y + s * 0.02f))

    // Linkes Auge
    drawCircle(Foam, radius = eyeWhiteR, center = leftEye)
    drawCircle(DeepSea, radius = pupilR,
        center = Offset(leftEye.x - s * 0.025f, eyeY + s * 0.015f))
    drawCircle(Foam, radius = highlightR,
        center = Offset(leftEye.x - s * 0.045f, eyeY - s * 0.015f))

    // Rechtes Auge
    drawCircle(Foam, radius = eyeWhiteR, center = rightEye)
    drawCircle(DeepSea, radius = pupilR,
        center = Offset(rightEye.x - s * 0.025f, eyeY + s * 0.015f))
    drawCircle(Foam, radius = highlightR,
        center = Offset(rightEye.x - s * 0.045f, eyeY - s * 0.015f))

    // ─── NASE ───
    val nose = Path().apply {
        moveTo(headCx - s * 0.05f, headCy + s * 0.00f)
        quadraticBezierTo(
            headCx + s * 0.01f, headCy + s * 0.10f,
            headCx + s * 0.06f, headCy + s * 0.02f
        )
    }
    drawPath(nose, color = CoralDeep.copy(alpha = 0.65f),
        style = Stroke(width = s * 0.045f, cap = StrokeCap.Round))
    // kleiner Nasenball (rosa Highlight)
    drawCircle(CoralPink.copy(alpha = 0.55f), radius = s * 0.045f,
        center = Offset(headCx + s * 0.01f, headCy + s * 0.08f))

    // ─── ARM mit Ellbogen ───
    val shoulder = Offset(cx - s * 0.65f, cy + s * 0.82f)
    val elbow    = Offset(cx - s * 1.00f, cy + s * 1.35f)
    val hand     = Offset(cx - s * 1.38f, cy + s * 0.95f)
    drawLine(
        brush = Brush.verticalGradient(0f to SeafoamDeep, 1f to SuitDark),
        start = shoulder, end = elbow,
        strokeWidth = s * 0.38f, cap = StrokeCap.Round
    )
    drawLine(
        brush = Brush.verticalGradient(0f to SeafoamDeep, 1f to SuitDark),
        start = elbow, end = hand,
        strokeWidth = s * 0.32f, cap = StrokeCap.Round
    )
    // Hand (größer, mit Highlight)
    drawCircle(SandyBeige, radius = s * 0.22f, center = hand)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam.copy(alpha = 0.5f), Color.Transparent),
            center = Offset(hand.x - s * 0.06f, hand.y - s * 0.06f),
            radius = s * 0.18f
        ),
        radius = s * 0.22f, center = hand
    )
    // Daumen-Andeutung
    drawCircle(SandyBeige, radius = s * 0.08f,
        center = Offset(hand.x + s * 0.12f, hand.y - s * 0.08f))

    // ─── RUTE — tapered 3 Segmente ───
    val rodTip = anglerRodTip(cx, cy, s)
    val mid1 = Offset(lerp(hand.x, rodTip.x, 0.4f), lerp(hand.y, rodTip.y, 0.4f))
    val mid2 = Offset(lerp(hand.x, rodTip.x, 0.75f), lerp(hand.y, rodTip.y, 0.75f))
    drawLine(DeepSea, hand, mid1, strokeWidth = s * 0.20f, cap = StrokeCap.Round)
    drawLine(DeepSea, mid1, mid2, strokeWidth = s * 0.13f, cap = StrokeCap.Round)
    drawLine(DeepSea, mid2, rodTip, strokeWidth = s * 0.07f, cap = StrokeCap.Round)
    // Glanz-Highlight auf der Rute
    drawLine(
        color = Foam.copy(alpha = 0.35f),
        start = hand,
        end = Offset(lerp(hand.x, rodTip.x, 0.55f), lerp(hand.y, rodTip.y, 0.55f)),
        strokeWidth = s * 0.05f, cap = StrokeCap.Round
    )

    // ─── ROLLE — metallisch + Drehgriff ───
    val reelC = Offset(hand.x + s * 0.10f, hand.y - s * 0.22f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam, SunDeep, CoralDeep),
            center = Offset(reelC.x - s * 0.06f, reelC.y - s * 0.06f),
            radius = s * 0.28f
        ),
        radius = s * 0.22f, center = reelC
    )
    drawCircle(DeepSea, radius = s * 0.22f, center = reelC, style = Stroke(width = s * 0.04f))
    // innerer Speichen-Stern (3 Linien)
    listOf(0f, 60f, 120f).forEach { angle ->
        val rad = Math.toRadians(angle.toDouble())
        val dx = kotlin.math.cos(rad).toFloat() * s * 0.16f
        val dy = kotlin.math.sin(rad).toFloat() * s * 0.16f
        drawLine(
            color = DeepSea.copy(alpha = 0.6f),
            start = Offset(reelC.x - dx, reelC.y - dy),
            end   = Offset(reelC.x + dx, reelC.y + dy),
            strokeWidth = s * 0.025f
        )
    }
    drawCircle(DeepSea, radius = s * 0.06f, center = reelC)
    // Drehgriff (kleiner Knauf seitlich)
    val handleEnd = Offset(reelC.x + s * 0.20f, reelC.y - s * 0.15f)
    drawLine(
        color = DeepSea, start = reelC, end = handleEnd,
        strokeWidth = s * 0.04f, cap = StrokeCap.Round
    )
    drawCircle(SunYellow, radius = s * 0.055f, center = handleEnd)
    drawCircle(DeepSea, radius = s * 0.055f, center = handleEnd,
        style = Stroke(width = s * 0.012f))
}

private fun anglerRodTip(cx: Float, cy: Float, s: Float): Offset {
    val hand = Offset(cx - s * 1.38f, cy + s * 0.95f)
    return Offset(hand.x - s * 2.10f, hand.y - s * 2.50f)
}

// ─────────────────────────────────────────────────────────────────────────
//  Figuren — POV-Rute
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawPovRod(handle: Offset, tip: Offset, s: Float) {
    // Rute in 4 Segmenten mit abnehmender Dicke (Foreshortening)
    val widths = listOf(s * 0.45f, s * 0.30f, s * 0.18f, s * 0.10f)
    val segments = widths.size
    for (i in 0 until segments) {
        val t1 = i.toFloat() / segments
        val t2 = (i + 1).toFloat() / segments
        drawLine(
            brush = Brush.verticalGradient(
                0f to DeepSea.copy(alpha = 0.95f),
                1f to SuitDark
            ),
            start = Offset(lerp(handle.x, tip.x, t1), lerp(handle.y, tip.y, t1)),
            end   = Offset(lerp(handle.x, tip.x, t2), lerp(handle.y, tip.y, t2)),
            strokeWidth = widths[i],
            cap = StrokeCap.Round
        )
    }
    // GRIFF (Vordergrund, beige) mit Highlight
    val gripEnd = Offset(lerp(handle.x, tip.x, 0.20f), lerp(handle.y, tip.y, 0.20f))
    drawLine(
        brush = Brush.verticalGradient(
            0f to SandyBeige,
            1f to CoralDeep.copy(alpha = 0.5f)
        ),
        start = handle,
        end = gripEnd,
        strokeWidth = s * 0.55f,
        cap = StrokeCap.Round
    )
    // Riffelung am Griff (3 kleine Linien)
    repeat(3) { i ->
        val t = 0.05f + i * 0.045f
        val cx = lerp(handle.x, tip.x, t)
        val cy = lerp(handle.y, tip.y, t)
        drawCircle(DeepSea.copy(alpha = 0.4f), radius = s * 0.04f, center = Offset(cx, cy))
    }

    // ROLLE
    val reelC = Offset(lerp(handle.x, tip.x, 0.22f) - s * 0.08f, lerp(handle.y, tip.y, 0.22f) - s * 0.20f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam, SunDeep, CoralDeep),
            center = Offset(reelC.x - s * 0.07f, reelC.y - s * 0.07f),
            radius = s * 0.40f
        ),
        radius = s * 0.32f, center = reelC
    )
    drawCircle(DeepSea, radius = s * 0.32f, center = reelC, style = Stroke(width = s * 0.05f))
    drawCircle(DeepSea, radius = s * 0.08f, center = reelC)
}

// ─────────────────────────────────────────────────────────────────────────
//  Figuren — Gegner-Fisch
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawOpponentFish(center: Offset, r: Float, alpha: Float) {
    // KÖRPER mit Vertikal-Gradient
    drawOval(
        brush = Brush.verticalGradient(
            0f to Lavender.copy(alpha = alpha),
            1f to LavenderDeep.copy(alpha = alpha)
        ),
        topLeft = Offset(center.x - r, center.y - r * 0.55f),
        size = Size(r * 2f, r * 1.10f)
    )
    // Bauch-Highlight
    drawOval(
        color = Foam.copy(alpha = alpha * 0.55f),
        topLeft = Offset(center.x - r * 0.55f, center.y + r * 0.0f),
        size = Size(r * 1.10f, r * 0.40f)
    )
    // Schwanz
    val tail = Path().apply {
        moveTo(center.x + r * 0.90f, center.y)
        lineTo(center.x + r * 1.55f, center.y - r * 0.55f)
        lineTo(center.x + r * 1.55f, center.y + r * 0.55f)
        close()
    }
    drawPath(
        path = tail,
        brush = Brush.horizontalGradient(
            colors = listOf(LavenderDeep.copy(alpha = alpha), Lavender.copy(alpha = alpha * 0.8f)),
            startX = center.x + r * 0.90f,
            endX = center.x + r * 1.55f
        )
    )
    // Flosse oben
    val fin = Path().apply {
        moveTo(center.x - r * 0.10f, center.y - r * 0.45f)
        cubicTo(
            center.x + r * 0.15f, center.y - r * 0.85f,
            center.x + r * 0.40f, center.y - r * 0.75f,
            center.x + r * 0.30f, center.y - r * 0.40f
        )
        close()
    }
    drawPath(fin, color = LavenderDeep.copy(alpha = alpha))

    // Schuppen-Andeutung (kleine Halbkreis-Pünktchen)
    val scaleColor = Foam.copy(alpha = alpha * 0.30f)
    for (row in 0..1) {
        for (col in 0..3) {
            val sx = center.x - r * 0.30f + col * r * 0.25f
            val sy = center.y - r * 0.10f + row * r * 0.18f
            drawCircle(scaleColor, radius = r * 0.06f, center = Offset(sx, sy))
        }
    }

    // Auge
    drawCircle(Foam.copy(alpha = alpha),    radius = r * 0.17f,
        center = Offset(center.x - r * 0.40f, center.y - r * 0.12f))
    drawCircle(DeepSea.copy(alpha = alpha), radius = r * 0.09f,
        center = Offset(center.x - r * 0.42f, center.y - r * 0.10f))
    drawCircle(Foam.copy(alpha = alpha),    radius = r * 0.03f,
        center = Offset(center.x - r * 0.44f, center.y - r * 0.13f))
}

// ─────────────────────────────────────────────────────────────────────────
//  Deck-Icon
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawDeckIcon(center: Offset, sz: Float, alpha: Float = 1f) {
    val cardW = sz * 1.0f
    val cardH = sz * 1.42f
    repeat(3) { i ->
        val off = (i - 1) * (sz * 0.07f)
        drawRoundRect(
            color = (if (i == 2) Foam else OceanMid).copy(alpha = alpha),
            topLeft = Offset(center.x - cardW / 2f + off, center.y - cardH / 2f + off),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(sz * 0.13f, sz * 0.13f)
        )
        drawRoundRect(
            color = SuitDark.copy(alpha = alpha * 0.6f),
            topLeft = Offset(center.x - cardW / 2f + off, center.y - cardH / 2f + off),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(sz * 0.13f, sz * 0.13f),
            style = Stroke(width = sz * 0.025f)
        )
    }
    // Symbol auf der obersten Karte
    val topOff = sz * 0.07f
    drawCircle(
        color = SuitRed.copy(alpha = alpha * 0.85f),
        radius = sz * 0.16f,
        center = Offset(center.x + topOff, center.y + topOff)
    )
}

// ─────────────────────────────────────────────────────────────────────────
//  Effekte: Leine, Splash, Tropfen, Karte, Hook-Glint
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawSaggingLine(start: Offset, end: Offset, sagFactor: Float = 0.15f) {
    val len = max(8f, kotlin.math.hypot(end.x - start.x, end.y - start.y))
    val sag = len * sagFactor
    val midX = (start.x + end.x) / 2f
    val midY = max(start.y, end.y) + sag
    val path = Path().apply {
        moveTo(start.x, start.y)
        quadraticBezierTo(midX, midY, end.x, end.y)
    }
    drawPath(path, color = DeepSea.copy(alpha = 0.80f), style = Stroke(width = 2.5f))
}

private fun DrawScope.drawHookGlint(center: Offset, r: Float) {
    // kleiner Haken-Klecks mit Glanzpunkt
    drawCircle(DeepSea, radius = r, center = center)
    drawCircle(Foam.copy(alpha = 0.7f), radius = r * 0.35f,
        center = Offset(center.x - r * 0.25f, center.y - r * 0.25f))
}

private fun DrawScope.drawSplash(center: Offset, progress: Float, maxRadius: Float, color: Color) {
    for (i in 0..2) {
        val delay = i * 0.18f
        val span  = 1f - delay
        val t = ((progress - delay) / span).coerceIn(0f, 1f)
        if (t > 0f) {
            val eased = 1f - (1f - t) * (1f - t)
            val r = eased * maxRadius * (1f - i * 0.10f)
            val alpha = (1f - t) * 0.70f
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = r,
                center = center,
                style = Stroke(width = max(1.5f, 3f - i * 0.5f))
            )
        }
    }
}

/** Kleine Wassertropfen, die beim Aufprall hochspritzen. */
private fun DrawScope.drawDroplets(center: Offset, p: Float, baseR: Float) {
    val droplets = 5
    val alpha = (1f - p) * 0.85f
    if (alpha <= 0.01f) return
    for (i in 0 until droplets) {
        val angle = (-90f - 40f) + (i.toFloat() / (droplets - 1)) * 80f   // Halbkreis nach oben
        val dist  = baseR * 12f * p
        val rad = Math.toRadians(angle.toDouble())
        val dx = cos(rad).toFloat() * dist
        val dy = sin(rad).toFloat() * dist + baseR * 8f * p * p          // Schwerkraft-Bogen
        drawCircle(
            color = OceanTop.copy(alpha = alpha),
            radius = baseR * (1f - p * 0.5f),
            center = Offset(center.x + dx, center.y + dy)
        )
    }
}

private fun DrawScope.drawPuff(center: Offset, p: Float, baseR: Float) {
    val alpha = (1f - p) * 0.7f
    for (i in 0..5) {
        val angle = i * 60f + p * 40f
        val r = baseR * 1.6f * p
        val rad = Math.toRadians(angle.toDouble())
        val cx = center.x + cos(rad).toFloat() * r
        val cy = center.y + sin(rad).toFloat() * r
        drawCircle(
            color = Foam.copy(alpha = alpha),
            radius = baseR * (1f - p * 0.3f),
            center = Offset(cx, cy)
        )
    }
}

private fun DrawScope.drawMiniCard(
    topLeft: Offset,
    size: Size,
    rank: String,
    suit: String,
    measurer: TextMeasurer,
    alpha: Float = 1f,
    rotation: Float = 0f
) {
    if (alpha <= 0.01f) return
    val center = Offset(topLeft.x + size.width / 2f, topLeft.y + size.height / 2f)
    rotate(degrees = rotation, pivot = center) {
        val isRed = suit == "♥" || suit == "♦"
        val suitColor = if (isRed) SuitRed else SuitDark
        val corner = min(14f, size.height * 0.13f)

        // Drop-Shadow
        drawRoundRect(
            color = DeepSea.copy(alpha = alpha * 0.32f),
            topLeft = Offset(topLeft.x + 2f, topLeft.y + 7f),
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        // Karte mit subtilem Gradient
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Foam.copy(alpha = alpha), OceanTop.copy(alpha = alpha * 0.95f))
            ),
            topLeft = topLeft,
            size = size,
            cornerRadius = CornerRadius(corner, corner)
        )
        // Pastell-Rahmen
        drawRoundRect(
            color = SunDeep.copy(alpha = alpha * 0.90f),
            topLeft = topLeft,
            size = size,
            cornerRadius = CornerRadius(corner, corner),
            style = Stroke(width = 2f)
        )
        // Glas-Highlight oben
        val highlightH = min(10f, size.height * 0.12f)
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Foam.copy(alpha = alpha * 0.65f), Color.Transparent)
            ),
            topLeft = topLeft,
            size = Size(size.width, highlightH * 1.6f),
            cornerRadius = CornerRadius(corner, corner)
        )

        // Rang oben links
        val rankFs = (size.height * 0.22f).coerceIn(10f, 24f).sp
        val rankLayout = measurer.measure(
            text = AnnotatedString(rank),
            style = TextStyle(color = suitColor.copy(alpha = alpha),
                fontSize = rankFs, fontWeight = FontWeight.ExtraBold)
        )
        drawText(
            textLayoutResult = rankLayout,
            topLeft = Offset(topLeft.x + 6f, topLeft.y + 2f)
        )
        // Großes Suit-Symbol mittig
        val suitFs = (size.height * 0.38f).coerceIn(16f, 36f).sp
        val suitLayout = measurer.measure(
            text = AnnotatedString(suit),
            style = TextStyle(color = suitColor.copy(alpha = alpha),
                fontSize = suitFs, fontWeight = FontWeight.Bold)
        )
        drawText(
            textLayoutResult = suitLayout,
            topLeft = Offset(
                topLeft.x + size.width / 2f - suitLayout.size.width / 2f,
                topLeft.y + size.height / 2f - suitLayout.size.height / 2f
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Math
// ─────────────────────────────────────────────────────────────────────────

private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
private fun easeOutCubic(t: Float): Float = 1f - (1f - t).let { it * it * it }
private fun easeInOutCubic(t: Float): Float =
    if (t < 0.5f) 4f * t * t * t else 1f - ((-2f * t + 2f).let { it * it * it }) / 2f

/** 0 → 1 → 0 für sanftes Ein-/Ausblenden von Banner & Vignette. */
private fun fadeEnvelope(p: Float): Float = when {
    p < 0.10f -> easeOutCubic(p / 0.10f)
    p > 0.90f -> easeOutCubic((1f - p) / 0.10f)
    else      -> 1f
}

// ═════════════════════════════════════════════════════════════════════════
//  STEAL-SPLASH (unter der Angel-Animation, Decorations nach Kartenzahl)
// ═════════════════════════════════════════════════════════════════════════

@Composable
private fun StealSplash(
    rank: String,
    count: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val p = progress
    val popScale = when {
        p < 0.15f -> easeOutBack(p / 0.15f)
        p > 0.92f -> 1f - ((p - 0.92f) / 0.08f) * 0.15f
        else      -> 1f
    }
    val splashAlpha = when {
        p < 0.06f -> p / 0.06f
        p > 0.92f -> ((1f - p) / 0.08f).coerceAtLeast(0f)
        else      -> 1f
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Decorations je nach Anzahl Karten — werden hinter dem Text gezeichnet.
        when {
            count >= 3 -> FlameDecoration(alpha = splashAlpha)
            count >= 2 -> BubbleDecoration(progress = p, alpha = splashAlpha)
        }

        Column(
            modifier = Modifier.graphicsLayer(
                scaleX    = popScale,
                scaleY    = popScale,
                rotationZ = 3f,         // entgegengesetzter Tilt zu Go Fish — eigener Charakter
                alpha     = splashAlpha
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComicTextBold(
                text        = "$count×  $rank",
                fontSize    = 58.sp,
                fillColor   = SeafoamGreen,
                strokeColor = SuitDark,
                strokeWidth = 12f
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = LocalTexts.current.animCaught,
                color = Foam,
                style = TextStyle(
                    fontSize      = 22.sp,
                    fontWeight    = FontWeight.Black,
                    fontFamily    = FontFamily.SansSerif,
                    letterSpacing = 1.sp,
                    shadow = Shadow(
                        color = DeepSea.copy(alpha = 0.85f),
                        offset = Offset(3f, 5f),
                        blurRadius = 8f
                    )
                )
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════
//  GIVE-SPLASH (eigene Karten werden geangelt — fliegen zum Gegner)
// ═════════════════════════════════════════════════════════════════════════

@Composable
private fun GiveSplash(
    rank: String,
    count: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val p = progress
    val popScale = when {
        p < 0.15f -> easeOutBack(p / 0.15f)
        p > 0.92f -> 1f - ((p - 0.92f) / 0.08f) * 0.15f
        else      -> 1f
    }
    val splashAlpha = when {
        p < 0.06f -> p / 0.06f
        p > 0.92f -> ((1f - p) / 0.08f).coerceAtLeast(0f)
        else      -> 1f
    }

    Column(
        modifier = modifier.graphicsLayer(
            scaleX    = popScale,
            scaleY    = popScale,
            rotationZ = -3f,
            alpha     = splashAlpha
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ComicTextBold(
            text        = "$count×  $rank",
            fontSize    = 56.sp,
            fillColor   = Lavender,
            strokeColor = SuitDark,
            strokeWidth = 12f
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = LocalTexts.current.animGaveAway,
            color = Foam,
            style = TextStyle(
                fontSize      = 22.sp,
                fontWeight    = FontWeight.Black,
                fontFamily    = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                shadow = Shadow(
                    color = DeepSea.copy(alpha = 0.85f),
                    offset = Offset(3f, 5f),
                    blurRadius = 8f
                )
            )
        )
    }
}

/** Wie ComicText, aber NICHT italic — eigene Optik für den Steal-Splash. */
@Composable
private fun ComicTextBold(
    text: String,
    fontSize: TextUnit,
    fillColor: Color,
    strokeColor: Color,
    strokeWidth: Float
) {
    val baseStyle = TextStyle(
        fontSize      = fontSize,
        fontWeight    = FontWeight.Black,
        fontFamily    = FontFamily.SansSerif,
        letterSpacing = 3.sp,
        textAlign     = TextAlign.Center
    )
    Box {
        Text(
            text  = text,
            color = strokeColor,
            style = baseStyle.copy(
                drawStyle = Stroke(
                    width = strokeWidth,
                    join  = StrokeJoin.Round,
                    miter = 10f
                ),
                shadow = Shadow(
                    color = DeepSea.copy(alpha = 0.55f),
                    offset = Offset(6f, 12f),
                    blurRadius = 14f
                )
            )
        )
        Text(
            text  = text,
            color = fillColor,
            style = baseStyle
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Decoration 2-Karten: aufsteigende Blubberblasen
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun BubbleDecoration(progress: Float, alpha: Float) {
    Canvas(modifier = Modifier.size(width = 320.dp, height = 150.dp)) {
        val w = size.width
        val h = size.height
        val bubbleCount = 14
        for (i in 0 until bubbleCount) {
            val xFrac = (i + 0.5f) / bubbleCount
            val phase = (i * 0.137f) % 1f
            val t = ((progress * 1.6f + phase) % 1f)
            val y = h * (1.05f - t * 1.15f)
            val r = (3f + (i % 4) * 3.5f) * (1f - t * 0.25f)
            val a = alpha * (1f - t) * 0.65f
            val wobble = kotlin.math.sin(t * 8f + i) * w * 0.025f
            val cx = w * xFrac + wobble
            drawCircle(BubbleWhite.copy(alpha = a),
                radius = r, center = Offset(cx, y))
            drawCircle(Foam.copy(alpha = a * 0.85f),
                radius = r * 0.30f,
                center = Offset(cx - r * 0.35f, y - r * 0.35f))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Decoration 3+ Karten: flackernde Flammen
// ─────────────────────────────────────────────────────────────────────────

@Composable
private fun FlameDecoration(alpha: Float) {
    val flicker by rememberInfiniteTransition(label = "flameDeco").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(360, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameFlicker"
    )
    Canvas(modifier = Modifier.size(width = 340.dp, height = 170.dp)) {
        val w = size.width
        val h = size.height
        // Flammen unten + an den Seiten — umrahmen den Text
        val positions = listOf(
            Triple(0.07f, 0.85f, 0.0f),    // bottom-left
            Triple(0.25f, 0.95f, 0.18f),
            Triple(0.50f, 1.00f, 0.36f),   // bottom-center
            Triple(0.75f, 0.95f, 0.54f),
            Triple(0.93f, 0.85f, 0.72f),   // bottom-right
            Triple(0.04f, 0.30f, 0.40f),   // top-left
            Triple(0.96f, 0.30f, 0.60f)    // top-right
        )
        positions.forEachIndexed { i, (xf, yf, ph) ->
            val localFlick = (flicker + ph) % 1f
            val scale = 0.75f + kotlin.math.sin(localFlick * 6.283f) * 0.25f
            val centerX = w * xf
            val centerY = h * yf
            drawFlame(
                center = Offset(centerX, centerY),
                w = w * 0.075f * scale,
                h = h * 0.30f * scale,
                alpha = alpha
            )
        }
    }
}

private fun DrawScope.drawFlame(center: Offset, w: Float, h: Float, alpha: Float) {
    // Outer flame (Gradient gelb → coral → rot)
    val flame = Path().apply {
        moveTo(center.x, center.y + h * 0.50f)
        cubicTo(
            center.x - w * 0.75f, center.y + h * 0.25f,
            center.x - w * 0.80f, center.y - h * 0.20f,
            center.x - w * 0.15f, center.y - h * 0.45f
        )
        cubicTo(
            center.x - w * 0.05f, center.y - h * 0.60f,
            center.x + w * 0.10f, center.y - h * 0.50f,
            center.x, center.y - h * 0.30f
        )
        cubicTo(
            center.x + w * 0.30f, center.y - h * 0.55f,
            center.x + w * 0.80f, center.y - h * 0.20f,
            center.x + w * 0.75f, center.y + h * 0.25f
        )
        cubicTo(
            center.x + w * 0.40f, center.y + h * 0.50f,
            center.x - w * 0.40f, center.y + h * 0.50f,
            center.x, center.y + h * 0.50f
        )
        close()
    }
    drawPath(flame, brush = Brush.verticalGradient(
        colors = listOf(
            SunDeep.copy(alpha = alpha * 0.95f),
            CoralDeep.copy(alpha = alpha * 0.90f),
            SuitRed.copy(alpha = alpha * 0.75f)
        )
    ))
    // Inner core — helleres Gelb-Highlight
    val core = Path().apply {
        moveTo(center.x, center.y + h * 0.30f)
        cubicTo(
            center.x - w * 0.30f, center.y + h * 0.10f,
            center.x - w * 0.35f, center.y - h * 0.12f,
            center.x, center.y - h * 0.30f
        )
        cubicTo(
            center.x + w * 0.35f, center.y - h * 0.12f,
            center.x + w * 0.30f, center.y + h * 0.10f,
            center.x, center.y + h * 0.30f
        )
        close()
    }
    drawPath(core, brush = Brush.verticalGradient(
        colors = listOf(SunYellow.copy(alpha = alpha * 0.95f), CoralPink.copy(alpha = alpha * 0.65f))
    ))
}

// ═════════════════════════════════════════════════════════════════════════
//  BOOK-CELEBRATION: 4 Karten konvergieren, Burst, Sparkles, "BUCH!" Splash
// ═════════════════════════════════════════════════════════════════════════

private const val BK_CONVERGE_START = 0.05f
private const val BK_CONVERGE_END   = 0.45f
private const val BK_BURST_START    = 0.40f
private const val BK_FADE_START     = 0.93f

private fun DrawScope.drawBookScene(cue: AnimationCue, p: Float, stage: Stage, measurer: TextMeasurer) {
    val s = stage.unit() * 0.10f
    val centerX = stage.centerX
    val centerY = stage.centerY
    val suits = listOf("♠", "♥", "♦", "♣")

    // 1. Goldener Burst (Glow) hinter den Karten
    if (p > BK_BURST_START) {
        val burstP = ((p - BK_BURST_START) / 0.40f).coerceIn(0f, 1f)
        val burstR = burstP * stage.unit() * 0.65f
        val burstAlpha = (1f - burstP) * 0.70f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    SunYellow.copy(alpha = burstAlpha * 0.85f),
                    SunDeep.copy(alpha = burstAlpha * 0.50f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = burstR
            ),
            radius = burstR,
            center = Offset(centerX, centerY)
        )
    }

    // 2. Sparkles in einem Kreis um das Zentrum
    if (p > 0.45f) {
        val sparkleP = ((p - 0.45f) / 0.45f).coerceIn(0f, 1f)
        val sparkleCount = 12
        val ringR = stage.unit() * 0.32f * (1f + sparkleP * 0.3f)
        for (i in 0 until sparkleCount) {
            val angle = (i.toFloat() / sparkleCount) * 360f + sparkleP * 90f
            val rad = Math.toRadians(angle.toDouble())
            val sx = centerX + kotlin.math.cos(rad).toFloat() * ringR
            val sy = centerY + kotlin.math.sin(rad).toFloat() * ringR
            val a = (1f - sparkleP) * (0.6f + kotlin.math.sin(p * 12f + i) * 0.4f)
            drawSparkle(
                center = Offset(sx, sy),
                size   = s * 0.10f * (1f + kotlin.math.sin(sparkleP * 8f + i) * 0.4f),
                color  = SunDeep.copy(alpha = a.coerceIn(0f, 1f))
            )
        }
    }

    // 3. 4 Karten konvergieren aus den Stage-Ecken zur Mitte
    val cardBaseW = s * 1.4f
    val cardBaseH = cardBaseW * 1.42f
    suits.forEachIndexed { i, suit ->
        // Startposition: 4 Ecken
        val angleDeg = -135f + i * 90f
        val rad = Math.toRadians(angleDeg.toDouble())
        val startDist = stage.unit() * 0.55f
        val startX = centerX + kotlin.math.cos(rad).toFloat() * startDist
        val startY = centerY + kotlin.math.sin(rad).toFloat() * startDist

        val t = ((p - BK_CONVERGE_START) / (BK_CONVERGE_END - BK_CONVERGE_START))
            .coerceIn(0f, 1f)
        val eased = easeInOutCubic(t)
        val cardX = lerp(startX, centerX, eased)
        val cardY = lerp(startY, centerY, eased)
        val scale = lerp(0.40f, 1f, eased)
        // Rotation: Karten spinnen beim Konvergieren, landen in leichter Fächer-Stellung
        val rotation = lerp(360f * (i + 1).toFloat(), (i - 1.5f) * 14f, eased)
        val alpha = when {
            p < BK_CONVERGE_START -> 0f
            p > BK_FADE_START -> ((1f - p) / (1f - BK_FADE_START)).coerceAtLeast(0f)
            else -> 1f
        }
        if (alpha <= 0.01f) return@forEachIndexed

        val cardW = cardBaseW * scale
        val cardH = cardBaseH * scale
        drawMiniCard(
            topLeft = Offset(cardX - cardW / 2f, cardY - cardH / 2f),
            size    = Size(cardW, cardH),
            rank    = cue.rank,
            suit    = suit,
            measurer = measurer,
            alpha    = alpha,
            rotation = rotation
        )
    }
}

private fun DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    // 4-Punkt-Sparkle (Plus)
    drawLine(color,
        Offset(center.x - size, center.y), Offset(center.x + size, center.y),
        strokeWidth = size * 0.28f, cap = StrokeCap.Round)
    drawLine(color,
        Offset(center.x, center.y - size), Offset(center.x, center.y + size),
        strokeWidth = size * 0.28f, cap = StrokeCap.Round)
    // Diagonalen (dünner)
    val diag = size * 0.55f
    drawLine(color.copy(alpha = color.alpha * 0.55f),
        Offset(center.x - diag, center.y - diag), Offset(center.x + diag, center.y + diag),
        strokeWidth = size * 0.16f, cap = StrokeCap.Round)
    drawLine(color.copy(alpha = color.alpha * 0.55f),
        Offset(center.x - diag, center.y + diag), Offset(center.x + diag, center.y - diag),
        strokeWidth = size * 0.16f, cap = StrokeCap.Round)
    // Glanzpunkt im Zentrum
    drawCircle(Foam.copy(alpha = color.alpha), radius = size * 0.18f, center = center)
}

@Composable
private fun BookSplash(
    rank: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val p = progress
    // Splash kommt erst nach der Konvergenz
    val visibleP = ((p - 0.50f) / 0.50f).coerceIn(0f, 1f)
    val popScale = when {
        visibleP < 0.20f -> easeOutBack(visibleP / 0.20f)
        visibleP > 0.85f -> 1f - ((visibleP - 0.85f) / 0.15f) * 0.15f
        else             -> 1f
    }
    val splashAlpha = when {
        visibleP <= 0f    -> 0f
        visibleP < 0.10f  -> visibleP / 0.10f
        visibleP > 0.85f  -> ((1f - visibleP) / 0.15f).coerceAtLeast(0f)
        else              -> 1f
    }
    if (splashAlpha <= 0.001f) return

    Column(
        modifier = modifier.graphicsLayer(
            scaleX    = popScale,
            scaleY    = popScale,
            rotationZ = -2f,
            alpha     = splashAlpha
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameIcon(GameIconKind.BOOKS_CLASSIC, modifier = Modifier.size(44.dp), tint = SunYellow)
        Spacer(Modifier.height(4.dp))
        ComicTextBold(
            text        = LocalTexts.current.animBook.replace("📚", "").trim(),
            fontSize    = 56.sp,
            fillColor   = SunYellow,
            strokeColor = SuitDark,
            strokeWidth = 12f
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = LocalTexts.current.animBookComplete(rank),
            color = Foam,
            style = TextStyle(
                fontSize      = 22.sp,
                fontWeight    = FontWeight.Black,
                fontFamily    = FontFamily.SansSerif,
                letterSpacing = 1.sp,
                shadow = Shadow(
                    color = DeepSea.copy(alpha = 0.85f),
                    offset = Offset(3f, 5f),
                    blurRadius = 8f
                )
            )
        )
    }
}

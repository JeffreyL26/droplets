package com.jbastudio.gofish.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.jbastudio.gofish.store.AvatarSkinTier
import com.jbastudio.gofish.ui.theme.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────
//  Modell
// ─────────────────────────────────────────────────────────────────────────

enum class AvatarKind(
    val displayName: String,
    val emoji: String,
    val defaultTier: AvatarSkinTier,
    /** Premium-Avatare haben eine FESTE Farbe — die Farbauswahl wird ausgeblendet. */
    val fixedColor: Boolean = false
) {
    FISH    ("Fisch",        "🐟", AvatarSkinTier.FREE),
    SHARK   ("Hai",          "🦈", AvatarSkinTier.FREE),
    WHALE   ("Wal",          "🐳", AvatarSkinTier.LOCKED),
    DOLPHIN ("Delfin",       "🐬", AvatarSkinTier.LOCKED),
    PUFFER  ("Kugelfisch",   "🐡", AvatarSkinTier.LOCKED),
    STARFISH("Seestern",     "⭐", AvatarSkinTier.LOCKED),

    // ── Premium-Avatare: feste Farbe, hochdetailliert, hinter Pay-/Ad-Wall ──
    MOBY      ("Moby Dick",  "🐋", AvatarSkinTier.LOCKED, fixedColor = true),
    NESSIE    ("Nessie",     "🦕", AvatarSkinTier.LOCKED, fixedColor = true),
    BLOBFISH  ("Blobfisch",  "🐠", AvatarSkinTier.LOCKED, fixedColor = true),
    KOI       ("Koifisch",   "🎏", AvatarSkinTier.LOCKED, fixedColor = true),
    BACKFISCH ("Backfisch",  "🍤", AvatarSkinTier.LOCKED, fixedColor = true),
    MEGALODON ("Megalodon",  "🦈", AvatarSkinTier.LOCKED, fixedColor = true)
}

enum class AvatarColor(
    val body: Color,
    val bodyDeep: Color,
    val displayName: String
) {
    SUN     (SunYellow,     SunDeep,             "Sonnengelb"),
    CORAL   (CoralPink,     CoralDeep,           "Korallrosa"),
    SEAFOAM (SeafoamGreen,  SeafoamDeep,         "Seafoam"),
    LAVENDER(Lavender,      LavenderDeep,        "Lavendel"),
    OCEAN   (OceanMid,      OceanDeep,           "Ozeanblau"),
    SANDY   (SandyBeige,    Color(0xFFCB9A6B),   "Sandbeige")
}

data class AvatarChoice(
    val kind: AvatarKind   = AvatarKind.FISH,
    val color: AvatarColor = AvatarColor.SUN
)

// ─────────────────────────────────────────────────────────────────────────
//  Composable API
// ─────────────────────────────────────────────────────────────────────────

private data class AnimParams(val bob: Float, val wag: Float) {
    companion object { val STILL = AnimParams(0f, 0f) }
}

@Composable
fun Avatar(
    choice: AvatarChoice,
    modifier: Modifier = Modifier,
    facingRight: Boolean = true,
    animated: Boolean = false
) {
    if (animated) AnimatedAvatar(choice, modifier, facingRight)
    else          StaticAvatar(choice, modifier, facingRight)
}

@Composable
private fun StaticAvatar(choice: AvatarChoice, modifier: Modifier, facingRight: Boolean) {
    Canvas(modifier = modifier) {
        drawAvatar(choice.kind, choice.color.body, choice.color.bodyDeep,
            facingRight, AnimParams.STILL)
    }
}

@Composable
private fun AnimatedAvatar(choice: AvatarChoice, modifier: Modifier, facingRight: Boolean) {
    val transition = rememberInfiniteTransition(label = "avatar")
    val bob by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val wag by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wag"
    )
    Canvas(modifier = modifier) {
        drawAvatar(choice.kind, choice.color.body, choice.color.bodyDeep,
            facingRight, AnimParams(bob, wag))
    }
}

private fun DrawScope.drawAvatar(
    kind: AvatarKind,
    body: Color,
    bodyDeep: Color,
    facingRight: Boolean,
    anim: AnimParams
) {
    when (kind) {
        AvatarKind.FISH     -> drawFishShape(body, bodyDeep, facingRight, anim)
        AvatarKind.SHARK    -> drawSharkShape(body, bodyDeep, facingRight, anim)
        AvatarKind.WHALE    -> drawWhaleShape(body, bodyDeep, facingRight, anim)
        AvatarKind.DOLPHIN  -> drawDolphinShape(body, bodyDeep, facingRight, anim)
        AvatarKind.PUFFER   -> drawPufferShape(body, bodyDeep, anim)
        AvatarKind.STARFISH -> drawStarfishShape(body, bodyDeep, anim)
        // Premium-Avatare: feste Farbe (body/bodyDeep ignoriert), nach rechts gezeichnet.
        AvatarKind.MOBY      -> drawPremium(facingRight, anim) { drawMobyShape() }
        AvatarKind.NESSIE    -> drawPremium(facingRight, anim) { drawNessieShape() }
        AvatarKind.BLOBFISH  -> drawPremium(facingRight, anim) { drawBlobfishShape() }
        AvatarKind.KOI       -> drawPremium(facingRight, anim) { drawKoiShape() }
        AvatarKind.BACKFISCH -> drawPremium(facingRight, anim) { drawBackfischShape() }
        AvatarKind.MEGALODON -> drawPremium(facingRight, anim) { drawMegalodonShape() }
    }
}

private fun mirror(facingRight: Boolean, w: Float): (Float) -> Float =
    if (facingRight) { x -> x } else { x -> w - x }

/**
 * Rahmen für die Premium-Avatare: Sie werden in [PremiumAvatars.kt] immer mit Blick
 * nach rechts gezeichnet. Hier kommt das gemeinsame Verhalten dazu — sanftes
 * vertikales Bob (animiert) und die Links-Spiegelung, wenn [facingRight] false ist.
 */
private fun DrawScope.drawPremium(
    facingRight: Boolean,
    anim: AnimParams,
    block: DrawScope.() -> Unit
) {
    val dy = anim.bob * size.height * 0.03f
    translate(top = dy) {
        if (facingRight) block()
        else scale(scaleX = -1f, scaleY = 1f, pivot = Offset(size.width / 2f, size.height / 2f)) { block() }
    }
}

// ═════════════════════════════════════════════════════════════════════════
//  FISCH  (Default)
// ═════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawFishShape(body: Color, bodyDeep: Color, facingRight: Boolean, anim: AnimParams) {
    val w = size.width
    val h = size.height
    val offY    = anim.bob * h * 0.04f
    val tailWag = anim.wag * h * 0.08f
    val flipX = mirror(facingRight, w)

    // Schwanz
    val tail = Path().apply {
        moveTo(flipX(w * 0.18f), h * 0.50f + offY)
        lineTo(flipX(w * 0.02f), h * 0.25f + offY + tailWag)
        lineTo(flipX(w * 0.02f), h * 0.75f + offY - tailWag)
        close()
    }
    drawPath(tail, color = bodyDeep)

    // Körper
    val bL = w * 0.15f; val bT = h * 0.18f + offY
    val bW = w * 0.75f; val bH = h * 0.64f
    drawOval(color = body, topLeft = Offset(bL, bT), size = Size(bW, bH))
    // Bauch
    drawOval(
        color = Foam.copy(alpha = 0.55f),
        topLeft = Offset(bL + bW * 0.18f, bT + bH * 0.55f),
        size = Size(bW * 0.55f, bH * 0.35f)
    )
    // Rückenflosse
    val fin = Path().apply {
        moveTo(flipX(w * 0.45f), bT + bH * 0.05f)
        cubicTo(
            flipX(w * 0.55f), bT - bH * 0.18f,
            flipX(w * 0.65f), bT - bH * 0.08f,
            flipX(w * 0.62f), bT + bH * 0.10f
        )
        close()
    }
    drawPath(fin, color = bodyDeep)
    // Wange
    drawCircle(CoralDeep.copy(alpha = 0.40f), radius = w * 0.05f,
        center = Offset(flipX(w * 0.66f), bT + bH * 0.62f))
    // Auge
    val eyeCx = flipX(w * 0.72f); val eyeCy = bT + bH * 0.40f
    drawCircle(Foam, radius = w * 0.085f, center = Offset(eyeCx, eyeCy))
    drawCircle(DeepSea, radius = w * 0.050f,
        center = Offset(eyeCx + (if (facingRight) 1 else -1) * w * 0.012f, eyeCy + h * 0.01f))
    drawCircle(Foam, radius = w * 0.018f,
        center = Offset(eyeCx + (if (facingRight) 1 else -1) * w * 0.025f, eyeCy - h * 0.012f))
    // Lächeln
    val smile = Path().apply {
        moveTo(flipX(w * 0.78f), bT + bH * 0.62f)
        cubicTo(
            flipX(w * 0.83f), bT + bH * 0.72f,
            flipX(w * 0.86f), bT + bH * 0.68f,
            flipX(w * 0.87f), bT + bH * 0.58f
        )
    }
    drawPath(smile, color = DeepSea, style = Stroke(width = w * 0.018f, cap = StrokeCap.Round))
}

// ═════════════════════════════════════════════════════════════════════════
//  HAI
// ═════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawSharkShape(body: Color, bodyDeep: Color, facingRight: Boolean, anim: AnimParams) {
    val w = size.width
    val h = size.height
    val offY    = anim.bob * h * 0.03f
    val tailWag = anim.wag * h * 0.06f
    val flipX = mirror(facingRight, w)

    // Asymmetrischer Schwanz: oberer Fluke groß, unterer klein (Hai-typisch)
    val tail = Path().apply {
        moveTo(flipX(w * 0.18f), h * 0.42f + offY)
        lineTo(flipX(w * 0.00f), h * 0.05f + offY + tailWag * 0.5f)
        lineTo(flipX(w * 0.08f), h * 0.48f + offY)
        lineTo(flipX(w * 0.00f), h * 0.78f + offY - tailWag * 0.3f)
        lineTo(flipX(w * 0.18f), h * 0.55f + offY)
        close()
    }
    drawPath(tail, color = bodyDeep)

    // Gestreckter Körper mit spitzer Nase
    val bodyPath = Path().apply {
        moveTo(flipX(w * 0.18f), h * 0.42f + offY)
        cubicTo(
            flipX(w * 0.42f), h * 0.18f + offY,
            flipX(w * 0.78f), h * 0.25f + offY,
            flipX(w * 0.97f), h * 0.45f + offY  // Spitze Nase
        )
        cubicTo(
            flipX(w * 0.99f), h * 0.55f + offY,
            flipX(w * 0.92f), h * 0.65f + offY,
            flipX(w * 0.78f), h * 0.72f + offY
        )
        cubicTo(
            flipX(w * 0.55f), h * 0.82f + offY,
            flipX(w * 0.30f), h * 0.68f + offY,
            flipX(w * 0.18f), h * 0.55f + offY
        )
        close()
    }
    drawPath(bodyPath, color = body)

    // Heller Bauch
    val belly = Path().apply {
        moveTo(flipX(w * 0.28f), h * 0.62f + offY)
        cubicTo(
            flipX(w * 0.45f), h * 0.82f + offY,
            flipX(w * 0.72f), h * 0.78f + offY,
            flipX(w * 0.82f), h * 0.65f + offY
        )
        cubicTo(
            flipX(w * 0.62f), h * 0.65f + offY,
            flipX(w * 0.45f), h * 0.65f + offY,
            flipX(w * 0.28f), h * 0.62f + offY
        )
        close()
    }
    drawPath(belly, color = Foam.copy(alpha = 0.65f))

    // Große Rückenflosse
    val dorsal = Path().apply {
        moveTo(flipX(w * 0.42f), h * 0.22f + offY)
        lineTo(flipX(w * 0.55f), h * 0.00f + offY)
        lineTo(flipX(w * 0.62f), h * 0.27f + offY)
        close()
    }
    drawPath(dorsal, color = bodyDeep)

    // Brustflosse
    val pectoral = Path().apply {
        moveTo(flipX(w * 0.42f), h * 0.64f + offY)
        lineTo(flipX(w * 0.36f), h * 0.92f + offY)
        lineTo(flipX(w * 0.60f), h * 0.72f + offY)
        close()
    }
    drawPath(pectoral, color = bodyDeep)

    // Kiemen
    listOf(0.55f, 0.62f, 0.69f).forEach { gx ->
        drawLine(
            color = bodyDeep,
            start = Offset(flipX(w * gx), h * 0.42f + offY),
            end   = Offset(flipX(w * gx), h * 0.55f + offY),
            strokeWidth = w * 0.010f
        )
    }

    // Auge (klein, fokussiert) + scharfe Braue
    val eyeCx = flipX(w * 0.80f); val eyeCy = h * 0.43f + offY
    drawCircle(Foam,    radius = w * 0.040f, center = Offset(eyeCx, eyeCy))
    drawCircle(DeepSea, radius = w * 0.022f, center = Offset(eyeCx, eyeCy))
    drawLine(
        color = DeepSea,
        start = Offset(flipX(w * 0.74f), h * 0.36f + offY),
        end   = Offset(flipX(w * 0.86f), h * 0.40f + offY),
        strokeWidth = w * 0.013f, cap = StrokeCap.Round
    )

    // Maul-Linie
    drawLine(
        color = DeepSea.copy(alpha = 0.70f),
        start = Offset(flipX(w * 0.78f), h * 0.62f + offY),
        end   = Offset(flipX(w * 0.92f), h * 0.58f + offY),
        strokeWidth = w * 0.015f, cap = StrokeCap.Round
    )
}

// ═════════════════════════════════════════════════════════════════════════
//  WAL
// ═════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawWhaleShape(body: Color, bodyDeep: Color, facingRight: Boolean, anim: AnimParams) {
    val w = size.width
    val h = size.height
    val offY    = anim.bob * h * 0.025f
    val tailWag = anim.wag * h * 0.05f
    val flipX = mirror(facingRight, w)

    // ─── Kleine horizontale Fluke (Halfbrick-Stil: zierlich) ───
    val fluke = Path().apply {
        moveTo(flipX(w * 0.20f), h * 0.50f + offY)
        cubicTo(
            flipX(w * 0.10f), h * 0.44f + offY,
            flipX(w * 0.00f), h * 0.32f + offY + tailWag * 0.7f,
            flipX(w * -0.04f), h * 0.24f + offY + tailWag
        )
        cubicTo(
            flipX(w * 0.02f), h * 0.36f + offY,
            flipX(w * 0.10f), h * 0.46f + offY,
            flipX(w * 0.13f), h * 0.50f + offY
        )
        cubicTo(
            flipX(w * 0.10f), h * 0.54f + offY,
            flipX(w * 0.02f), h * 0.64f + offY,
            flipX(w * -0.04f), h * 0.76f + offY - tailWag
        )
        cubicTo(
            flipX(w * 0.00f), h * 0.68f + offY - tailWag * 0.7f,
            flipX(w * 0.10f), h * 0.56f + offY,
            flipX(w * 0.20f), h * 0.50f + offY
        )
        close()
    }
    drawPath(fluke, color = bodyDeep)

    // ─── Sehr runder pummeliger Körper (Halfbrick-Wal-Stil) ───
    val bodyPath = Path().apply {
        moveTo(flipX(w * 0.20f), h * 0.42f + offY)
        // Runde Oberseite über Rücken und Melone
        cubicTo(
            flipX(w * 0.28f), h * 0.10f + offY,
            flipX(w * 0.62f), h * 0.06f + offY,
            flipX(w * 0.86f), h * 0.18f + offY
        )
        // Bauchige Melone — bauscht nach vorn und unten
        cubicTo(
            flipX(w * 1.04f), h * 0.28f + offY,
            flipX(w * 1.04f), h * 0.62f + offY,
            flipX(w * 0.88f), h * 0.74f + offY
        )
        // Großer runder Bauchschwung zurück zur Fluke
        cubicTo(
            flipX(w * 0.66f), h * 0.96f + offY,
            flipX(w * 0.30f), h * 0.96f + offY,
            flipX(w * 0.12f), h * 0.74f + offY
        )
        cubicTo(
            flipX(w * 0.04f), h * 0.60f + offY,
            flipX(w * 0.10f), h * 0.48f + offY,
            flipX(w * 0.20f), h * 0.42f + offY
        )
        close()
    }
    drawPath(bodyPath, brush = Brush.verticalGradient(
        colors = listOf(bodyDeep, body, body),
        startY = h * 0.04f + offY,
        endY   = h * 0.80f + offY
    ))

    // ─── Heller großer Bauch ───
    val belly = Path().apply {
        moveTo(flipX(w * 0.18f), h * 0.60f + offY)
        cubicTo(
            flipX(w * 0.34f), h * 0.94f + offY,
            flipX(w * 0.74f), h * 0.90f + offY,
            flipX(w * 0.90f), h * 0.68f + offY
        )
        cubicTo(
            flipX(w * 0.66f), h * 0.74f + offY,
            flipX(w * 0.38f), h * 0.72f + offY,
            flipX(w * 0.18f), h * 0.60f + offY
        )
        close()
    }
    drawPath(belly, color = Foam.copy(alpha = 0.85f))

    // ─── Glänzendes Top-Highlight (3D-Pop wie bei Halfbrick-Figuren) ───
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam.copy(alpha = 0.60f), Foam.copy(alpha = 0.0f)),
            center = Offset(flipX(w * 0.52f), h * 0.20f + offY),
            radius = w * 0.38f
        ),
        radius = w * 0.38f,
        center = Offset(flipX(w * 0.52f), h * 0.20f + offY)
    )
    // Kleiner zusätzlicher Glanzpunkt am Melonenbogen
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam.copy(alpha = 0.55f), Foam.copy(alpha = 0.0f)),
            center = Offset(flipX(w * 0.78f), h * 0.30f + offY),
            radius = w * 0.10f
        ),
        radius = w * 0.10f,
        center = Offset(flipX(w * 0.78f), h * 0.30f + offY)
    )

    // ─── Brustflosse ───
    val pectoral = Path().apply {
        moveTo(flipX(w * 0.46f), h * 0.72f + offY)
        cubicTo(
            flipX(w * 0.38f), h * 0.96f + offY,
            flipX(w * 0.64f), h * 1.00f + offY,
            flipX(w * 0.62f), h * 0.80f + offY
        )
        close()
    }
    drawPath(pectoral, color = bodyDeep)

    // ─── Atemloch + animierter Wasser-Spritzer ───
    drawCircle(DeepSea.copy(alpha = 0.80f), radius = w * 0.020f,
        center = Offset(flipX(w * 0.60f), h * 0.08f + offY))
    val spoutAlpha = if (anim.bob > 0f) anim.bob * 0.90f else 0f
    if (spoutAlpha > 0.01f) {
        for (i in 0..3) {
            drawCircle(
                BubbleWhite.copy(alpha = spoutAlpha * (1f - i * 0.22f)),
                radius = w * 0.024f * (1f + i * 0.30f),
                center = Offset(
                    flipX(w * (0.60f + (i % 2) * 0.02f - 0.01f)),
                    h * (-0.02f - i * 0.04f) + offY
                )
            )
        }
    }

    // ─── GROSSES expressives Auge (Halfbrick-Markenzeichen) ───
    val eyeCx = flipX(w * 0.80f); val eyeCy = h * 0.52f + offY
    // Sklera
    drawCircle(Foam, radius = w * 0.105f, center = Offset(eyeCx, eyeCy))
    // Pupille (groß, dunkel)
    drawCircle(DeepSea, radius = w * 0.068f,
        center = Offset(eyeCx + (if (facingRight) 1 else -1) * w * 0.010f, eyeCy + h * 0.010f))
    // Glanzpunkt
    drawCircle(Foam, radius = w * 0.028f,
        center = Offset(eyeCx - w * 0.022f, eyeCy - h * 0.024f))
    // Kleiner zweiter Glanzpunkt unten
    drawCircle(Foam.copy(alpha = 0.85f), radius = w * 0.012f,
        center = Offset(eyeCx + w * 0.022f, eyeCy + h * 0.024f))

    // ─── Kleines süßes Lächeln (klein gehalten, Halfbrick-Stil) ───
    val mouth = Path().apply {
        moveTo(flipX(w * 0.82f), h * 0.72f + offY)
        cubicTo(
            flipX(w * 0.86f), h * 0.78f + offY,
            flipX(w * 0.92f), h * 0.76f + offY,
            flipX(w * 0.94f), h * 0.70f + offY
        )
    }
    drawPath(mouth, color = DeepSea, style = Stroke(width = w * 0.018f, cap = StrokeCap.Round))

    // ─── Wange ───
    drawCircle(
        color = CoralDeep.copy(alpha = 0.42f),
        radius = w * 0.045f,
        center = Offset(flipX(w * 0.72f), h * 0.66f + offY)
    )
}

// ═════════════════════════════════════════════════════════════════════════
//  DELFIN
// ═════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawDolphinShape(body: Color, bodyDeep: Color, facingRight: Boolean, anim: AnimParams) {
    val w = size.width
    val h = size.height
    val offY    = anim.bob * h * 0.03f
    val tailWag = anim.wag * h * 0.06f
    val flipX = mirror(facingRight, w)

    // ─── Gegabelte horizontale Fluke (hinten links, vom Körper trailend) ───
    val tail = Path().apply {
        moveTo(flipX(w * 0.20f), h * 0.40f + offY)
        // Oberer Lobe — nach oben-links
        cubicTo(
            flipX(w * 0.08f), h * 0.34f + offY,
            flipX(w * -0.04f), h * 0.24f + offY + tailWag * 0.8f,
            flipX(w * -0.08f), h * 0.20f + offY + tailWag
        )
        cubicTo(
            flipX(w * -0.02f), h * 0.34f + offY,
            flipX(w * 0.06f), h * 0.44f + offY,
            flipX(w * 0.10f), h * 0.48f + offY     // Mittel-Notch
        )
        // Unterer Lobe — nach unten-links
        cubicTo(
            flipX(w * 0.06f), h * 0.55f + offY,
            flipX(w * -0.02f), h * 0.62f + offY,
            flipX(w * -0.06f), h * 0.66f + offY - tailWag * 0.8f
        )
        cubicTo(
            flipX(w * 0.04f), h * 0.62f + offY - tailWag * 0.5f,
            flipX(w * 0.12f), h * 0.55f + offY,
            flipX(w * 0.20f), h * 0.52f + offY
        )
        close()
    }
    drawPath(tail, color = bodyDeep)

    // ─── Körper mit DEUTLICHER Melone → Crease → langem schmalen Rostrum ───
    val bodyPath = Path().apply {
        moveTo(flipX(w * 0.20f), h * 0.38f + offY)        // oberer Schwanzansatz
        // Rücken: hoch gewölbt
        cubicTo(
            flipX(w * 0.26f), h * 0.10f + offY,
            flipX(w * 0.48f), h * 0.06f + offY,
            flipX(w * 0.62f), h * 0.14f + offY
        )
        // MELONE: bauchig nach vorne & runter (deutlich erkennbarer Stirn-Bump)
        cubicTo(
            flipX(w * 0.74f), h * 0.18f + offY,
            flipX(w * 0.82f), h * 0.30f + offY,
            flipX(w * 0.80f), h * 0.42f + offY            // Melonen-Vorderkante
        )
        // SCHNAUZE OBEN: deutliche Crease (Einbuchtung) → schmales Rostrum nach vorne
        cubicTo(
            flipX(w * 0.74f), h * 0.47f + offY,            // CREASE: zurück & runter
            flipX(w * 0.86f), h * 0.51f + offY,
            flipX(w * 0.96f), h * 0.55f + offY            // Spitze des Schnabels oben
        )
        // SCHNAUZEN-SPITZE: gerundet
        cubicTo(
            flipX(w * 1.00f), h * 0.57f + offY,
            flipX(w * 1.00f), h * 0.61f + offY,
            flipX(w * 0.96f), h * 0.62f + offY
        )
        // SCHNAUZE UNTEN: parallel zum Oberkiefer zurück (schmaler langer Schnabel)
        cubicTo(
            flipX(w * 0.86f), h * 0.62f + offY,
            flipX(w * 0.78f), h * 0.62f + offY,
            flipX(w * 0.72f), h * 0.62f + offY            // Mundwinkel / Schnauzen-Basis
        )
        // Kiefer/Hals nach unten
        cubicTo(
            flipX(w * 0.66f), h * 0.66f + offY,
            flipX(w * 0.56f), h * 0.66f + offY,
            flipX(w * 0.48f), h * 0.66f + offY
        )
        // Bauch — KONKAV nach innen gewölbt (typische Delfin-Krümmung)
        cubicTo(
            flipX(w * 0.38f), h * 0.62f + offY,            // hochgezogen statt runtersackend
            flipX(w * 0.26f), h * 0.55f + offY,            // tiefste Einkrümmung
            flipX(w * 0.20f), h * 0.50f + offY
        )
        close()
    }
    drawPath(bodyPath, brush = Brush.verticalGradient(
        colors = listOf(bodyDeep, body, body),
        startY = h * 0.06f + offY,
        endY   = h * 0.66f + offY
    ))

    // ─── Heller Bauch (folgt der konkaven Bauchlinie) ───
    val belly = Path().apply {
        moveTo(flipX(w * 0.22f), h * 0.50f + offY)
        // Untere Kante — konkav, folgt der eingewölbten Bauchlinie
        cubicTo(
            flipX(w * 0.30f), h * 0.57f + offY,
            flipX(w * 0.42f), h * 0.62f + offY,
            flipX(w * 0.54f), h * 0.65f + offY
        )
        cubicTo(
            flipX(w * 0.68f), h * 0.63f + offY,
            flipX(w * 0.80f), h * 0.62f + offY,
            flipX(w * 0.92f), h * 0.61f + offY            // bis zur Schnauzen-Spitze
        )
        // Obere Kante des Bauchs
        cubicTo(
            flipX(w * 0.80f), h * 0.57f + offY,
            flipX(w * 0.50f), h * 0.55f + offY,
            flipX(w * 0.30f), h * 0.52f + offY
        )
        close()
    }
    drawPath(belly, color = Foam.copy(alpha = 0.82f))

    // ─── CREASE-Linie: verdeutlicht den Übergang Melone → Schnauze ───
    val crease = Path().apply {
        moveTo(flipX(w * 0.76f), h * 0.40f + offY)
        cubicTo(
            flipX(w * 0.74f), h * 0.46f + offY,
            flipX(w * 0.78f), h * 0.50f + offY,
            flipX(w * 0.86f), h * 0.52f + offY
        )
    }
    drawPath(crease, color = bodyDeep.copy(alpha = 0.55f),
        style = Stroke(width = w * 0.012f, cap = StrokeCap.Round))

    // ─── Glänzendes Top-Highlight ───
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam.copy(alpha = 0.55f), Foam.copy(alpha = 0.0f)),
            center = Offset(flipX(w * 0.42f), h * 0.14f + offY),
            radius = w * 0.32f
        ),
        radius = w * 0.32f,
        center = Offset(flipX(w * 0.42f), h * 0.14f + offY)
    )
    // Zusätzlicher Glanzpunkt auf der Melone
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam.copy(alpha = 0.45f), Foam.copy(alpha = 0.0f)),
            center = Offset(flipX(w * 0.72f), h * 0.26f + offY),
            radius = w * 0.11f
        ),
        radius = w * 0.11f,
        center = Offset(flipX(w * 0.72f), h * 0.26f + offY)
    )

    // ─── HOHE SICHELRÜCKENFLOSSE — Spitze nach HINTEN (links, zum Schwanz) ───
    val dorsal = Path().apply {
        moveTo(flipX(w * 0.52f), h * 0.13f + offY)        // Basis vorne (Kopfseite)
        // Vorderkante steil aufsteigend & sichelartig nach hinten gebogen
        cubicTo(
            flipX(w * 0.50f), h * 0.02f + offY,
            flipX(w * 0.42f), h * -0.04f + offY,
            flipX(w * 0.34f), h * 0.00f + offY            // Spitze — geneigt nach hinten/links
        )
        // Hinterkante zurück zur Basis hinten
        cubicTo(
            flipX(w * 0.34f), h * 0.06f + offY,
            flipX(w * 0.38f), h * 0.12f + offY,
            flipX(w * 0.42f), h * 0.16f + offY            // Basis hinten (Schwanzseite)
        )
        close()
    }
    drawPath(dorsal, color = bodyDeep)

    // ─── Sichelförmige Brustflosse ───
    val pectoral = Path().apply {
        moveTo(flipX(w * 0.52f), h * 0.63f + offY)
        cubicTo(
            flipX(w * 0.40f), h * 0.86f + offY,
            flipX(w * 0.62f), h * 0.92f + offY,
            flipX(w * 0.64f), h * 0.72f + offY
        )
        close()
    }
    drawPath(pectoral, color = bodyDeep)

    // ─── Mund-Linie am Schnabel mit Smile-Upturn am Mundwinkel ───
    val mouth = Path().apply {
        moveTo(flipX(w * 0.94f), h * 0.59f + offY)        // bei der Schnauzen-Spitze
        cubicTo(
            flipX(w * 0.88f), h * 0.63f + offY,
            flipX(w * 0.80f), h * 0.63f + offY,
            flipX(w * 0.74f), h * 0.57f + offY            // hochgezogener Mundwinkel
        )
    }
    drawPath(mouth, color = DeepSea, style = Stroke(width = w * 0.015f, cap = StrokeCap.Round))

    // ─── GROSSES expressives Auge auf der vorderen Melone ───
    val eyeCx = flipX(w * 0.72f); val eyeCy = h * 0.40f + offY
    drawCircle(Foam, radius = w * 0.078f, center = Offset(eyeCx, eyeCy))
    drawCircle(DeepSea, radius = w * 0.050f,
        center = Offset(eyeCx + (if (facingRight) 1 else -1) * w * 0.008f, eyeCy + h * 0.008f))
    drawCircle(Foam, radius = w * 0.023f,
        center = Offset(eyeCx - w * 0.020f, eyeCy - h * 0.022f))
    drawCircle(Foam.copy(alpha = 0.85f), radius = w * 0.011f,
        center = Offset(eyeCx + w * 0.020f, eyeCy + h * 0.022f))

    // ─── Rosa Wange (hinter-unter dem Auge) ───
    drawCircle(
        color = CoralDeep.copy(alpha = 0.50f),
        radius = w * 0.040f,
        center = Offset(flipX(w * 0.64f), h * 0.52f + offY)
    )
}

// ═════════════════════════════════════════════════════════════════════════
//  KUGELFISCH (Pufferfish)
// ═════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawPufferShape(body: Color, bodyDeep: Color, anim: AnimParams) {
    val w = size.width
    val h = size.height
    val offY = anim.bob * h * 0.04f
    val cx = w * 0.50f
    val cy = h * 0.50f + offY
    val r  = min(w, h) * 0.32f      // etwas kleiner, damit Flossen Platz haben

    // ─── Frontale Brustflossen (links + rechts, symmetrisch) ───
    // Zuerst gezeichnet → liegen hinter Körper & Stacheln
    val finTopY  = cy - r * 0.08f
    val finBotY  = cy + r * 0.22f
    val finTipY  = cy + r * 0.05f
    val finReach = r * 0.55f

    val leftFin = Path().apply {
        moveTo(cx - r * 0.95f, finTopY)
        cubicTo(
            cx - r * 0.95f - finReach, cy - r * 0.20f,
            cx - r * 0.95f - finReach * 1.10f, finTipY,
            cx - r * 0.95f - finReach * 0.85f, finBotY
        )
        cubicTo(
            cx - r * 0.95f - finReach * 0.35f, cy + r * 0.25f,
            cx - r * 0.95f - finReach * 0.10f, cy + r * 0.10f,
            cx - r * 0.95f, finTopY
        )
        close()
    }
    drawPath(leftFin, color = bodyDeep)
    // Flossenstrahlen links (3 dünne Bögen für Textur)
    listOf(0.20f, 0.45f, 0.70f).forEach { t ->
        drawLine(
            color = body.copy(alpha = 0.55f),
            start = Offset(cx - r * 0.95f, cy + r * (-0.05f + t * 0.20f)),
            end = Offset(cx - r * 0.95f - finReach * 0.85f, cy + r * (0.00f + t * 0.15f)),
            strokeWidth = r * 0.035f, cap = StrokeCap.Round
        )
    }

    val rightFin = Path().apply {
        moveTo(cx + r * 0.95f, finTopY)
        cubicTo(
            cx + r * 0.95f + finReach, cy - r * 0.20f,
            cx + r * 0.95f + finReach * 1.10f, finTipY,
            cx + r * 0.95f + finReach * 0.85f, finBotY
        )
        cubicTo(
            cx + r * 0.95f + finReach * 0.35f, cy + r * 0.25f,
            cx + r * 0.95f + finReach * 0.10f, cy + r * 0.10f,
            cx + r * 0.95f, finTopY
        )
        close()
    }
    drawPath(rightFin, color = bodyDeep)
    listOf(0.20f, 0.45f, 0.70f).forEach { t ->
        drawLine(
            color = body.copy(alpha = 0.55f),
            start = Offset(cx + r * 0.95f, cy + r * (-0.05f + t * 0.20f)),
            end = Offset(cx + r * 0.95f + finReach * 0.85f, cy + r * (0.00f + t * 0.15f)),
            strokeWidth = r * 0.035f, cap = StrokeCap.Round
        )
    }

    // ─── Stacheln rundherum (Frontalansicht: ringsum sichtbar) ───
    val spikeCount = 14
    for (i in 0 until spikeCount) {
        val angle = (i.toFloat() / spikeCount) * 360f
        val rad   = Math.toRadians(angle.toDouble())
        val rad1  = Math.toRadians((angle - 4f).toDouble())
        val rad2  = Math.toRadians((angle + 4f).toDouble())
        val innerR = r
        val outerR = r * 1.20f
        val spike = Path().apply {
            moveTo(cx + cos(rad1).toFloat() * innerR, cy + sin(rad1).toFloat() * innerR)
            lineTo(cx + cos(rad).toFloat()  * outerR, cy + sin(rad).toFloat()  * outerR)
            lineTo(cx + cos(rad2).toFloat() * innerR, cy + sin(rad2).toFloat() * innerR)
            close()
        }
        drawPath(spike, color = bodyDeep)
    }

    // ─── Kugel-Körper ───
    drawCircle(body, radius = r, center = Offset(cx, cy))
    // Bauch-Highlight (oben links)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Foam.copy(alpha = 0.60f), Color.Transparent),
            center = Offset(cx - r * 0.30f, cy - r * 0.30f),
            radius = r * 0.55f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
    // Ventraler Schatten (unten rechts) — gibt etwas Volumen
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, bodyDeep.copy(alpha = 0.22f)),
            center = Offset(cx + r * 0.30f, cy + r * 0.30f),
            radius = r
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // ─── Wangen (frontal, links + rechts) ───
    drawCircle(CoralDeep.copy(alpha = 0.45f), radius = r * 0.10f,
        center = Offset(cx - r * 0.45f, cy + r * 0.15f))
    drawCircle(CoralDeep.copy(alpha = 0.45f), radius = r * 0.10f,
        center = Offset(cx + r * 0.45f, cy + r * 0.15f))

    // ─── Zwei runde Augen (frontal) ───
    val eyeY = cy - r * 0.10f
    listOf(-0.22f, 0.22f).forEach { ox ->
        val ecx = cx + r * ox
        // Augenhöhle (sanfter Schatten)
        drawCircle(bodyDeep.copy(alpha = 0.25f), radius = r * 0.20f,
            center = Offset(ecx, eyeY + r * 0.02f))
        drawCircle(Foam,    radius = r * 0.18f, center = Offset(ecx, eyeY))
        drawCircle(DeepSea, radius = r * 0.10f, center = Offset(ecx, eyeY + r * 0.02f))
        drawCircle(Foam,    radius = r * 0.040f,
            center = Offset(ecx - r * 0.040f, eyeY - r * 0.040f))
    }

    // ─── Süßer kleiner Mund (frontal, gepuckert) ───
    val mouth = Path().apply {
        moveTo(cx - r * 0.10f, cy + r * 0.32f)
        quadraticBezierTo(cx, cy + r * 0.44f, cx + r * 0.10f, cy + r * 0.32f)
        quadraticBezierTo(cx, cy + r * 0.36f, cx - r * 0.10f, cy + r * 0.32f)
        close()
    }
    drawPath(mouth, color = CoralDeep.copy(alpha = 0.85f))
    drawPath(mouth, color = DeepSea,
        style = Stroke(width = r * 0.035f, cap = StrokeCap.Round))
}

// ═════════════════════════════════════════════════════════════════════════
//  SEESTERN
// ═════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawStarfishShape(body: Color, bodyDeep: Color, anim: AnimParams) {
    val w = size.width
    val h = size.height
    val offY = anim.bob * h * 0.03f
    val cx = w * 0.50f
    val cy = h * 0.50f + offY
    val outerR = min(w, h) * 0.45f
    val innerR = outerR * 0.42f

    // 5-Stern-Path (Helper)
    fun starPath(scale: Float, dy: Float = 0f): Path {
        val path = Path()
        for (i in 0 until 10) {
            val angle = -90f + i * 36f      // Spitze oben
            val rad = Math.toRadians(angle.toDouble())
            val r = (if (i % 2 == 0) outerR else innerR) * scale
            val x = cx + cos(rad).toFloat() * r
            val y = cy + dy + sin(rad).toFloat() * r
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    // Dunkler Schatten unten
    drawPath(starPath(1.00f, dy = 3f), color = bodyDeep)
    // Heller Stern oben drauf (3D-Effekt)
    drawPath(starPath(0.95f, dy = 0f), color = body)

    // Sommersprossen
    val freckles = listOf(
        Offset(cx - outerR * 0.18f, cy + outerR * 0.05f),
        Offset(cx + outerR * 0.20f, cy + outerR * 0.08f),
        Offset(cx + outerR * 0.05f, cy + outerR * 0.22f),
        Offset(cx - outerR * 0.22f, cy + outerR * 0.25f),
        Offset(cx + outerR * 0.27f, cy + outerR * 0.20f)
    )
    freckles.forEach { pos ->
        drawCircle(bodyDeep.copy(alpha = 0.55f),
            radius = outerR * 0.045f, center = pos)
    }

    // Augen — leicht über der Mitte, gemütlich angeordnet
    val eyeY = cy - outerR * 0.12f
    listOf(-0.10f, 0.10f).forEach { ox ->
        val ecx = cx + outerR * ox
        drawCircle(Foam,    radius = outerR * 0.13f, center = Offset(ecx, eyeY))
        drawCircle(DeepSea, radius = outerR * 0.075f,
            center = Offset(ecx, eyeY + outerR * 0.015f))
        drawCircle(Foam,    radius = outerR * 0.030f,
            center = Offset(ecx - outerR * 0.030f, eyeY - outerR * 0.035f))
    }

    // Wangen
    drawCircle(CoralDeep.copy(alpha = 0.45f), radius = outerR * 0.06f,
        center = Offset(cx - outerR * 0.22f, cy + outerR * 0.02f))
    drawCircle(CoralDeep.copy(alpha = 0.45f), radius = outerR * 0.06f,
        center = Offset(cx + outerR * 0.22f, cy + outerR * 0.02f))

    // Lächeln
    val smile = Path().apply {
        moveTo(cx - outerR * 0.11f, cy + outerR * 0.04f)
        quadraticBezierTo(cx, cy + outerR * 0.13f,
            cx + outerR * 0.11f, cy + outerR * 0.04f)
    }
    drawPath(smile, color = DeepSea,
        style = Stroke(width = outerR * 0.045f, cap = StrokeCap.Round))
}

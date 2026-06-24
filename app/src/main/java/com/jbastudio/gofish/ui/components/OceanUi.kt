package com.jbastudio.gofish.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbastudio.gofish.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────
//  OZEAN-HINTERGRUND mit animierten Blubberblasen
// ─────────────────────────────────────────────────────────────────────────

private data class Bubble(
    val xFraction: Float,
    val radius: Float,
    val speed: Float,
    val phase: Float,
    val wobble: Float,
    val alpha: Float
)

@Composable
fun OceanBackground(
    modifier: Modifier = Modifier,
    bubbleCount: Int = 18,
    content: @Composable BoxScope.() -> Unit
) {
    val bubbles = remember {
        val r = Random(42)
        List(bubbleCount) {
            Bubble(
                xFraction = r.nextFloat(),
                radius    = 6f + r.nextFloat() * 22f,
                speed     = 0.35f + r.nextFloat() * 0.55f,
                phase     = r.nextFloat(),
                wobble    = 8f + r.nextFloat() * 22f,
                alpha     = 0.35f + r.nextFloat() * 0.45f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "bubbles")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to OceanTop,
                    0.55f to OceanMid,
                    1.0f to OceanDeep
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBubbles(bubbles, time)
            drawSeaweed()
        }
        content()
    }
}

private fun DrawScope.drawBubbles(bubbles: List<Bubble>, time: Float) {
    val w = size.width
    val h = size.height
    bubbles.forEach { b ->
        val progress = ((time + b.phase) * b.speed) % 1f
        val y = h * (1.05f - progress * 1.15f)
        val wobbleX = sin((progress * 4f + b.phase) * PI.toFloat() * 2f) * b.wobble
        val x = b.xFraction * w + wobbleX
        val color = BubbleWhite.copy(alpha = b.alpha * (1f - progress * 0.4f))
        drawCircle(
            color  = color,
            radius = b.radius,
            center = Offset(x, y)
        )
        // kleines Highlight
        drawCircle(
            color  = Foam.copy(alpha = b.alpha * 0.9f),
            radius = b.radius * 0.28f,
            center = Offset(x - b.radius * 0.35f, y - b.radius * 0.35f)
        )
    }
}

private fun DrawScope.drawSeaweed() {
    val h = size.height
    val w = size.width
    // ein paar weiche Seegras-Schwünge unten – nur dekorativ
    listOf(0.12f to SeafoamDeep, 0.45f to SeafoamGreen, 0.78f to SeafoamDeep).forEach { (xf, c) ->
        val baseX = w * xf
        val path = Path().apply {
            moveTo(baseX, h)
            cubicTo(baseX - 18f, h - 60f, baseX + 24f, h - 120f, baseX - 6f, h - 180f)
        }
        drawPath(path, color = c.copy(alpha = 0.35f), style = Stroke(width = 14f))
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  FISCH-MASKOTTCHEN (Cartoon-Fisch via Canvas)
// ─────────────────────────────────────────────────────────────────────────

@Composable
fun FishMascot(
    modifier: Modifier = Modifier,
    body: Color = CoralPink,
    bodyDeep: Color = CoralDeep,
    facingRight: Boolean = true,
    bobbing: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "fish")
    val bob by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "bob"
    )
    val tailWag by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "wag"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val offY = if (bobbing) bob * (h * 0.04f) else 0f

        // Spiegeln, falls Fisch nach links schauen soll
        val flipX: (Float) -> Float = if (facingRight) { x -> x } else { x -> w - x }

        // Schwanz (Dreieck)
        val tailWagY = tailWag * (h * 0.08f)
        val tail = Path().apply {
            moveTo(flipX(w * 0.18f), h * 0.5f + offY)
            lineTo(flipX(w * 0.02f), h * 0.25f + offY + tailWagY)
            lineTo(flipX(w * 0.02f), h * 0.75f + offY - tailWagY)
            close()
        }
        drawPath(tail, color = bodyDeep)

        // Körper (Ellipse)
        val bodyLeft = w * 0.15f
        val bodyTop  = h * 0.18f + offY
        val bodyW    = w * 0.75f
        val bodyH    = h * 0.64f
        drawOval(
            color   = body,
            topLeft = Offset(bodyLeft, bodyTop),
            size    = Size(bodyW, bodyH)
        )

        // Bauch-Highlight
        drawOval(
            color   = Foam.copy(alpha = 0.55f),
            topLeft = Offset(bodyLeft + bodyW * 0.18f, bodyTop + bodyH * 0.55f),
            size    = Size(bodyW * 0.55f, bodyH * 0.35f)
        )

        // Flosse oben
        val finPath = Path().apply {
            moveTo(flipX(w * 0.45f), bodyTop + bodyH * 0.05f)
            cubicTo(
                flipX(w * 0.55f), bodyTop - bodyH * 0.18f,
                flipX(w * 0.65f), bodyTop - bodyH * 0.08f,
                flipX(w * 0.62f), bodyTop + bodyH * 0.10f
            )
            close()
        }
        drawPath(finPath, color = bodyDeep)

        // Wange
        drawCircle(
            color  = CoralDeep.copy(alpha = 0.45f),
            radius = w * 0.05f,
            center = Offset(flipX(w * 0.66f), bodyTop + bodyH * 0.62f)
        )

        // Auge weiß
        val eyeCx = flipX(w * 0.72f)
        val eyeCy = bodyTop + bodyH * 0.40f
        drawCircle(Foam, radius = w * 0.085f, center = Offset(eyeCx, eyeCy))
        // Pupille
        drawCircle(DeepSea, radius = w * 0.05f, center = Offset(eyeCx + (if (facingRight) 1 else -1) * w * 0.012f, eyeCy + h * 0.01f))
        // Highlight
        drawCircle(Foam, radius = w * 0.018f, center = Offset(eyeCx + (if (facingRight) 1 else -1) * w * 0.025f, eyeCy - h * 0.012f))

        // Lächeln
        val smile = Path().apply {
            moveTo(flipX(w * 0.78f), bodyTop + bodyH * 0.62f)
            cubicTo(
                flipX(w * 0.83f), bodyTop + bodyH * 0.72f,
                flipX(w * 0.86f), bodyTop + bodyH * 0.68f,
                flipX(w * 0.87f), bodyTop + bodyH * 0.58f
            )
        }
        drawPath(smile, color = DeepSea, style = Stroke(width = w * 0.018f))
    }
}

/**
 * Statische, nicht-animierte Mini-Version des Maskottchens — z. B. fürs Log,
 * wo dauernde Animationen ablenkend wären.
 */
@Composable
fun MiniFish(
    modifier: Modifier = Modifier,
    body: Color = CoralPink,
    bodyDeep: Color = CoralDeep,
    facingRight: Boolean = true
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val flipX: (Float) -> Float = if (facingRight) { x -> x } else { x -> w - x }

        // Schwanz
        val tail = Path().apply {
            moveTo(flipX(w * 0.18f), h * 0.50f)
            lineTo(flipX(w * 0.02f), h * 0.25f)
            lineTo(flipX(w * 0.02f), h * 0.75f)
            close()
        }
        drawPath(tail, color = bodyDeep)

        // Körper
        val bodyLeft = w * 0.15f
        val bodyTop  = h * 0.18f
        val bodyW    = w * 0.75f
        val bodyH    = h * 0.64f
        drawOval(
            color = body,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyW, bodyH)
        )
        // Bauch-Highlight
        drawOval(
            color = Foam.copy(alpha = 0.50f),
            topLeft = Offset(bodyLeft + bodyW * 0.18f, bodyTop + bodyH * 0.55f),
            size = Size(bodyW * 0.55f, bodyH * 0.35f)
        )
        // Flosse oben
        val finPath = Path().apply {
            moveTo(flipX(w * 0.45f), bodyTop + bodyH * 0.05f)
            cubicTo(
                flipX(w * 0.55f), bodyTop - bodyH * 0.18f,
                flipX(w * 0.65f), bodyTop - bodyH * 0.08f,
                flipX(w * 0.62f), bodyTop + bodyH * 0.10f
            )
            close()
        }
        drawPath(finPath, color = bodyDeep)
        // Auge
        val eyeCx = flipX(w * 0.72f)
        val eyeCy = bodyTop + bodyH * 0.40f
        drawCircle(Foam, radius = w * 0.10f, center = Offset(eyeCx, eyeCy))
        drawCircle(DeepSea, radius = w * 0.055f,
            center = Offset(eyeCx + (if (facingRight) 1 else -1) * w * 0.012f, eyeCy + h * 0.01f))
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  SPIELKARTE (cartoonish, pastell)
// ─────────────────────────────────────────────────────────────────────────

@Composable
fun PlayingCardView(
    rank: String,
    suit: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    highlighted: Boolean = false,
    highlightStolen: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val isRed = suit == "♥" || suit == "♦"
    val suitColor = if (isRed) SuitRed else SuitDark

    // Highlight-Farbe: grün für vom Gegner geangelte Karten, sonst gelb (Go Fish).
    val hlAccent = if (highlightStolen) SeafoamDeep else SunDeep
    val hlGlow   = if (highlightStolen) SeafoamGreen else SunYellow

    // Pulsierendes Highlight, wenn die Karte gerade neu in die Hand kam
    val pulse by rememberInfiniteTransition(label = "cardPulse").animateFloat(
        initialValue = 0.35f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseAlpha = if (highlighted) pulse else 0f
    val scale      = if (highlighted) 1f + pulse * 0.06f else 1f

    val borderColor = when {
        highlighted -> hlAccent
        selected    -> SunDeep
        else        -> Color.Transparent
    }
    val bg = when {
        highlighted -> hlGlow.copy(alpha = 0.55f + pulse * 0.3f)
        selected    -> SunYellow.copy(alpha = 0.5f)
        else        -> Foam
    }

    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(
                elevation = when {
                    highlighted -> 14.dp
                    selected    -> 10.dp
                    else        -> 6.dp
                },
                shape = RoundedCornerShape(14.dp),
                ambientColor = if (highlighted) hlAccent.copy(alpha = pulseAlpha) else DeepSea.copy(alpha = 0.25f),
                spotColor    = if (highlighted) hlAccent.copy(alpha = pulseAlpha) else DeepSea.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
            .padding(2.dp)
    ) {
        // Highlight-Streifen oben
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(OceanTop.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )
        // Rang oben links
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 6.dp, top = 4.dp)
        ) {
            Text(
                text = rank,
                color = suitColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = suit,
                color = suitColor,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        // Großes Suit-Symbol in der Mitte
        Text(
            text = suit,
            color = suitColor,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
        // Rang unten rechts (gespiegelt-ähnlich)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 6.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = suit,
                color = suitColor,
                fontSize = 14.sp
            )
            Text(
                text = rank,
                color = suitColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
        // Selektions- / Highlight-Rahmen
        if (selected || highlighted) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = if (highlighted) 4.dp else 3.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(14.dp)
                    )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Kleine Helfer
// ─────────────────────────────────────────────────────────────────────────

@Composable
fun BubblePanel(
    modifier: Modifier = Modifier,
    background: Color = Foam,
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = DeepSea.copy(alpha = 0.25f),
                spotColor = DeepSea.copy(alpha = 0.25f)
            )
            .clip(shape)
            .background(background)
            // Klick NACH dem clip → das Aufleuchten (Ripple) folgt der abgerundeten
            // Panel-Form statt der rechteckigen Hitbox.
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        content()
    }
}


package com.jbastudio.gofish.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.jbastudio.gofish.ui.theme.DeepSea
import kotlin.math.cos
import kotlin.math.sin

/**
 * Selbst gezeichneter, kohärenter Flat-Icon-Satz — ersetzt die Emojis der App.
 *
 * Bewusst flach/zweifarbig in der App-Palette gehalten (statt glänzender,
 * mehrfarbiger Emojis). Wird via Canvas gezeichnet, passend zum restlichen
 * Grafik-Stil (Avatare, Karten, Animationen), frei skalier- und tintbar.
 */
enum class GameIconKind {
    GLOBE, HOME, ROD, WAVE, FISH, SATELLITE, HAND, SEARCH, PENCIL, CHECK, CLOSE,
    DECK, CARD, BOOKS, BOOKS_CLASSIC, SCROLL, ARROW_UP, HOURGLASS, TROPHY, HOOK, HANDSHAKE,
    CLAPPER, FLAG_CHECKERED
}

@Composable
fun GameIcon(
    kind: GameIconKind,
    modifier: Modifier = Modifier,
    tint: Color = DeepSea
) {
    Canvas(modifier = modifier) { drawGameIcon(kind, tint) }
}

private fun DrawScope.drawGameIcon(kind: GameIconKind, c: Color) {
    when (kind) {
        GameIconKind.GLOBE          -> drawGlobe(c)
        GameIconKind.HOME           -> drawHome(c)
        GameIconKind.ROD            -> drawRod(c)
        GameIconKind.WAVE           -> drawWave(c)
        GameIconKind.FISH           -> drawFishIcon(c)
        GameIconKind.SATELLITE      -> drawSatellite(c)
        GameIconKind.HAND           -> drawHand(c)
        GameIconKind.SEARCH         -> drawSearch(c)
        GameIconKind.PENCIL         -> drawPencil(c)
        GameIconKind.CHECK          -> drawCheck(c)
        GameIconKind.CLOSE          -> drawClose(c)
        GameIconKind.DECK           -> drawDeck(c)
        GameIconKind.CARD           -> drawCard(c)
        GameIconKind.BOOKS          -> drawBooks(c)
        GameIconKind.BOOKS_CLASSIC  -> drawBooksClassic(c)
        GameIconKind.SCROLL         -> drawScroll(c)
        GameIconKind.ARROW_UP       -> drawArrowUp(c)
        GameIconKind.HOURGLASS      -> drawHourglass(c)
        GameIconKind.TROPHY         -> drawTrophy(c)
        GameIconKind.HOOK           -> drawHook(c)
        GameIconKind.HANDSHAKE      -> drawHandshake(c)
        GameIconKind.CLAPPER        -> drawClapper(c)
        GameIconKind.FLAG_CHECKERED -> drawCheckeredFlag(c)
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.sw() = size.minDimension * 0.085f
private fun DrawScope.line(a: Offset, b: Offset, c: Color, w: Float = sw()) =
    drawLine(c, a, b, strokeWidth = w, cap = StrokeCap.Round)

private fun DrawScope.strokePath(p: Path, c: Color, w: Float = sw()) =
    drawPath(p, c, style = Stroke(width = w, cap = StrokeCap.Round, join = StrokeJoin.Round))

// ─────────────────────────────────────────────────────────────────────────
//  Icons (Koordinaten relativ zur Canvas-Größe w×h)
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawGlobe(c: Color) {
    val w = size.width; val h = size.height; val cx = w / 2f; val cy = h / 2f
    val r = size.minDimension * 0.40f
    val s = sw()
    // Kugel
    drawCircle(c, radius = r, center = Offset(cx, cy), style = Stroke(width = s))
    // Äquator
    drawLine(c, Offset(cx - r, cy), Offset(cx + r, cy), strokeWidth = s * 0.7f)
    // zwei Breitengrade (innerhalb der Kugel verkürzt)
    val ly = r * 0.45f; val lx = r * 0.89f
    drawLine(c, Offset(cx - lx, cy - ly), Offset(cx + lx, cy - ly), strokeWidth = s * 0.6f)
    drawLine(c, Offset(cx - lx, cy + ly), Offset(cx + lx, cy + ly), strokeWidth = s * 0.6f)
    // Meridian (senkrechte Ellipse durch die Pole)
    drawOval(
        c,
        topLeft = Offset(cx - r * 0.5f, cy - r),
        size = Size(r, r * 2f),
        style = Stroke(width = s * 0.7f)
    )
}

private fun DrawScope.drawHome(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    val roof = Path().apply {
        moveTo(w * 0.12f, h * 0.50f)
        lineTo(w * 0.50f, h * 0.16f)
        lineTo(w * 0.88f, h * 0.50f)
    }
    strokePath(roof, c, s)
    // Korpus
    val body = Path().apply {
        moveTo(w * 0.22f, h * 0.46f)
        lineTo(w * 0.22f, h * 0.84f)
        lineTo(w * 0.78f, h * 0.84f)
        lineTo(w * 0.78f, h * 0.46f)
    }
    strokePath(body, c, s)
    // Tür
    drawRect(c, topLeft = Offset(w * 0.43f, h * 0.60f), size = Size(w * 0.14f, h * 0.24f))
}

private fun DrawScope.drawRod(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    // Rute (diagonal)
    line(Offset(w * 0.18f, h * 0.86f), Offset(w * 0.80f, h * 0.16f), c, s)
    // Griff-Markierung
    line(Offset(w * 0.18f, h * 0.86f), Offset(w * 0.30f, h * 0.72f), c, s * 1.5f)
    // Schnur + Haken
    val lineEnd = Offset(w * 0.80f, h * 0.16f)
    line(lineEnd, Offset(w * 0.80f, h * 0.50f), c, s * 0.6f)
    val hook = Path().apply {
        moveTo(w * 0.80f, h * 0.50f)
        cubicTo(w * 0.80f, h * 0.66f, w * 0.66f, h * 0.66f, w * 0.66f, h * 0.54f)
    }
    strokePath(hook, c, s * 0.7f)
}

private fun DrawScope.drawWave(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    listOf(0.40f, 0.66f).forEach { yf ->
        val p = Path().apply {
            moveTo(w * 0.10f, h * yf)
            cubicTo(w * 0.27f, h * (yf - 0.16f), w * 0.40f, h * (yf + 0.16f), w * 0.55f, h * yf)
            cubicTo(w * 0.70f, h * (yf - 0.16f), w * 0.83f, h * (yf + 0.16f), w * 0.92f, h * yf)
        }
        strokePath(p, c, s)
    }
}

private fun DrawScope.drawFishIcon(c: Color) {
    val w = size.width; val h = size.height; val cy = h / 2f
    // Körper
    drawOval(c, topLeft = Offset(w * 0.18f, h * 0.30f), size = Size(w * 0.52f, h * 0.40f))
    // Schwanz
    val tail = Path().apply {
        moveTo(w * 0.70f, cy)
        lineTo(w * 0.90f, h * 0.30f)
        lineTo(w * 0.90f, h * 0.70f)
        close()
    }
    drawPath(tail, c)
    // Auge (ausgespart)
    drawCircle(Color.White, radius = size.minDimension * 0.05f, center = Offset(w * 0.32f, h * 0.45f))
}

private fun DrawScope.drawSatellite(c: Color) {
    val w = size.width; val h = size.height; val s = sw(); val cx = w / 2f; val cy = h / 2f
    // Korpus
    drawRect(c, topLeft = Offset(cx - w * 0.10f, cy - h * 0.16f), size = Size(w * 0.20f, h * 0.32f))
    // Solarpaneele
    drawRect(c, topLeft = Offset(w * 0.05f, cy - h * 0.10f), size = Size(w * 0.28f, h * 0.20f),
        style = Stroke(width = s * 0.8f))
    drawRect(c, topLeft = Offset(w * 0.67f, cy - h * 0.10f), size = Size(w * 0.28f, h * 0.20f),
        style = Stroke(width = s * 0.8f))
    // Antenne / Schüssel
    line(Offset(cx, cy - h * 0.16f), Offset(cx + w * 0.06f, cy - h * 0.34f), c, s * 0.7f)
    drawCircle(c, radius = size.minDimension * 0.07f, center = Offset(cx + w * 0.06f, cy - h * 0.34f),
        style = Stroke(width = s * 0.7f))
}

private fun DrawScope.drawHand(c: Color) {
    val w = size.width; val h = size.height
    val palmTop = h * 0.46f
    // Handfläche — abgerundet, frontal (flache „Stopp"-Hand)
    drawRoundRectCompat(c, Offset(w * 0.28f, palmTop), Size(w * 0.48f, h * 0.36f), w * 0.13f)
    // Vier Finger dicht beieinander, leichte Längenstaffelung (Mittelfinger am längsten);
    // die Unterkanten ragen in die Handfläche, sodass alles verbunden wirkt.
    val fw = w * 0.105f
    val gap = w * 0.018f
    val startX = w * 0.285f
    val fingerBottom = palmTop + h * 0.07f
    val tipY = floatArrayOf(0.27f, 0.21f, 0.24f, 0.30f)   // Zeige-, Mittel-, Ring-, kleiner Finger
    for (i in 0..3) {
        val x = startX + i * (fw + gap)
        drawRoundRectCompat(c, Offset(x, h * tipY[i]), Size(fw, fingerBottom - h * tipY[i]), fw * 0.5f)
    }
    // Daumen — vom Handballen schräg nach oben-außen: offene, präsentierende Handfläche
    line(Offset(w * 0.33f, h * 0.58f), Offset(w * 0.15f, h * 0.44f), c, w * 0.13f)
}

private fun DrawScope.drawSearch(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    val r = size.minDimension * 0.26f
    drawCircle(c, radius = r, center = Offset(w * 0.42f, h * 0.42f), style = Stroke(width = s))
    line(Offset(w * 0.61f, h * 0.61f), Offset(w * 0.82f, h * 0.82f), c, s * 1.2f)
}

private fun DrawScope.drawPencil(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    // Körper
    val body = Path().apply {
        moveTo(w * 0.28f, h * 0.78f)
        lineTo(w * 0.70f, h * 0.36f)
        lineTo(w * 0.84f, h * 0.50f)
        lineTo(w * 0.42f, h * 0.92f)
        close()
    }
    strokePath(body, c, s)
    // Spitze
    val tip = Path().apply {
        moveTo(w * 0.28f, h * 0.78f)
        lineTo(w * 0.20f, h * 0.92f)
        lineTo(w * 0.42f, h * 0.92f)
    }
    drawPath(tip, c)
}

private fun DrawScope.drawCheck(c: Color) {
    val w = size.width; val h = size.height; val s = sw() * 1.2f
    val p = Path().apply {
        moveTo(w * 0.22f, h * 0.54f)
        lineTo(w * 0.42f, h * 0.74f)
        lineTo(w * 0.80f, h * 0.30f)
    }
    strokePath(p, c, s)
}

private fun DrawScope.drawClose(c: Color) {
    val w = size.width; val h = size.height; val s = sw() * 1.2f
    line(Offset(w * 0.26f, h * 0.26f), Offset(w * 0.74f, h * 0.74f), c, s)
    line(Offset(w * 0.74f, h * 0.26f), Offset(w * 0.26f, h * 0.74f), c, s)
}

private fun DrawScope.drawDeck(c: Color) {
    val w = size.width; val h = size.height; val s = sw() * 0.8f
    // hintere Karte
    drawRoundRectCompat(c, Offset(w * 0.22f, h * 0.18f), Size(w * 0.46f, h * 0.58f), w * 0.06f,
        stroke = s)
    // vordere Karte
    drawRoundRectCompat(c, Offset(w * 0.34f, h * 0.30f), Size(w * 0.46f, h * 0.58f), w * 0.06f,
        stroke = s, fillFirst = true)
    // Symbol
    drawCircle(c, radius = size.minDimension * 0.07f, center = Offset(w * 0.57f, h * 0.59f))
}

private fun DrawScope.drawCard(c: Color) {
    val w = size.width; val h = size.height; val s = sw() * 0.8f
    drawRoundRectCompat(c, Offset(w * 0.28f, h * 0.18f), Size(w * 0.44f, h * 0.64f), w * 0.07f,
        stroke = s)
    // kleiner Stern in der Mitte
    drawStar(Offset(w * 0.50f, h * 0.50f), size.minDimension * 0.13f, c)
}

private fun DrawScope.drawBooks(c: Color) {
    val w = size.width; val h = size.height; val s = sw() * 0.8f
    val bh = h * 0.185f
    val r  = w * 0.035f
    // Drei liegende Bücher, abwechselnd versetzt — je mit gefülltem Buchrücken
    // und einem angedeuteten Seitenschnitt am gegenüberliegenden Ende.
    val xs        = floatArrayOf(0.19f, 0.27f, 0.17f)
    val ys        = floatArrayOf(0.585f, 0.385f, 0.185f)
    val ws        = floatArrayOf(0.60f, 0.52f, 0.56f)
    val spineLeft = booleanArrayOf(true, false, true)
    for (i in 0..2) {
        val x = w * xs[i]; val y = h * ys[i]; val bw = w * ws[i]
        drawRoundRectCompat(c, Offset(x, y), Size(bw, bh), r, stroke = s)
        val spineW = w * 0.055f
        val spineX = if (spineLeft[i]) x else x + bw - spineW
        drawRoundRectCompat(c, Offset(spineX, y), Size(spineW, bh), r * 0.6f)
        val cutX = if (spineLeft[i]) x + bw - w * 0.05f else x + w * 0.05f
        line(Offset(cutX, y + bh * 0.30f), Offset(cutX, y + bh * 0.70f), c, s * 0.6f)
    }
}

/** Klassische Stapel-Variante — bewusst unverändert für die „BUCH!"-Abschlussanimation. */
private fun DrawScope.drawBooksClassic(c: Color) {
    val w = size.width; val h = size.height; val s = sw() * 0.8f
    val bh = h * 0.17f
    val books = listOf(
        Triple(w * 0.18f, h * 0.60f, w * 0.62f),
        Triple(w * 0.24f, h * 0.40f, w * 0.54f),
        Triple(w * 0.16f, h * 0.20f, w * 0.58f)
    )
    books.forEach { (x, y, bw) ->
        drawRoundRectCompat(c, Offset(x, y), Size(bw, bh), w * 0.03f, stroke = s)
        line(
            Offset(x + bw - w * 0.06f, y + bh * 0.20f),
            Offset(x + bw - w * 0.06f, y + bh * 0.80f),
            c, s * 0.7f
        )
    }
}

private fun DrawScope.drawScroll(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    line(Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.78f), c, s)
    // oben
    val up = Path().apply {
        moveTo(w * 0.36f, h * 0.36f); lineTo(w * 0.5f, h * 0.20f); lineTo(w * 0.64f, h * 0.36f)
    }
    strokePath(up, c, s)
    // unten
    val down = Path().apply {
        moveTo(w * 0.36f, h * 0.64f); lineTo(w * 0.5f, h * 0.80f); lineTo(w * 0.64f, h * 0.64f)
    }
    strokePath(down, c, s)
}

private fun DrawScope.drawArrowUp(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    line(Offset(w * 0.5f, h * 0.80f), Offset(w * 0.5f, h * 0.26f), c, s)
    val head = Path().apply {
        moveTo(w * 0.30f, h * 0.46f); lineTo(w * 0.5f, h * 0.22f); lineTo(w * 0.70f, h * 0.46f)
    }
    strokePath(head, c, s)
}

private fun DrawScope.drawHourglass(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    val cx = w * 0.5f
    val topY = h * 0.15f; val botY = h * 0.85f
    val xl = w * 0.30f; val xr = w * 0.70f
    val neckY = h * 0.50f
    // Rahmenplatten oben/unten — breiter als das Glas, kräftig & abgerundet
    line(Offset(w * 0.22f, topY), Offset(w * 0.78f, topY), c, s * 1.4f)
    line(Offset(w * 0.22f, botY), Offset(w * 0.78f, botY), c, s * 1.4f)
    // schlanke Seitenstreben — vervollständigen die klassische Sanduhr-Silhouette
    line(Offset(w * 0.265f, topY), Offset(w * 0.265f, botY), c, s * 0.5f)
    line(Offset(w * 0.735f, topY), Offset(w * 0.735f, botY), c, s * 0.5f)
    // Glas: zwei an der Taille zusammenlaufende Kammern
    val glass = Path().apply {
        moveTo(xl, topY); lineTo(xr, topY)
        lineTo(cx, neckY)
        lineTo(xr, botY); lineTo(xl, botY)
        lineTo(cx, neckY)
        close()
    }
    strokePath(glass, c, s * 0.85f)
    // Sand oben — an die Glaswände angepasster Trichter
    val sandTop = Path().apply {
        moveTo(w * 0.385f, h * 0.30f)
        lineTo(w * 0.615f, h * 0.30f)
        lineTo(cx, neckY - h * 0.01f)
        close()
    }
    drawPath(sandTop, c)
    // rieselnder Sand durch die Taille
    line(Offset(cx, neckY - h * 0.02f), Offset(cx, h * 0.70f), c, s * 0.45f)
    // Sand unten — abgerundeter Haufen
    val sandBot = Path().apply {
        moveTo(w * 0.37f, botY - h * 0.02f)
        cubicTo(w * 0.42f, h * 0.69f, w * 0.58f, h * 0.69f, w * 0.63f, botY - h * 0.02f)
        close()
    }
    drawPath(sandBot, c)
}

private fun DrawScope.drawTrophy(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    // Pokal-Schale
    val cup = Path().apply {
        moveTo(w * 0.32f, h * 0.20f)
        lineTo(w * 0.68f, h * 0.20f)
        lineTo(w * 0.63f, h * 0.50f)
        lineTo(w * 0.37f, h * 0.50f)
        close()
    }
    strokePath(cup, c, s)
    // Henkel
    val lh = Path().apply { moveTo(w * 0.32f, h * 0.24f); cubicTo(w * 0.16f, h * 0.26f, w * 0.18f, h * 0.44f, w * 0.34f, h * 0.42f) }
    val rh = Path().apply { moveTo(w * 0.68f, h * 0.24f); cubicTo(w * 0.84f, h * 0.26f, w * 0.82f, h * 0.44f, w * 0.66f, h * 0.42f) }
    strokePath(lh, c, s * 0.8f); strokePath(rh, c, s * 0.8f)
    // Stiel + Fuß
    line(Offset(w * 0.5f, h * 0.50f), Offset(w * 0.5f, h * 0.68f), c, s)
    line(Offset(w * 0.36f, h * 0.82f), Offset(w * 0.64f, h * 0.82f), c, s)
    line(Offset(w * 0.42f, h * 0.68f), Offset(w * 0.58f, h * 0.68f), c, s)
}

private fun DrawScope.drawHook(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    // Öhr
    drawCircle(c, radius = size.minDimension * 0.07f, center = Offset(w * 0.5f, h * 0.20f),
        style = Stroke(width = s * 0.8f))
    // Schaft + J-Kurve
    val p = Path().apply {
        moveTo(w * 0.5f, h * 0.27f)
        lineTo(w * 0.5f, h * 0.62f)
        cubicTo(w * 0.5f, h * 0.84f, w * 0.26f, h * 0.84f, w * 0.26f, h * 0.62f)
    }
    strokePath(p, c, s)
    // Widerhaken
    line(Offset(w * 0.26f, h * 0.62f), Offset(w * 0.36f, h * 0.52f), c, s * 0.8f)
}

private fun DrawScope.drawHandshake(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    val cy = h * 0.52f
    // zwei Unterarme, die in der Mitte zusammenkommen
    line(Offset(w * 0.08f, h * 0.40f), Offset(w * 0.46f, cy), c, s * 1.8f)
    line(Offset(w * 0.92f, h * 0.40f), Offset(w * 0.54f, cy), c, s * 1.8f)
    // verschränkte Hände (Mitte)
    drawRoundRectCompat(c, Offset(w * 0.38f, cy - h * 0.10f), Size(w * 0.24f, h * 0.20f), w * 0.05f)
    // Finger-Andeutung
    line(Offset(w * 0.46f, cy - h * 0.02f), Offset(w * 0.56f, cy - h * 0.02f), Color.White, s * 0.6f)
}

private fun DrawScope.drawClapper(c: Color) {
    val w = size.width; val h = size.height; val s = sw() * 0.7f
    // Schiefertafel (Umriss)
    drawRoundRectCompat(c, Offset(w * 0.16f, h * 0.46f), Size(w * 0.68f, h * 0.38f), w * 0.05f, stroke = s)
    // Klappe oben — gefülltes, schräg angehobenes Parallelogramm (Scharnier links)
    val stick = Path().apply {
        moveTo(w * 0.155f, h * 0.44f)
        lineTo(w * 0.175f, h * 0.30f)
        lineTo(w * 0.845f, h * 0.22f)
        lineTo(w * 0.845f, h * 0.36f)
        close()
    }
    drawPath(stick, c)
    // Diagonale Streifen — als weiße Aussparungen, auf die Klappe begrenzt
    clipPath(stick) {
        var x = 0.04f
        while (x < 0.95f) {
            drawLine(
                Color.White,
                Offset(w * x, h * 0.48f),
                Offset(w * (x + 0.11f), h * 0.16f),
                strokeWidth = w * 0.05f
            )
            x += 0.145f
        }
    }
}

private fun DrawScope.drawCheckeredFlag(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    // Mast
    line(Offset(w * 0.24f, h * 0.16f), Offset(w * 0.24f, h * 0.86f), c, s)
    // Fahne (Raster 3×2)
    val fx = w * 0.24f; val fy = h * 0.18f; val fw = w * 0.52f; val fh = h * 0.34f
    drawRect(c, topLeft = Offset(fx, fy), size = Size(fw, fh), style = Stroke(width = s * 0.7f))
    val cols = 3; val rows = 2
    val cw = fw / cols; val ch = fh / rows
    for (r in 0 until rows) for (col in 0 until cols) {
        if ((r + col) % 2 == 0) {
            drawRect(c, topLeft = Offset(fx + col * cw, fy + r * ch), size = Size(cw, ch))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
//  Zeichen-Helfer
// ─────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawRoundRectCompat(
    c: Color, topLeft: Offset, size: Size, radius: Float,
    stroke: Float? = null, fillFirst: Boolean = false
) {
    val cr = androidx.compose.ui.geometry.CornerRadius(radius, radius)
    if (stroke == null) {
        drawRoundRect(c, topLeft, size, cr)
    } else {
        if (fillFirst) drawRoundRect(Color.White, topLeft, size, cr)
        drawRoundRect(c, topLeft, size, cr, style = Stroke(width = stroke))
    }
}

private fun DrawScope.drawStar(center: Offset, r: Float, c: Color) {
    val path = Path()
    for (i in 0 until 10) {
        val angle = -90.0 + i * 36.0
        val rad = Math.toRadians(angle)
        val rr = if (i % 2 == 0) r else r * 0.45f
        val x = center.x + cos(rad).toFloat() * rr
        val y = center.y + sin(rad).toFloat() * rr
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, c)
}

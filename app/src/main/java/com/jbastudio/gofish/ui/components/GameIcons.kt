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
import com.jbastudio.gofish.ui.theme.*
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
    DECK, CARD, BOOKS, BOOKS_CLASSIC, SCROLL, ARROW_UP, ARROW_LEFT, HOURGLASS, TROPHY, HOOK, HANDSHAKE,
    CLAPPER, FLAG_CHECKERED, SPEAKER, SPEAKER_MUTED
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
    // Standard-Tönung (DeepSea) ⇒ farbig zeichnen. Wird das Icon mit einer
    // anderen Farbe getönt (z. B. Foam auf dunklem Button, accentDeep im
    // GameOver-Dialog), bleibt es einfarbig in genau dieser Farbe.
    val colored = c == DeepSea
    when (kind) {
        GameIconKind.GLOBE          -> drawGlobe(c, colored)
        GameIconKind.HOME           -> drawHome(c, colored)
        GameIconKind.ROD            -> drawRod(c, colored)
        GameIconKind.WAVE           -> drawWave(c, colored)
        GameIconKind.FISH           -> drawFishIcon(c, colored)
        GameIconKind.SATELLITE      -> drawSatellite(c, colored)
        GameIconKind.HAND           -> drawHand(c)
        GameIconKind.SEARCH         -> drawSearch(c, colored)
        GameIconKind.PENCIL         -> drawPencil(c, colored)
        GameIconKind.CHECK          -> drawCheck(c)
        GameIconKind.CLOSE          -> drawClose(c)
        GameIconKind.DECK           -> drawDeck(c, colored)
        GameIconKind.CARD           -> drawCard(c, colored)
        GameIconKind.BOOKS          -> drawBooks(c, colored)
        GameIconKind.BOOKS_CLASSIC  -> drawBooksClassic(c)
        GameIconKind.SCROLL         -> drawScroll(c)
        GameIconKind.ARROW_UP       -> drawArrowUp(c)
        GameIconKind.ARROW_LEFT     -> drawArrowLeft(c)
        GameIconKind.HOURGLASS      -> drawHourglass(c, colored)
        GameIconKind.TROPHY         -> drawTrophy(c, colored)
        GameIconKind.HOOK           -> drawHook(c, colored)
        GameIconKind.HANDSHAKE      -> drawHandshake(c, colored)
        GameIconKind.CLAPPER        -> drawClapper(c, colored)
        GameIconKind.FLAG_CHECKERED -> drawCheckeredFlag(c, colored)
        GameIconKind.SPEAKER        -> drawSpeaker(c, colored, muted = false)
        GameIconKind.SPEAKER_MUTED  -> drawSpeaker(c, colored, muted = true)
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

private fun DrawScope.drawGlobe(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val cx = w / 2f; val cy = h / 2f
    val r = size.minDimension * 0.40f
    val s = sw()
    // Kugel
    if (colored) drawCircle(OceanMid, radius = r, center = Offset(cx, cy))
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

private fun DrawScope.drawHome(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    // Farbige Füllung: Wände sandfarben, Dach koralle
    if (colored) {
        val walls = Path().apply {
            moveTo(w * 0.22f, h * 0.46f)
            lineTo(w * 0.22f, h * 0.84f)
            lineTo(w * 0.78f, h * 0.84f)
            lineTo(w * 0.78f, h * 0.46f)
            close()
        }
        drawPath(walls, SandyBeige)
        val roofFill = Path().apply {
            moveTo(w * 0.12f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.16f)
            lineTo(w * 0.88f, h * 0.50f)
            close()
        }
        drawPath(roofFill, CoralDeep)
    }
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

private fun DrawScope.drawRod(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    // Rute (diagonal)
    line(Offset(w * 0.18f, h * 0.86f), Offset(w * 0.80f, h * 0.16f), c, s)
    // Griff-Markierung (farbig hervorgehoben)
    line(Offset(w * 0.18f, h * 0.86f), Offset(w * 0.30f, h * 0.72f), if (colored) SunDeep else c, s * 1.5f)
    // Schnur + Haken
    val lineEnd = Offset(w * 0.80f, h * 0.16f)
    line(lineEnd, Offset(w * 0.80f, h * 0.50f), c, s * 0.6f)
    val hook = Path().apply {
        moveTo(w * 0.80f, h * 0.50f)
        cubicTo(w * 0.80f, h * 0.66f, w * 0.66f, h * 0.66f, w * 0.66f, h * 0.54f)
    }
    strokePath(hook, c, s * 0.7f)
}

private fun DrawScope.drawWave(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    val col = if (colored) OceanDeep else c
    listOf(0.40f, 0.66f).forEach { yf ->
        val p = Path().apply {
            moveTo(w * 0.10f, h * yf)
            cubicTo(w * 0.27f, h * (yf - 0.16f), w * 0.40f, h * (yf + 0.16f), w * 0.55f, h * yf)
            cubicTo(w * 0.70f, h * (yf - 0.16f), w * 0.83f, h * (yf + 0.16f), w * 0.92f, h * yf)
        }
        strokePath(p, col, s)
    }
}

private fun DrawScope.drawFishIcon(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val cy = h / 2f
    val bodyCol = if (colored) SeafoamGreen else c
    val finCol  = if (colored) SeafoamDeep else c
    // Körper
    drawOval(bodyCol, topLeft = Offset(w * 0.18f, h * 0.30f), size = Size(w * 0.52f, h * 0.40f))
    // Schwanz
    val tail = Path().apply {
        moveTo(w * 0.70f, cy)
        lineTo(w * 0.90f, h * 0.30f)
        lineTo(w * 0.90f, h * 0.70f)
        close()
    }
    drawPath(tail, finCol)
    // Auge
    drawCircle(Color.White, radius = size.minDimension * 0.06f, center = Offset(w * 0.32f, h * 0.45f))
    if (colored) drawCircle(DeepSea, radius = size.minDimension * 0.03f, center = Offset(w * 0.32f, h * 0.45f))
}

private fun DrawScope.drawSatellite(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw(); val cx = w / 2f; val cy = h / 2f
    // Korpus
    drawRect(c, topLeft = Offset(cx - w * 0.10f, cy - h * 0.16f), size = Size(w * 0.20f, h * 0.32f))
    // Solarpaneele (farbige Füllung)
    if (colored) {
        drawRect(OceanDeep, topLeft = Offset(w * 0.05f, cy - h * 0.10f), size = Size(w * 0.28f, h * 0.20f))
        drawRect(OceanDeep, topLeft = Offset(w * 0.67f, cy - h * 0.10f), size = Size(w * 0.28f, h * 0.20f))
    }
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
    val palmLeft = w * 0.31f
    val palmTop  = h * 0.44f
    val palmW    = w * 0.42f
    val palmH    = h * 0.40f
    // Handfläche — abgerundeter Block, frontal (flache „Stopp"-Hand)
    drawRoundRectCompat(c, Offset(palmLeft, palmTop), Size(palmW, palmH), w * 0.15f)
    // Vier Finger dicht beieinander, leichte Längenstaffelung (Mittelfinger am längsten).
    // Die Unterkanten ragen in die Handfläche, sodass alles verbunden wirkt.
    val fw  = w * 0.092f
    val gap = w * 0.017f
    val startX = palmLeft + w * 0.013f
    val fingerBottom = palmTop + palmH * 0.30f
    val tipY = floatArrayOf(0.24f, 0.18f, 0.21f, 0.27f)   // Zeige-, Mittel-, Ring-, kleiner Finger
    for (i in 0..3) {
        val x = startX + i * (fw + gap)
        drawRoundRectCompat(c, Offset(x, h * tipY[i]), Size(fw, fingerBottom - h * tipY[i]), fw * 0.5f)
    }
    // Daumen — kurzer, seitlich abgespreizter Stummel links am Handballen,
    // klar unterhalb der Fingerspitzen (kein nach oben gestreckter „Arm").
    line(
        Offset(palmLeft + w * 0.06f, palmTop + palmH * 0.50f),
        Offset(palmLeft - w * 0.12f, palmTop + palmH * 0.22f),
        c, w * 0.135f
    )
}

private fun DrawScope.drawSearch(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    val r = size.minDimension * 0.26f
    if (colored) drawCircle(OceanMid, radius = r - s * 0.5f, center = Offset(w * 0.42f, h * 0.42f))
    drawCircle(c, radius = r, center = Offset(w * 0.42f, h * 0.42f), style = Stroke(width = s))
    line(Offset(w * 0.61f, h * 0.61f), Offset(w * 0.82f, h * 0.82f), c, s * 1.2f)
}

private fun DrawScope.drawPencil(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    // Körper
    val body = Path().apply {
        moveTo(w * 0.28f, h * 0.78f)
        lineTo(w * 0.70f, h * 0.36f)
        lineTo(w * 0.84f, h * 0.50f)
        lineTo(w * 0.42f, h * 0.92f)
        close()
    }
    if (colored) drawPath(body, SunDeep)
    strokePath(body, c, s)

    // Angespitztes Ende: heller Holzkegel (passt zum Schaft) mit dunkler Mine.
    val baseTop = Offset(w * 0.28f, h * 0.78f)   // Schaftende oben
    val baseBot = Offset(w * 0.42f, h * 0.92f)   // Schaftende unten
    val point   = Offset(w * 0.215f, h * 0.965f) // Bleistiftspitze (unten links)

    val wood = Path().apply {
        moveTo(baseTop.x, baseTop.y)
        lineTo(baseBot.x, baseBot.y)
        lineTo(point.x, point.y)
        close()
    }
    drawPath(wood, if (colored) SandyBeige else c)
    if (colored) strokePath(wood, c, s)

    // Mine (Graphit) — klar erkennbares dunkles Dreieck an der Spitze
    val f = 0.58f
    val leadTop = Offset(baseTop.x + (point.x - baseTop.x) * f, baseTop.y + (point.y - baseTop.y) * f)
    val leadBot = Offset(baseBot.x + (point.x - baseBot.x) * f, baseBot.y + (point.y - baseBot.y) * f)
    val lead = Path().apply {
        moveTo(leadTop.x, leadTop.y)
        lineTo(leadBot.x, leadBot.y)
        lineTo(point.x, point.y)
        close()
    }
    drawPath(lead, if (colored) DeepSea else c)
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

private fun DrawScope.drawDeck(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw() * 0.8f
    // hintere Karte (farbiger Rücken)
    if (colored) drawRoundRectCompat(OceanMid, Offset(w * 0.22f, h * 0.18f), Size(w * 0.46f, h * 0.58f), w * 0.06f)
    drawRoundRectCompat(c, Offset(w * 0.22f, h * 0.18f), Size(w * 0.46f, h * 0.58f), w * 0.06f,
        stroke = s)
    // vordere Karte
    drawRoundRectCompat(c, Offset(w * 0.34f, h * 0.30f), Size(w * 0.46f, h * 0.58f), w * 0.06f,
        stroke = s, fillFirst = true)
    // Symbol
    drawCircle(if (colored) SuitRed else c, radius = size.minDimension * 0.07f, center = Offset(w * 0.57f, h * 0.59f))
}

private fun DrawScope.drawCard(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw() * 0.8f
    if (colored) drawRoundRectCompat(Foam, Offset(w * 0.28f, h * 0.18f), Size(w * 0.44f, h * 0.64f), w * 0.07f)
    drawRoundRectCompat(c, Offset(w * 0.28f, h * 0.18f), Size(w * 0.44f, h * 0.64f), w * 0.07f,
        stroke = s)
    // kleiner Stern in der Mitte
    drawStar(Offset(w * 0.50f, h * 0.50f), size.minDimension * 0.13f, if (colored) SunDeep else c)
}

private fun DrawScope.drawBooks(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw() * 0.8f
    val bh = h * 0.185f
    val r  = w * 0.035f
    // Drei liegende Bücher, abwechselnd versetzt — je mit gefülltem Buchrücken
    // und einem angedeuteten Seitenschnitt am gegenüberliegenden Ende.
    val xs        = floatArrayOf(0.19f, 0.27f, 0.17f)
    val ys        = floatArrayOf(0.585f, 0.385f, 0.185f)
    val ws        = floatArrayOf(0.60f, 0.52f, 0.56f)
    val spineLeft = booleanArrayOf(true, false, true)
    val covers    = arrayOf(CoralDeep, SeafoamDeep, SunDeep)
    for (i in 0..2) {
        val x = w * xs[i]; val y = h * ys[i]; val bw = w * ws[i]
        if (colored) drawRoundRectCompat(covers[i], Offset(x, y), Size(bw, bh), r)
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

/** Zurück-Pfeil (zeigt nach links) — neutral gehalten für gute Lesbarkeit. */
private fun DrawScope.drawArrowLeft(c: Color) {
    val w = size.width; val h = size.height; val s = sw()
    line(Offset(w * 0.80f, h * 0.5f), Offset(w * 0.26f, h * 0.5f), c, s)
    val head = Path().apply {
        moveTo(w * 0.46f, h * 0.30f); lineTo(w * 0.22f, h * 0.5f); lineTo(w * 0.46f, h * 0.70f)
    }
    strokePath(head, c, s)
}

private fun DrawScope.drawHourglass(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    val sand = if (colored) SunDeep else c
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
    drawPath(sandTop, sand)
    // rieselnder Sand durch die Taille
    line(Offset(cx, neckY - h * 0.02f), Offset(cx, h * 0.70f), sand, s * 0.45f)
    // Sand unten — abgerundeter Haufen
    val sandBot = Path().apply {
        moveTo(w * 0.37f, botY - h * 0.02f)
        cubicTo(w * 0.42f, h * 0.69f, w * 0.58f, h * 0.69f, w * 0.63f, botY - h * 0.02f)
        close()
    }
    drawPath(sandBot, sand)
}

private fun DrawScope.drawTrophy(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    // Pokal-Schale
    val cup = Path().apply {
        moveTo(w * 0.32f, h * 0.20f)
        lineTo(w * 0.68f, h * 0.20f)
        lineTo(w * 0.63f, h * 0.50f)
        lineTo(w * 0.37f, h * 0.50f)
        close()
    }
    if (colored) drawPath(cup, SunDeep)
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

private fun DrawScope.drawHook(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    val accent = if (colored) SunDeep else c
    // Öhr
    drawCircle(accent, radius = size.minDimension * 0.07f, center = Offset(w * 0.5f, h * 0.20f),
        style = Stroke(width = s * 0.8f))
    // Schaft + J-Kurve
    val p = Path().apply {
        moveTo(w * 0.5f, h * 0.27f)
        lineTo(w * 0.5f, h * 0.62f)
        cubicTo(w * 0.5f, h * 0.84f, w * 0.26f, h * 0.84f, w * 0.26f, h * 0.62f)
    }
    strokePath(p, c, s)
    // Widerhaken
    line(Offset(w * 0.26f, h * 0.62f), Offset(w * 0.36f, h * 0.52f), accent, s * 0.8f)
}

private fun DrawScope.drawHandshake(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    val cy = h * 0.52f
    val leftSleeve  = if (colored) CoralDeep else c
    val rightSleeve = if (colored) SeafoamDeep else c
    val hands       = if (colored) SandyBeige else c
    // zwei Unterarme, die in der Mitte zusammenkommen
    line(Offset(w * 0.08f, h * 0.40f), Offset(w * 0.46f, cy), leftSleeve, s * 1.8f)
    line(Offset(w * 0.92f, h * 0.40f), Offset(w * 0.54f, cy), rightSleeve, s * 1.8f)
    // verschränkte Hände (Mitte)
    drawRoundRectCompat(hands, Offset(w * 0.38f, cy - h * 0.10f), Size(w * 0.24f, h * 0.20f), w * 0.05f)
    // Finger-Andeutung
    line(Offset(w * 0.46f, cy - h * 0.02f), Offset(w * 0.56f, cy - h * 0.02f),
        if (colored) DeepSea.copy(alpha = 0.5f) else Color.White, s * 0.6f)
}

private fun DrawScope.drawClapper(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw() * 0.7f
    // Schiefertafel (farbige Füllung + Umriss)
    if (colored) drawRoundRectCompat(OceanDeep, Offset(w * 0.16f, h * 0.46f), Size(w * 0.68f, h * 0.38f), w * 0.05f)
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

private fun DrawScope.drawCheckeredFlag(c: Color, colored: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    // Mast (farbig hervorgehoben)
    line(Offset(w * 0.24f, h * 0.16f), Offset(w * 0.24f, h * 0.86f), if (colored) SunDeep else c, s)
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

/**
 * Klassisches Lautsprecher-Symbol. Ohne [muted] mit zwei nach rechts
 * abstrahlenden Schallwellen, mit [muted] stattdessen einem deutlichen Kreuz
 * (Stumm).
 */
private fun DrawScope.drawSpeaker(c: Color, colored: Boolean, muted: Boolean) {
    val w = size.width; val h = size.height; val s = sw()
    val cy = h * 0.5f
    // Korpus: Box + Konus als eine gefüllte Silhouette
    val body = Path().apply {
        moveTo(w * 0.44f, h * 0.22f)
        lineTo(w * 0.26f, h * 0.40f)
        lineTo(w * 0.12f, h * 0.40f)
        lineTo(w * 0.12f, h * 0.60f)
        lineTo(w * 0.26f, h * 0.60f)
        lineTo(w * 0.44f, h * 0.78f)
        close()
    }
    drawPath(body, c)
    if (muted) {
        // Stumm-Kreuz rechts neben dem Lautsprecher (rötlich, wenn farbig)
        val x0 = w * 0.56f; val x1 = w * 0.82f
        val yt = cy - h * 0.13f; val yb = cy + h * 0.13f
        val cross = if (colored) CoralDeep else c
        line(Offset(x0, yt), Offset(x1, yb), cross, s * 1.15f)
        line(Offset(x1, yt), Offset(x0, yb), cross, s * 1.15f)
    } else {
        // Zwei Schallwellen (Kreisbögen), zentriert an der Konusöffnung
        val waveCol = if (colored) SeafoamDeep else c
        listOf(0.15f, 0.25f).forEach { rf ->
            val r = size.minDimension * rf
            drawArc(
                color = waveCol,
                startAngle = -50f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(w * 0.44f - r, cy - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = s * 0.9f, cap = StrokeCap.Round)
            )
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

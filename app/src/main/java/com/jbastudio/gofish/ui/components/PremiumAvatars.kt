package com.jbastudio.gofish.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath

// ─────────────────────────────────────────────────────────────────────────
//  Premium-Avatare — hochdetaillierte Meeresbewohner mit FESTER Farbe
//  (Pay-/Ad-Wall). Im 4:3-Raum gezeichnet, Blick nach rechts. Facing-Spiegelung
//  und Bob-Animation werden vom Aufrufer (drawAvatar) gewrappt. Die Geometrie ist
//  aus der visuell verifizierten Canvas-Vorschau 1:1 portiert (drawscope-Primitive).
// ─────────────────────────────────────────────────────────────────────────

/** MOBY — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawMobyShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.22f * w, 0.46f * h); cubicTo(0.12f * w, 0.4f * h, 0.045f * w, 0.3f * h, 0.005f * w, 0.235f * h); cubicTo(0.035f * w, 0.35f * h, 0.1f * w, 0.45f * h, 0.16f * w, 0.5f * h); cubicTo(0.1f * w, 0.55f * h, 0.04f * w, 0.64f * h, 0.01f * w, 0.72f * h); cubicTo(0.07f * w, 0.63f * h, 0.15f * w, 0.55f * h, 0.23f * w, 0.52f * h); close() }
    drawPath(p0, Color(0xFFB7C6D4L))
    drawPath(p0, Color(0x80A6B8C7L), style = Stroke(width = 0.01f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p1 = Path().apply { moveTo(0.2f * w, 0.4f * h); cubicTo(0.3f * w, 0.27f * h, 0.46f * w, 0.18f * h, 0.62f * w, 0.155f * h); cubicTo(0.76f * w, 0.135f * h, 0.88f * w, 0.155f * h, 0.945f * w, 0.225f * h); cubicTo(0.985f * w, 0.295f * h, 0.99f * w, 0.45f * h, 0.955f * w, 0.55f * h); cubicTo(0.925f * w, 0.61f * h, 0.85f * w, 0.625f * h, 0.78f * w, 0.625f * h); cubicTo(0.58f * w, 0.63f * h, 0.4f * w, 0.7f * h, 0.27f * w, 0.655f * h); cubicTo(0.2f * w, 0.625f * h, 0.165f * w, 0.535f * h, 0.2f * w, 0.4f * h); close() }
    drawPath(p1, Brush.verticalGradient(0f to Color(0xFFB7C6D4L), 0.5f to Color(0xFFEEF2F6L), 1f to Color(0xFFEEF2F6L), startY = 0.1f * h, endY = 0.64f * h))
    val p2 = Path().apply { moveTo(0.3f * w, 0.595f * h); cubicTo(0.46f * w, 0.675f * h, 0.64f * w, 0.635f * h, 0.78f * w, 0.62f * h); cubicTo(0.86f * w, 0.61f * h, 0.915f * w, 0.575f * h, 0.95f * w, 0.52f * h); cubicTo(0.9f * w, 0.615f * h, 0.8f * w, 0.635f * h, 0.68f * w, 0.632f * h); cubicTo(0.5f * w, 0.628f * h, 0.4f * w, 0.655f * h, 0.3f * w, 0.595f * h); close() }
    drawPath(p2, Color(0xEBFFFFFFL))
    val p3 = Path().apply { moveTo(0.66f * w, 0.59f * h); cubicTo(0.76f * w, 0.598f * h, 0.86f * w, 0.59f * h, 0.93f * w, 0.55f * h); cubicTo(0.945f * w, 0.585f * h, 0.945f * w, 0.615f * h, 0.915f * w, 0.635f * h); cubicTo(0.84f * w, 0.66f * h, 0.74f * w, 0.66f * h, 0.66f * w, 0.64f * h); cubicTo(0.63f * w, 0.62f * h, 0.63f * w, 0.6f * h, 0.66f * w, 0.59f * h); close() }
    drawPath(p3, Color(0x808FA2B4L))
    val p4 = Path().apply { moveTo(0.2f * w, 0.42f * h); cubicTo(0.26f * w, 0.36f * h, 0.34f * w, 0.33f * h, 0.42f * w, 0.32f * h); cubicTo(0.33f * w, 0.41f * h, 0.27f * w, 0.51f * h, 0.245f * w, 0.6f * h); cubicTo(0.2f * w, 0.53f * h, 0.18f * w, 0.47f * h, 0.2f * w, 0.42f * h); close() }
    drawPath(p4, Color(0x388FA2B4L))
    drawCircle(Brush.radialGradient(0f to Color(0x99FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.58f * w, 0.25f * h), radius = 0.4f * w), radius = 0.4f * w, center = Offset(0.58f * w, 0.25f * h))
    drawPath(p1, Color(0x99A6B8C7L), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p5 = Path().apply { moveTo(0.34f * w, 0.24f * h); cubicTo(0.54f * w, 0.155f * h, 0.76f * w, 0.15f * h, 0.92f * w, 0.235f * h) }
    drawPath(p5, Color(0xD9FFFFFFL), style = Stroke(width = 0.018f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p6 = Path().apply { moveTo(0.58f * w, 0.3f * h); cubicTo(0.65f * w, 0.34f * h, 0.72f * w, 0.33f * h, 0.8f * w, 0.39f * h) }
    drawPath(p6, Color(0xE6CBD6E0L), style = Stroke(width = 0.01f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p7 = Path().apply { moveTo(0.74f * w, 0.27f * h); lineTo(0.82f * w, 0.31f * h) }
    drawPath(p7, Color(0xCCCBD6E0L), style = Stroke(width = 0.008f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x801F3D52L), radius = 0.015f * w, center = Offset(0.86f * w, 0.205f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.072f * w, center = Offset(0.81f * w, 0.5f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.04464f * w, center = Offset(0.816f * w, 0.508f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.0216f * w, center = Offset(0.792f * w, 0.48f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.01008f * w, center = Offset(0.828f * w, 0.52f * h))
    drawCircle(Color(0x6BFF8FA3L), radius = 0.04f * w, center = Offset(0.72f * w, 0.57f * h))
    val p8 = Path().apply { moveTo(0.855f * w, 0.59f * h); cubicTo(0.895f * w, 0.628f * h, 0.935f * w, 0.618f * h, 0.95f * w, 0.57f * h) }
    drawPath(p8, Color(0xFF1F3D52L), style = Stroke(width = 0.016f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** NESSIE — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawNessieShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.02f * w, 0.74f * h); cubicTo(0.04f * w, 0.58f * h, 0.18f * w, 0.56f * h, 0.22f * w, 0.74f * h); close() }
    drawPath(p0, Brush.verticalGradient(0f to Color(0xFF274C33L), 1f to Color(0xFF3E7350L), startY = 0.54f * h, endY = 0.74f * h))
    val p1 = Path().apply { moveTo(0.2f * w, 0.74f * h); cubicTo(0.22f * w, 0.5f * h, 0.46f * w, 0.48f * h, 0.5f * w, 0.74f * h); close() }
    drawPath(p1, Brush.verticalGradient(0f to Color(0xFF274C33L), 1f to Color(0xFF3E7350L), startY = 0.46f * h, endY = 0.74f * h))
    val p2 = Path().apply { moveTo(0.2f * w, 0.74f * h); cubicTo(0.28f * w, 0.66f * h, 0.42f * w, 0.66f * h, 0.5f * w, 0.74f * h); close() }
    drawPath(p2, Color(0x2E1C3A26L))
    drawCircle(Brush.radialGradient(0f to Color(0x66FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.33f * w, 0.55f * h), radius = 0.12f * w), radius = 0.12f * w, center = Offset(0.33f * w, 0.55f * h))
    drawCircle(Brush.radialGradient(0f to Color(0x4DFFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.11f * w, 0.61f * h), radius = 0.06f * w), radius = 0.06f * w, center = Offset(0.11f * w, 0.61f * h))
    val p3 = Path().apply { moveTo(0.46f * w, 0.74f * h); cubicTo(0.4f * w, 0.56f * h, 0.46f * w, 0.4f * h, 0.58f * w, 0.3f * h); cubicTo(0.66f * w, 0.23f * h, 0.7f * w, 0.16f * h, 0.7f * w, 0.13f * h); cubicTo(0.74f * w, 0.09f * h, 0.86f * w, 0.09f * h, 0.9f * w, 0.15f * h); cubicTo(0.84f * w, 0.2f * h, 0.8f * w, 0.25f * h, 0.78f * w, 0.32f * h); cubicTo(0.74f * w, 0.44f * h, 0.74f * w, 0.58f * h, 0.72f * w, 0.74f * h); close() }
    drawPath(p3, Brush.verticalGradient(0f to Color(0xFF3E7350L), 0.6f to Color(0xFF3E7350L), 1f to Color(0xFF274C33L), startY = 0.1f * h, endY = 0.74f * h))
    val p4 = Path().apply { moveTo(0.72f * w, 0.74f * h); cubicTo(0.73f * w, 0.56f * h, 0.72f * w, 0.42f * h, 0.76f * w, 0.3f * h); cubicTo(0.79f * w, 0.23f * h, 0.83f * w, 0.19f * h, 0.88f * w, 0.16f * h); cubicTo(0.86f * w, 0.24f * h, 0.82f * w, 0.36f * h, 0.8f * w, 0.5f * h); cubicTo(0.79f * w, 0.6f * h, 0.78f * w, 0.68f * h, 0.78f * w, 0.74f * h); close() }
    drawPath(p4, Color(0xE68FC59EL))
    val p5 = Path().apply { moveTo(0.47f * w, 0.74f * h); cubicTo(0.44f * w, 0.56f * h, 0.49f * w, 0.42f * h, 0.59f * w, 0.32f * h); cubicTo(0.55f * w, 0.44f * h, 0.54f * w, 0.58f * h, 0.55f * w, 0.74f * h); close() }
    drawPath(p5, Color(0x4D1C3A26L))
    val p6 = Path().apply { moveTo(0.89f * w, 0.17f * h); cubicTo(0.83f * w, 0.24f * h, 0.8f * w, 0.38f * h, 0.79f * w, 0.54f * h) }
    drawPath(p6, Color(0x8CBFE8CCL), style = Stroke(width = 0.022f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Brush.radialGradient(0f to Color(0x52FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.66f * w, 0.26f * h), radius = 0.16f * w), radius = 0.16f * w, center = Offset(0.66f * w, 0.26f * h))
    val p7 = Path().apply { moveTo(0.7f * w, 0.155f * h); cubicTo(0.7f * w, 0.07f * h, 0.84f * w, 0.05f * h, 0.92f * w, 0.1f * h); cubicTo(0.99f * w, 0.14f * h, 0.97f * w, 0.22f * h, 0.9f * w, 0.235f * h); cubicTo(0.82f * w, 0.25f * h, 0.72f * w, 0.23f * h, 0.7f * w, 0.155f * h); close() }
    drawPath(p7, Brush.verticalGradient(0f to Color(0xFF3E7350L), 1f to Color(0xFF274C33L), startY = 0.05f * h, endY = 0.25f * h))
    val p8 = Path().apply { moveTo(0.86f * w, 0.215f * h); cubicTo(0.92f * w, 0.235f * h, 0.97f * w, 0.21f * h, 0.965f * w, 0.17f * h); cubicTo(0.94f * w, 0.2f * h, 0.9f * w, 0.205f * h, 0.86f * w, 0.215f * h); close() }
    drawPath(p8, Color(0xD98FC59EL))
    drawCircle(Brush.radialGradient(0f to Color(0x73FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.82f * w, 0.11f * h), radius = 0.1f * w), radius = 0.1f * w, center = Offset(0.82f * w, 0.11f * h))
    val p9 = Path().apply { moveTo(0.74f * w, 0.075f * h); cubicTo(0.73f * w, 0.04f * h, 0.78f * w, 0.035f * h, 0.785f * w, 0.07f * h); close() }
    drawPath(p9, Color(0xFF274C33L))
    val p10 = Path().apply { moveTo(0.8f * w, 0.06f * h); cubicTo(0.795f * w, 0.025f * h, 0.85f * w, 0.025f * h, 0.85f * w, 0.06f * h); close() }
    drawPath(p10, Color(0xFF274C33L))
    val p11 = Path().apply { moveTo(0f * w, 0.74f * h); lineTo(1f * w, 0.74f * h); lineTo(1f * w, 0.76f * h); lineTo(0f * w, 0.76f * h); close() }
    drawPath(p11, Color(0x1A1C3A26L))
    val p12 = Path().apply { moveTo(0.4f * w, 0.74f * h); quadraticBezierTo(0.59f * w, 0.715f * h, 0.78f * w, 0.74f * h) }
    drawPath(p12, Color(0x73FFFFFFL), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p13 = Path().apply { moveTo(0.16f * w, 0.74f * h); quadraticBezierTo(0.35f * w, 0.718f * h, 0.54f * w, 0.74f * h) }
    drawPath(p13, Color(0x66FFFFFFL), style = Stroke(width = 0.011f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p14 = Path().apply { moveTo(0f * w, 0.75f * h); quadraticBezierTo(0.12f * w, 0.728f * h, 0.24f * w, 0.75f * h) }
    drawPath(p14, Color(0x52FFFFFFL), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p15 = Path().apply { moveTo(0.62f * w, 0.78f * h); quadraticBezierTo(0.8f * w, 0.755f * h, 0.98f * w, 0.78f * h) }
    drawPath(p15, Color(0x47FFFFFFL), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.072f * w, center = Offset(0.83f * w, 0.135f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.046f * w, center = Offset(0.838f * w, 0.141f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.02f * w, center = Offset(0.814f * w, 0.117f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.009f * w, center = Offset(0.846f * w, 0.153f * h))
    val p16 = Path().apply { moveTo(0.9f * w, 0.175f * h); cubicTo(0.925f * w, 0.205f * h, 0.955f * w, 0.2f * h, 0.965f * w, 0.175f * h) }
    drawPath(p16, Color(0xFF1F3D52L), style = Stroke(width = 0.016f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x6BFF8FA3L), radius = 0.034f * w, center = Offset(0.9f * w, 0.16f * h))
}

/** BLOBFISH — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawBlobfishShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.3f * w, 0.42f * h); cubicTo(0.14f * w, 0.34f * h, 0.06f * w, 0.4f * h, 0.1f * w, 0.5f * h); cubicTo(0.14f * w, 0.58f * h, 0.24f * w, 0.56f * h, 0.32f * w, 0.52f * h); close() }
    drawPath(p0, Color(0xFFD98794L))
    val p1 = Path().apply { moveTo(0.24f * w, 0.66f * h); cubicTo(0.12f * w, 0.74f * h, 0.1f * w, 0.86f * h, 0.18f * w, 0.9f * h); cubicTo(0.24f * w, 0.88f * h, 0.3f * w, 0.8f * h, 0.34f * w, 0.72f * h); close() }
    drawPath(p1, Color(0xFFD98794L))
    val p2 = Path().apply { moveTo(0.24f * w, 0.4f * h); cubicTo(0.3f * w, 0.18f * h, 0.5f * w, 0.1f * h, 0.66f * w, 0.16f * h); cubicTo(0.82f * w, 0.21f * h, 0.96f * w, 0.3f * h, 0.96f * w, 0.44f * h); cubicTo(0.96f * w, 0.58f * h, 0.9f * w, 0.7f * h, 0.78f * w, 0.8f * h); cubicTo(0.66f * w, 0.9f * h, 0.46f * w, 0.94f * h, 0.34f * w, 0.86f * h); cubicTo(0.22f * w, 0.78f * h, 0.18f * w, 0.62f * h, 0.2f * w, 0.5f * h); cubicTo(0.21f * w, 0.45f * h, 0.22f * w, 0.42f * h, 0.24f * w, 0.4f * h); close() }
    drawPath(p2, Brush.radialGradient(0f to Color(0xFFF8BFC5L), 0.5f to Color(0xFFF2A7AFL), 1f to Color(0xFFD98794L), center = Offset(0.52f * w, 0.24f * h), radius = 0.78f * w))
    drawPath(p2, Brush.verticalGradient(0f to Color(0x00FFFFFFL), 1f to Color(0x47C26F7EL), startY = 0.55f * h, endY = 0.95f * h))
    val p3 = Path().apply { moveTo(0.3f * w, 0.7f * h); cubicTo(0.4f * w, 0.9f * h, 0.66f * w, 0.9f * h, 0.78f * w, 0.74f * h); cubicTo(0.66f * w, 0.8f * h, 0.46f * w, 0.82f * h, 0.3f * w, 0.7f * h); close() }
    drawPath(p3, Color(0xE6F9CBD0L))
    drawCircle(Brush.radialGradient(0f to Color(0x8CFFFFFFL), 0.6f to Color(0x1FFFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.46f * w, 0.22f * h), radius = 0.4f * w), radius = 0.4f * w, center = Offset(0.46f * w, 0.24f * h))
    val p4 = Path().apply { moveTo(0.4f * w, 0.16f * h); cubicTo(0.52f * w, 0.11f * h, 0.62f * w, 0.13f * h, 0.7f * w, 0.18f * h) }
    drawPath(p4, Color(0x80FFFFFFL), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p5 = Path().apply { moveTo(0.7f * w, 0.72f * h); cubicTo(0.78f * w, 0.86f * h, 0.9f * w, 0.84f * h, 0.88f * w, 0.72f * h); cubicTo(0.82f * w, 0.68f * h, 0.75f * w, 0.69f * h, 0.7f * w, 0.72f * h); close() }
    drawPath(p5, Color(0xFFD98794L))
    drawOval(Color(0x2EC26F7EL), topLeft = Offset(0.5f * w, 0.4f * h), size = Size(0.4f * w, 0.34f * h))
    val p6 = Path().apply { moveTo(0.56f * w, 0.46f * h); cubicTo(0.62f * w, 0.4f * h, 0.78f * w, 0.4f * h, 0.84f * w, 0.48f * h); cubicTo(0.9f * w, 0.56f * h, 0.88f * w, 0.66f * h, 0.8f * w, 0.72f * h); cubicTo(0.74f * w, 0.77f * h, 0.64f * w, 0.77f * h, 0.58f * w, 0.72f * h); cubicTo(0.51f * w, 0.66f * h, 0.5f * w, 0.53f * h, 0.56f * w, 0.46f * h); close() }
    drawPath(p6, Brush.radialGradient(0f to Color(0xFFF8BFC5L), 0.55f to Color(0xFFF2A7AFL), 1f to Color(0xFFD98794L), center = Offset(0.66f * w, 0.5f * h), radius = 0.24f * w))
    drawPath(p6, Brush.verticalGradient(0f to Color(0x00FFFFFFL), 1f to Color(0x4DC26F7EL), startY = 0.6f * h, endY = 0.76f * h))
    drawCircle(Color(0x80FFFFFFL), radius = 0.07f * w, center = Offset(0.66f * w, 0.5f * h))
    drawCircle(Color(0xB3FFFFFFL), radius = 0.03f * w, center = Offset(0.62f * w, 0.53f * h))
    drawCircle(Color(0x8CC26F7EL), radius = 0.022f * w, center = Offset(0.69f * w, 0.66f * h))
    drawCircle(Color(0x8CC26F7EL), radius = 0.02f * w, center = Offset(0.77f * w, 0.64f * h))
    val p7 = Path().apply { moveTo(0.5f * w, 0.36f * h); cubicTo(0.58f * w, 0.3f * h, 0.76f * w, 0.3f * h, 0.86f * w, 0.38f * h); cubicTo(0.84f * w, 0.46f * h, 0.78f * w, 0.48f * h, 0.7f * w, 0.47f * h); cubicTo(0.62f * w, 0.46f * h, 0.54f * w, 0.45f * h, 0.5f * w, 0.42f * h); close() }
    drawPath(p7, Color(0xD9D98794L))
    val p8 = Path().apply { moveTo(0.54f * w, 0.45f * h); cubicTo(0.64f * w, 0.49f * h, 0.76f * w, 0.49f * h, 0.82f * w, 0.44f * h) }
    drawPath(p8, Color(0x59C26F7EL), style = Stroke(width = 0.014f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p9 = Path().apply { moveTo(0.84f * w, 0.56f * h); cubicTo(0.88f * w, 0.64f * h, 0.86f * w, 0.72f * h, 0.8f * w, 0.76f * h) }
    drawPath(p9, Color(0x4DC26F7EL), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p10 = Path().apply { moveTo(0.62f * w, 0.8f * h); cubicTo(0.67f * w, 0.74f * h, 0.75f * w, 0.74f * h, 0.8f * w, 0.8f * h) }
    drawPath(p10, Color(0xFF1F3D52L), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.062f * w, center = Offset(0.585f * w, 0.42f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.042f * w, center = Offset(0.589f * w, 0.426f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.018f * w, center = Offset(0.569f * w, 0.402f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.008f * w, center = Offset(0.601f * w, 0.436f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.062f * w, center = Offset(0.745f * w, 0.42f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.042f * w, center = Offset(0.749f * w, 0.426f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.018f * w, center = Offset(0.729f * w, 0.402f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.008f * w, center = Offset(0.761f * w, 0.436f * h))
    drawCircle(Color(0x66E98C99L), radius = 0.045f * w, center = Offset(0.52f * w, 0.52f * h))
    drawCircle(Color(0x66E98C99L), radius = 0.045f * w, center = Offset(0.82f * w, 0.52f * h))
}

/** KOI — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawKoiShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.24f * w, 0.52f * h); cubicTo(0.12f * w, 0.66f * h, 0.02f * w, 0.86f * h, -0.04f * w, 0.98f * h); cubicTo(0.06f * w, 0.92f * h, 0.16f * w, 0.8f * h, 0.26f * w, 0.66f * h); cubicTo(0.24f * w, 0.62f * h, 0.23f * w, 0.57f * h, 0.24f * w, 0.52f * h); close() }
    drawPath(p0, Color(0x8CC8761AL))
    val p1 = Path().apply { moveTo(0.24f * w, 0.48f * h); cubicTo(0.12f * w, 0.34f * h, 0.02f * w, 0.16f * h, -0.04f * w, 0.04f * h); cubicTo(0.06f * w, 0.12f * h, 0.17f * w, 0.24f * h, 0.27f * w, 0.38f * h); cubicTo(0.25f * w, 0.42f * h, 0.24f * w, 0.45f * h, 0.24f * w, 0.48f * h); close() }
    drawPath(p1, Color(0x99C8761AL))
    val p2 = Path().apply { moveTo(0.22f * w, 0.4f * h); cubicTo(0.1f * w, 0.34f * h, 0.02f * w, 0.4f * h, 0f * w, 0.5f * h); cubicTo(0.02f * w, 0.6f * h, 0.1f * w, 0.66f * h, 0.22f * w, 0.6f * h); cubicTo(0.3f * w, 0.56f * h, 0.3f * w, 0.44f * h, 0.22f * w, 0.4f * h); close() }
    drawPath(p2, Color(0xCCDE9024L))
    val p3 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.12f * w, 0.4f * h, 0.05f * w, 0.22f * h, 0f * w, 0.08f * h) }
    drawPath(p3, Color(0x66FFF3CCL), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p4 = Path().apply { moveTo(0.22f * w, 0.52f * h); cubicTo(0.12f * w, 0.62f * h, 0.05f * w, 0.8f * h, 0f * w, 0.94f * h) }
    drawPath(p4, Color(0x66FFF3CCL), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p5 = Path().apply { moveTo(0.4f * w, 0.24f * h); cubicTo(0.46f * w, 0.04f * h, 0.58f * w, -0.02f * h, 0.66f * w, 0.06f * h); cubicTo(0.62f * w, 0.1f * h, 0.58f * w, 0.14f * h, 0.56f * w, 0.22f * h); cubicTo(0.5f * w, 0.18f * h, 0.44f * w, 0.2f * h, 0.4f * w, 0.24f * h); close() }
    drawPath(p5, Color(0xB3C8761AL))
    val p6 = Path().apply { moveTo(0.48f * w, 0.2f * h); cubicTo(0.5f * w, 0.1f * h, 0.54f * w, 0.06f * h, 0.58f * w, 0.04f * h) }
    drawPath(p6, Color(0x73FFF3CCL), style = Stroke(width = 0.01f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p7 = Path().apply { moveTo(0.56f * w, 0.78f * h); cubicTo(0.56f * w, 0.94f * h, 0.64f * w, 1f * h, 0.7f * w, 0.92f * h); cubicTo(0.66f * w, 0.86f * h, 0.62f * w, 0.82f * h, 0.62f * w, 0.76f * h); cubicTo(0.6f * w, 0.76f * h, 0.58f * w, 0.77f * h, 0.56f * w, 0.78f * h); close() }
    drawPath(p7, Color(0x99C8761AL))
    val p8 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.3f * w, 0.2f * h, 0.56f * w, 0.1f * h, 0.78f * w, 0.18f * h); cubicTo(0.94f * w, 0.24f * h, 1f * w, 0.4f * h, 0.99f * w, 0.52f * h); cubicTo(1f * w, 0.64f * h, 0.94f * w, 0.78f * h, 0.78f * w, 0.84f * h); cubicTo(0.56f * w, 0.92f * h, 0.3f * w, 0.82f * h, 0.22f * w, 0.52f * h); cubicTo(0.215f * w, 0.51f * h, 0.215f * w, 0.51f * h, 0.22f * w, 0.5f * h); close() }
    drawPath(p8, Brush.verticalGradient(0f to Color(0xFFDE9024L), 0.42f to Color(0xFFF6B73CL), 1f to Color(0xFFDE9024L), startY = 0.08f * h, endY = 0.86f * h))
    val p9 = Path().apply { moveTo(0.28f * w, 0.66f * h); cubicTo(0.44f * w, 0.9f * h, 0.72f * w, 0.86f * h, 0.92f * w, 0.62f * h); cubicTo(0.74f * w, 0.74f * h, 0.46f * w, 0.78f * h, 0.28f * w, 0.66f * h); close() }
    drawPath(p9, Color(0xD9FFE39AL))
    clipPath(p8) {
        val p10 = Path().apply { moveTo(0.305f * w, 0.36f * h); quadraticBezierTo(0.34f * w, 0.31f * h, 0.375f * w, 0.36f * h) }
        drawPath(p10, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p11 = Path().apply { moveTo(0.405f * w, 0.36f * h); quadraticBezierTo(0.44f * w, 0.31f * h, 0.475f * w, 0.36f * h) }
        drawPath(p11, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p12 = Path().apply { moveTo(0.505f * w, 0.36f * h); quadraticBezierTo(0.54f * w, 0.31f * h, 0.575f * w, 0.36f * h) }
        drawPath(p12, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p13 = Path().apply { moveTo(0.605f * w, 0.36f * h); quadraticBezierTo(0.64f * w, 0.31f * h, 0.675f * w, 0.36f * h) }
        drawPath(p13, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p14 = Path().apply { moveTo(0.705f * w, 0.36f * h); quadraticBezierTo(0.74f * w, 0.31f * h, 0.775f * w, 0.36f * h) }
        drawPath(p14, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p15 = Path().apply { moveTo(0.805f * w, 0.36f * h); quadraticBezierTo(0.84f * w, 0.31f * h, 0.875f * w, 0.36f * h) }
        drawPath(p15, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p16 = Path().apply { moveTo(0.355f * w, 0.52f * h); quadraticBezierTo(0.39f * w, 0.47f * h, 0.425f * w, 0.52f * h) }
        drawPath(p16, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p17 = Path().apply { moveTo(0.455f * w, 0.52f * h); quadraticBezierTo(0.49f * w, 0.47f * h, 0.525f * w, 0.52f * h) }
        drawPath(p17, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p18 = Path().apply { moveTo(0.555f * w, 0.52f * h); quadraticBezierTo(0.59f * w, 0.47f * h, 0.625f * w, 0.52f * h) }
        drawPath(p18, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p19 = Path().apply { moveTo(0.655f * w, 0.52f * h); quadraticBezierTo(0.69f * w, 0.47f * h, 0.725f * w, 0.52f * h) }
        drawPath(p19, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p20 = Path().apply { moveTo(0.755f * w, 0.52f * h); quadraticBezierTo(0.79f * w, 0.47f * h, 0.825f * w, 0.52f * h) }
        drawPath(p20, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p21 = Path().apply { moveTo(0.855f * w, 0.52f * h); quadraticBezierTo(0.89f * w, 0.47f * h, 0.925f * w, 0.52f * h) }
        drawPath(p21, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p22 = Path().apply { moveTo(0.305f * w, 0.68f * h); quadraticBezierTo(0.34f * w, 0.63f * h, 0.375f * w, 0.68f * h) }
        drawPath(p22, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p23 = Path().apply { moveTo(0.405f * w, 0.68f * h); quadraticBezierTo(0.44f * w, 0.63f * h, 0.475f * w, 0.68f * h) }
        drawPath(p23, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p24 = Path().apply { moveTo(0.505f * w, 0.68f * h); quadraticBezierTo(0.54f * w, 0.63f * h, 0.575f * w, 0.68f * h) }
        drawPath(p24, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p25 = Path().apply { moveTo(0.605f * w, 0.68f * h); quadraticBezierTo(0.64f * w, 0.63f * h, 0.675f * w, 0.68f * h) }
        drawPath(p25, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p26 = Path().apply { moveTo(0.705f * w, 0.68f * h); quadraticBezierTo(0.74f * w, 0.63f * h, 0.775f * w, 0.68f * h) }
        drawPath(p26, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val p27 = Path().apply { moveTo(0.805f * w, 0.68f * h); quadraticBezierTo(0.84f * w, 0.63f * h, 0.875f * w, 0.68f * h) }
        drawPath(p27, Color(0x4DDE9024L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
    val p28 = Path().apply { moveTo(0.52f * w, 0.3f * h); cubicTo(0.62f * w, 0.26f * h, 0.7f * w, 0.3f * h, 0.68f * w, 0.4f * h); cubicTo(0.62f * w, 0.46f * h, 0.52f * w, 0.44f * h, 0.5f * w, 0.38f * h); cubicTo(0.49f * w, 0.34f * h, 0.5f * w, 0.31f * h, 0.52f * w, 0.3f * h); close() }
    drawPath(p28, Color(0xEBFFFFFFL))
    drawCircle(Color(0xE6E0584AL), radius = 0.05f * w, center = Offset(0.74f * w, 0.34f * h))
    drawCircle(Brush.radialGradient(0f to Color(0x8CFFF3CCL), 0.6f to Color(0x1FFFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.58f * w, 0.26f * h), radius = 0.34f * w), radius = 0.34f * w, center = Offset(0.58f * w, 0.26f * h))
    val p29 = Path().apply { moveTo(0.32f * w, 0.26f * h); cubicTo(0.5f * w, 0.14f * h, 0.7f * w, 0.16f * h, 0.88f * w, 0.26f * h) }
    drawPath(p29, Color(0x8CFFF3CCL), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0xB3FFFFFFL), radius = 0.03f * w, center = Offset(0.66f * w, 0.22f * h))
    drawCircle(Color(0x99FFFFFFL), radius = 0.018f * w, center = Offset(0.84f * w, 0.34f * h))
    val p30 = Path().apply { moveTo(0.66f * w, 0.62f * h); cubicTo(0.62f * w, 0.84f * h, 0.78f * w, 0.92f * h, 0.84f * w, 0.8f * h); cubicTo(0.78f * w, 0.74f * h, 0.72f * w, 0.68f * h, 0.7f * w, 0.6f * h); cubicTo(0.69f * w, 0.6f * h, 0.67f * w, 0.61f * h, 0.66f * w, 0.62f * h); close() }
    drawPath(p30, Color(0x8CC8761AL))
    val p31 = Path().apply { moveTo(0.7f * w, 0.64f * h); cubicTo(0.72f * w, 0.74f * h, 0.76f * w, 0.8f * h, 0.8f * w, 0.82f * h) }
    drawPath(p31, Color(0x66FFF3CCL), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p32 = Path().apply { moveTo(0.66f * w, 0.62f * h); cubicTo(0.7f * w, 0.66f * h, 0.72f * w, 0.7f * h, 0.72f * w, 0.74f * h) }
    drawPath(p32, Color(0x59DE9024L), style = Stroke(width = 0.014f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p33 = Path().apply { moveTo(0.965f * w, 0.56f * h); cubicTo(1f * w, 0.6f * h, 1.01f * w, 0.66f * h, 0.99f * w, 0.7f * h) }
    drawPath(p33, Color(0xCCDE9024L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p34 = Path().apply { moveTo(0.95f * w, 0.58f * h); cubicTo(0.99f * w, 0.64f * h, 1f * w, 0.7f * h, 0.97f * w, 0.74f * h) }
    drawPath(p34, Color(0xB3DE9024L), style = Stroke(width = 0.01f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.085f * w, center = Offset(0.85f * w, 0.48f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.055f * w, center = Offset(0.858f * w, 0.488f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.024f * w, center = Offset(0.83f * w, 0.458f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.011f * w, center = Offset(0.868f * w, 0.5f * h))
    val p35 = Path().apply { moveTo(0.9f * w, 0.62f * h); cubicTo(0.93f * w, 0.68f * h, 0.97f * w, 0.67f * h, 0.985f * w, 0.62f * h) }
    drawPath(p35, Color(0xFF1F3D52L), style = Stroke(width = 0.016f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x66FF8FA3L), radius = 0.042f * w, center = Offset(0.8f * w, 0.58f * h))
}

/** BACKFISCH — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawBackfischShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.1f * w, 0.4f * h, 0.03f * w, 0.3f * h, -0.03f * w, 0.2f * h); cubicTo(0f * w, 0.3f * h, 0.02f * w, 0.38f * h, 0.04f * w, 0.44f * h); cubicTo(0f * w, 0.46f * h, -0.05f * w, 0.48f * h, -0.06f * w, 0.5f * h); cubicTo(-0.05f * w, 0.52f * h, 0f * w, 0.54f * h, 0.04f * w, 0.56f * h); cubicTo(0.02f * w, 0.62f * h, 0f * w, 0.7f * h, -0.03f * w, 0.8f * h); cubicTo(0.03f * w, 0.7f * h, 0.1f * w, 0.6f * h, 0.22f * w, 0.5f * h); close() }
    drawPath(p0, Brush.horizontalGradient(0f to Color(0xFF7A481DL), 0.45f to Color(0xFF9A5C24L), 1f to Color(0xFFCB8638L), startX = -0.06f * w, endX = 0.22f * w))
    val p1 = Path().apply { moveTo(0.42f * w, 0.18f * h); cubicTo(0.46f * w, 0.04f * h, 0.54f * w, 0.02f * h, 0.58f * w, 0.05f * h); cubicTo(0.55f * w, 0.09f * h, 0.6f * w, 0.07f * h, 0.63f * w, 0.1f * h); cubicTo(0.66f * w, 0.06f * h, 0.7f * w, 0.08f * h, 0.7f * w, 0.13f * h); cubicTo(0.62f * w, 0.16f * h, 0.52f * w, 0.18f * h, 0.42f * w, 0.18f * h); close() }
    drawPath(p1, Brush.verticalGradient(0f to Color(0xFF7A481DL), 0.5f to Color(0xFF9A5C24L), 1f to Color(0xFFCB8638L), startY = 0.02f * h, endY = 0.18f * h))
    val p2 = Path().apply { moveTo(0.42f * w, 0.84f * h); cubicTo(0.46f * w, 0.96f * h, 0.54f * w, 0.97f * h, 0.57f * w, 0.94f * h); cubicTo(0.55f * w, 0.9f * h, 0.6f * w, 0.92f * h, 0.63f * w, 0.88f * h); cubicTo(0.55f * w, 0.87f * h, 0.48f * w, 0.86f * h, 0.42f * w, 0.84f * h); close() }
    drawPath(p2, Brush.verticalGradient(0f to Color(0xFF7A481DL), 0.5f to Color(0xFF9A5C24L), 1f to Color(0xFFCB8638L), startY = 0.96f * h, endY = 0.82f * h))
    val p3 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.27f * w, 0.3f * h, 0.33f * w, 0.22f * h, 0.4f * w, 0.18f * h); cubicTo(0.44f * w, 0.22f * h, 0.46f * w, 0.2f * h, 0.5f * w, 0.16f * h); cubicTo(0.55f * w, 0.21f * h, 0.58f * w, 0.18f * h, 0.63f * w, 0.15f * h); cubicTo(0.7f * w, 0.18f * h, 0.74f * w, 0.16f * h, 0.8f * w, 0.2f * h); cubicTo(0.86f * w, 0.23f * h, 0.88f * w, 0.21f * h, 0.92f * w, 0.26f * h); cubicTo(0.99f * w, 0.31f * h, 1f * w, 0.36f * h, 0.97f * w, 0.4f * h); cubicTo(1.01f * w, 0.43f * h, 1f * w, 0.48f * h, 0.96f * w, 0.5f * h); cubicTo(1f * w, 0.52f * h, 1.01f * w, 0.57f * h, 0.97f * w, 0.6f * h); cubicTo(1f * w, 0.64f * h, 0.99f * w, 0.69f * h, 0.92f * w, 0.74f * h); cubicTo(0.88f * w, 0.79f * h, 0.86f * w, 0.77f * h, 0.8f * w, 0.8f * h); cubicTo(0.74f * w, 0.84f * h, 0.7f * w, 0.82f * h, 0.63f * w, 0.85f * h); cubicTo(0.58f * w, 0.82f * h, 0.55f * w, 0.85f * h, 0.5f * w, 0.84f * h); cubicTo(0.46f * w, 0.88f * h, 0.43f * w, 0.86f * h, 0.39f * w, 0.82f * h); cubicTo(0.32f * w, 0.78f * h, 0.27f * w, 0.7f * h, 0.22f * w, 0.5f * h); close() }
    drawPath(p3, Brush.radialGradient(0f to Color(0xFFE8C079L), 0.4f to Color(0xFFCB8638L), 0.78f to Color(0xFFCB8638L), 1f to Color(0xFF9A5C24L), center = Offset(0.62f * w, 0.4f * h), radius = 0.66f * w))
    val p4 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.27f * w, 0.3f * h, 0.33f * w, 0.22f * h, 0.4f * w, 0.18f * h); cubicTo(0.55f * w, 0.21f * h, 0.74f * w, 0.16f * h, 0.92f * w, 0.26f * h); cubicTo(0.99f * w, 0.31f * h, 1f * w, 0.36f * h, 0.97f * w, 0.4f * h); cubicTo(0.8f * w, 0.32f * h, 0.55f * w, 0.3f * h, 0.4f * w, 0.34f * h); cubicTo(0.3f * w, 0.38f * h, 0.25f * w, 0.44f * h, 0.22f * w, 0.5f * h); close() }
    drawPath(p4, Color(0x4D9A5C24L))
    clipPath(p3) {
        drawCircle(Brush.radialGradient(0f to Color(0x8CF2DCA6L), 1f to Color(0x00F2DCA6L), center = Offset(0.58f * w, 0.34f * h), radius = 0.34f * w), radius = 0.34f * w, center = Offset(0.58f * w, 0.34f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.65f * w, 0.5f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.501645f * w, 0.418115f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.614629f * w, 0.615164f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.718394f * w, 0.470403f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.386478f * w, 0.388197f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.799445f * w, 0.672235f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.534223f * w, 0.424247f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.474971f * w, 0.392953f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.868686f * w, 0.711484f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.322664f * w, 0.370654f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.732519f * w, 0.418929f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.698313f * w, 0.735006f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.306425f * w, 0.31516f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.942383f * w, 0.461332f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.392437f * w, 0.74206f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.551156f * w, 0.262536f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.893982f * w, 0.516344f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.206492f * w, 0.731997f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.88543f * w, 0.217065f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.582179f * w, 0.580081f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.32792f * w, 0.704842f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(1.028548f * w, 0.182491f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.238738f * w, 0.648419f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.610791f * w, 0.517941f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.66678f * w, 0.401648f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.439785f * w, 0.578059f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.776184f * w, 0.542401f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.51718f * w, 0.346423f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.518939f * w, 0.635577f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.825663f * w, 0.517277f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.33861f * w, 0.324292f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.753411f * w, 0.687912f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.652394f * w, 0.474461f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.350909f * w, 0.323603f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.925356f * w, 0.732928f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.371267f * w, 0.420543f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.59965f * w, 0.34174f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.844889f * w, 0.767402f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.228356f * w, 0.360568f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.904096f * w, 0.376871f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.532032f * w, 0.788462f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.382619f * w, 0.299222f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.99959f * w, 0.426814f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.225026f * w, 0.793919f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.74733f * w, 0.240995f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.769351f * w, 0.488793f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.554658f * w, 0.531382f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.727179f * w, 0.409853f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.516217f * w, 0.521372f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.557653f * w, 0.603762f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.777774f * w, 0.343174f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.365566f * w, 0.56491f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.763181f * w, 0.605973f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.612204f * w, 0.299707f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.397531f * w, 0.617203f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.899951f * w, 0.584674f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.359965f * w, 0.272526f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.642121f * w, 0.672419f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.794846f * w, 0.545858f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.257466f * w, 0.261848f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.913265f * w, 0.725666f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.48843f * w, 0.493576f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.437277f * w, 0.268284f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.96417f * w, 0.772565f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.220741f * w, 0.431744f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.790298f * w, 0.291907f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.710228f * w, 0.809265f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.235044f * w, 0.364471f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(1.03436f * w, 0.331977f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.569638f * w, 0.53695f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.715874f * w, 0.676866f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.3415f * w, 0.29844f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.873497f * w, 0.71811f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.46535f * w, 0.275017f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.503895f * w, 0.721141f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.89695f * w, 0.293911f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.248265f * w, 0.679901f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.817132f * w, 0.356776f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.647129f * w, 0.597251f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.295488f * w, 0.456322f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(1.01293f * w, 0.484633f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.296529f * w, 0.577418f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.622933f * w, 0.360226f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.885442f * w, 0.699623f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.56039f * w, 0.477899f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.712375f * w, 0.587081f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.560349f * w, 0.37666f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.498117f * w, 0.651743f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.821324f * w, 0.328159f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.367554f * w, 0.682528f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.707821f * w, 0.317101f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.698489f * w, 0.672475f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.324026f * w, 0.348711f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.918566f * w, 0.61991f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.412327f * w, 0.420563f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.540565f * w, 0.531458f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.894608f * w, 0.522018f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.213792f * w, 0.421359f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.87306f * w, 0.63583f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.596544f * w, 0.309115f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.315217f * w, 0.741112f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(1.035285f * w, 0.216052f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.241694f * w, 0.817082f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.607271f * w, 0.470559f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.672315f * w, 0.600453f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.43012f * w, 0.375106f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.784289f * w, 0.63503f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.518631f * w, 0.367073f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.50416f * w, 0.619402f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.849207f * w, 0.40469f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.318014f * w, 0.56184f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.758435f * w, 0.479422f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.668649f * w, 0.473488f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.319224f * w, 0.577152f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.956858f * w, 0.371159f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.357477f * w, 0.678961f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.586157f * w, 0.275105f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.880978f * w, 0.764148f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.187367f * w, 0.205536f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.928015f * w, 0.813939f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.539551f * w, 0.178889f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.345439f * w, 0.815044f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.638984f * w, 0.474315f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.481392f * w, 0.575968f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.654292f * w, 0.42062f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.686f * w, 0.567428f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.007f * w, center = Offset(0.385689f * w, 0.456083f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.01375f * w, center = Offset(0.840706f * w, 0.511277f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.0115f * w, center = Offset(0.470701f * w, 0.528198f * h))
        drawCircle(Color(0x669A5C24L), radius = 0.00925f * w, center = Offset(0.525815f * w, 0.4279f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.891742f * w, 0.590543f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.283011f * w, 0.321506f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.80489f * w, 0.714137f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.666362f * w, 0.23006f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.313358f * w, 0.760291f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(1.002103f * w, 0.237769f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.330001f * w, 0.695911f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.618294f * w, 0.355293f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.629826f * w, 0.493138f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.483713f * w, 0.505693f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.744633f * w, 0.433272f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.571771f * w, 0.593165f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.493375f * w, 0.344316f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.847213f * w, 0.666393f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.370223f * w, 0.300873f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.713315f * w, 0.670037f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.720956f * w, 0.341968f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.321746f * w, 0.584954f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.933116f * w, 0.464563f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.428211f * w, 0.436428f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.537745f * w, 0.623552f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.91681f * w, 0.284638f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.219445f * w, 0.75029f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.87678f * w, 0.19892f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.620143f * w, 0.782834f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.589201f * w, 0.471605f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.736763f * w, 0.548974f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.479869f * w, 0.445052f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.639625f * w, 0.499178f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.734945f * w, 0.530502f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.369993f * w, 0.393753f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.844302f * w, 0.639772f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.518576f * w, 0.296993f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.486654f * w, 0.708954f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.904205f * w, 0.267482f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.291386f * w, 0.691169f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.778335f * w, 0.33468f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.698007f * w, 0.578383f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.293619f * w, 0.483511f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.998247f * w, 0.40787f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.356957f * w, 0.658003f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.58144f * w, 0.248541f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.631732f * w, 0.510492f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.483008f * w, 0.39855f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.735375f * w, 0.604466f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.589037f * w, 0.375046f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.476978f * w, 0.583656f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.852333f * w, 0.437242f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.381629f * w, 0.486734f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.689355f * w, 0.557895f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.745496f * w, 0.35677f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.012f * w, center = Offset(0.310415f * w, 0.680886f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.006f * w, center = Offset(0.923492f * w, 0.257021f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.008f * w, center = Offset(0.455447f * w, 0.743091f * h))
        drawCircle(Color(0xA6F2DCA6L), radius = 0.01f * w, center = Offset(0.506412f * w, 0.242913f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.538385f * w, 0.483449f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.548385f * w, 0.491449f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.536068f * w, 0.447685f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.546068f * w, 0.455685f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.741496f * w, 0.447712f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.751496f * w, 0.455712f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.357209f * w, 0.509234f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.367209f * w, 0.517234f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.734071f * w, 0.363798f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.744071f * w, 0.371798f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.623286f * w, 0.609807f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.633286f * w, 0.617807f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.304745f * w, 0.254179f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.314745f * w, 0.262179f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.67987f * w, 0.524863f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.68987f * w, 0.532863f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.47973f * w, 0.361397f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.48973f * w, 0.369397f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.586732f * w, 0.589833f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.596732f * w, 0.597833f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.728216f * w, 0.305749f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.738216f * w, 0.313749f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.310638f * w, 0.62791f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.320638f * w, 0.63591f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.836096f * w, 0.292465f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.846096f * w, 0.300465f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.505314f * w, 0.610805f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.515314f * w, 0.618805f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.52935f * w, 0.430857f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.53935f * w, 0.438857f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.716565f * w, 0.480971f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.726565f * w, 0.488971f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.412285f * w, 0.460891f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.422285f * w, 0.468891f * h))
        drawCircle(Color(0x80F2DCA6L), radius = 0.013f * w, center = Offset(0.665863f * w, 0.424306f * h))
        drawCircle(Color(0x737A481DL), radius = 0.006f * w, center = Offset(0.675863f * w, 0.432306f * h))
    }
    val p5 = Path().apply { moveTo(0.33f * w, 0.23f * h); cubicTo(0.45f * w, 0.17f * h, 0.62f * w, 0.16f * h, 0.8f * w, 0.21f * h) }
    drawPath(p5, Color(0x73F2DCA6L), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p6 = Path().apply { moveTo(0.06f * w, 0.78f * h); cubicTo(0.2f * w, 0.7f * h, 0.3f * w, 0.78f * h, 0.3f * w, 0.9f * h); cubicTo(0.2f * w, 0.96f * h, 0.1f * w, 0.94f * h, 0.06f * w, 0.86f * h); close() }
    drawPath(p6, Brush.verticalGradient(0f to Color(0xFFFFF09AL), 1f to Color(0xFFFFE45EL), startY = 0.7f * h, endY = 0.96f * h))
    val p7 = Path().apply { moveTo(0.06f * w, 0.78f * h); cubicTo(0.2f * w, 0.7f * h, 0.3f * w, 0.78f * h, 0.3f * w, 0.9f * h) }
    drawPath(p7, Color(0xFFE8B23AL), style = Stroke(width = 0.022f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p8 = Path().apply { moveTo(0.1f * w, 0.8f * h); cubicTo(0.2f * w, 0.76f * h, 0.27f * w, 0.8f * h, 0.27f * w, 0.88f * h) }
    drawPath(p8, Color(0xB3FFFFFFL), style = Stroke(width = 0.014f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawLine(Color(0x8CFFFFFFL), Offset(0.12f * w, 0.82f * h), Offset(0.2f * w, 0.86f * h), strokeWidth = 0.008f * w, cap = StrokeCap.Round)
    drawLine(Color(0x8CFFFFFFL), Offset(0.16f * w, 0.8f * h), Offset(0.21f * w, 0.88f * h), strokeWidth = 0.008f * w, cap = StrokeCap.Round)
    drawLine(Color(0x8CFFFFFFL), Offset(0.21f * w, 0.8f * h), Offset(0.24f * w, 0.88f * h), strokeWidth = 0.008f * w, cap = StrokeCap.Round)
    drawCircle(Color(0xFFFFFFFFL), radius = 0.1f * w, center = Offset(0.8f * w, 0.46f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.064f * w, center = Offset(0.812f * w, 0.468f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.026f * w, center = Offset(0.776f * w, 0.434f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.011f * w, center = Offset(0.824f * w, 0.486f * h))
    val p9 = Path().apply { moveTo(0.72f * w, 0.31f * h); cubicTo(0.78f * w, 0.27f * h, 0.86f * w, 0.28f * h, 0.9f * w, 0.32f * h) }
    drawPath(p9, Color(0x997A481DL), style = Stroke(width = 0.016f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x6BFF8FA3L), radius = 0.05f * w, center = Offset(0.71f * w, 0.58f * h))
    val p10 = Path().apply { moveTo(0.8f * w, 0.64f * h); cubicTo(0.85f * w, 0.71f * h, 0.91f * w, 0.69f * h, 0.94f * w, 0.62f * h) }
    drawPath(p10, Color(0xFF1F3D52L), style = Stroke(width = 0.018f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** MEGALODON — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawMegalodonShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.1f * w, 0.4f * h, 0.02f * w, 0.22f * h, -0.04f * w, 0.1f * h); cubicTo(0.02f * w, 0.3f * h, 0.06f * w, 0.42f * h, 0.1f * w, 0.5f * h); cubicTo(0.05f * w, 0.6f * h, 0f * w, 0.78f * h, -0.05f * w, 0.92f * h); cubicTo(0.06f * w, 0.78f * h, 0.14f * w, 0.62f * h, 0.22f * w, 0.52f * h); close() }
    drawPath(p0, Brush.verticalGradient(0f to Color(0xFF333C45L), 0.5f to Color(0xFF5B6772L), 1f to Color(0xFF333C45L), startY = 0.1f * h, endY = 0.92f * h))
    val p1 = Path().apply { moveTo(0.46f * w, 0.3f * h); cubicTo(0.5f * w, 0.02f * h, 0.6f * w, -0.06f * h, 0.64f * w, 0.04f * h); cubicTo(0.62f * w, 0.1f * h, 0.62f * w, 0.18f * h, 0.66f * w, 0.26f * h); cubicTo(0.58f * w, 0.22f * h, 0.5f * w, 0.24f * h, 0.46f * w, 0.3f * h); close() }
    drawPath(p1, Brush.verticalGradient(0f to Color(0xFF333C45L), 1f to Color(0xFF5B6772L), startY = -0.04f * h, endY = 0.3f * h))
    val p2 = Path().apply { moveTo(0.2f * w, 0.48f * h); cubicTo(0.3f * w, 0.22f * h, 0.5f * w, 0.18f * h, 0.68f * w, 0.22f * h); cubicTo(0.82f * w, 0.25f * h, 0.93f * w, 0.3f * h, 0.97f * w, 0.38f * h); cubicTo(0.92f * w, 0.4f * h, 0.86f * w, 0.4f * h, 0.82f * w, 0.41f * h); cubicTo(0.86f * w, 0.5f * h, 0.86f * w, 0.58f * h, 0.82f * w, 0.66f * h); cubicTo(0.86f * w, 0.68f * h, 0.92f * w, 0.7f * h, 0.96f * w, 0.74f * h); cubicTo(0.88f * w, 0.82f * h, 0.74f * w, 0.88f * h, 0.56f * w, 0.88f * h); cubicTo(0.38f * w, 0.88f * h, 0.26f * w, 0.74f * h, 0.2f * w, 0.52f * h); close() }
    drawPath(p2, Brush.verticalGradient(0f to Color(0xFF333C45L), 0.45f to Color(0xFF5B6772L), 1f to Color(0xFFC8D1D7L), startY = 0.18f * h, endY = 0.88f * h))
    clipPath(p2) {
        val p3 = Path().apply { moveTo(0.22f * w, 0.62f * h); cubicTo(0.34f * w, 0.92f * h, 0.74f * w, 0.9f * h, 0.92f * w, 0.74f * h); cubicTo(0.7f * w, 0.8f * h, 0.42f * w, 0.8f * h, 0.22f * w, 0.62f * h); close() }
        drawPath(p3, Color(0xE6C8D1D7L))
        val p4 = Path().apply { moveTo(0.4f * w, 0.24f * h); cubicTo(0.55f * w, 0.2f * h, 0.7f * w, 0.22f * h, 0.8f * w, 0.27f * h); cubicTo(0.66f * w, 0.26f * h, 0.52f * w, 0.26f * h, 0.4f * w, 0.3f * h); close() }
        drawPath(p4, Color(0x59222A31L))
        drawCircle(Brush.radialGradient(0f to Color(0x52FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.5f * w, 0.3f * h), radius = 0.34f * w), radius = 0.34f * w, center = Offset(0.5f * w, 0.3f * h))
    }
    val p5 = Path().apply { moveTo(0.24f * w, 0.46f * h); cubicTo(0.32f * w, 0.24f * h, 0.5f * w, 0.2f * h, 0.68f * w, 0.235f * h); cubicTo(0.82f * w, 0.265f * h, 0.92f * w, 0.31f * h, 0.965f * w, 0.375f * h) }
    drawPath(p5, Color(0x4DFFFFFFL), style = Stroke(width = 0.022f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p6 = Path().apply { moveTo(0.42f * w, 0.42f * h); cubicTo(0.408f * w, 0.5f * h, 0.408f * w, 0.56f * h, 0.414f * w, 0.62f * h) }
    drawPath(p6, Color(0x4D222A31L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p7 = Path().apply { moveTo(0.465f * w, 0.426f * h); cubicTo(0.453f * w, 0.5f * h, 0.453f * w, 0.56f * h, 0.459f * w, 0.62f * h) }
    drawPath(p7, Color(0x4D222A31L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p8 = Path().apply { moveTo(0.51f * w, 0.432f * h); cubicTo(0.498f * w, 0.5f * h, 0.498f * w, 0.56f * h, 0.504f * w, 0.62f * h) }
    drawPath(p8, Color(0x4D222A31L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p9 = Path().apply { moveTo(0.34f * w, 0.4f * h); lineTo(0.4f * w, 0.52f * h) }
    drawPath(p9, Color(0x73C8D1D7L), style = Stroke(width = 0.01f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p10 = Path().apply { moveTo(0.3f * w, 0.5f * h); lineTo(0.345f * w, 0.585f * h) }
    drawPath(p10, Color(0x59C8D1D7L), style = Stroke(width = 0.008f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p11 = Path().apply { moveTo(0.5f * w, 0.78f * h); cubicTo(0.46f * w, 0.98f * h, 0.64f * w, 1.02f * h, 0.66f * w, 0.84f * h); cubicTo(0.6f * w, 0.82f * h, 0.54f * w, 0.8f * h, 0.5f * w, 0.78f * h); close() }
    drawPath(p11, Brush.verticalGradient(0f to Color(0xFF5B6772L), 1f to Color(0xFF333C45L), startY = 0.78f * h, endY = 1f * h))
    val p12 = Path().apply { moveTo(0.78f * w, 0.5f * h); cubicTo(0.86f * w, 0.43f * h, 0.94f * w, 0.42f * h, 0.985f * w, 0.445f * h); cubicTo(0.96f * w, 0.5f * h, 0.96f * w, 0.62f * h, 0.985f * w, 0.7f * h); cubicTo(0.94f * w, 0.73f * h, 0.86f * w, 0.73f * h, 0.78f * w, 0.66f * h); cubicTo(0.8f * w, 0.6f * h, 0.8f * w, 0.56f * h, 0.78f * w, 0.5f * h); close() }
    drawPath(p12, Brush.radialGradient(0f to Color(0xFF222A31L), 1f to Color(0xFF333C45L), center = Offset(0.88f * w, 0.57f * h), radius = 0.13f * w))
    clipPath(p12) {
        val p13 = Path().apply { moveTo(0.78f * w, 0.485f * h); cubicTo(0.88f * w, 0.435f * h, 0.95f * w, 0.43f * h, 0.985f * w, 0.45f * h); cubicTo(0.95f * w, 0.49f * h, 0.88f * w, 0.5f * h, 0.78f * w, 0.515f * h); close() }
        drawPath(p13, Color(0xFFE58A95L))
        val p14 = Path().apply { moveTo(0.78f * w, 0.655f * h); cubicTo(0.88f * w, 0.71f * h, 0.95f * w, 0.715f * h, 0.985f * w, 0.69f * h); cubicTo(0.95f * w, 0.65f * h, 0.88f * w, 0.64f * h, 0.78f * w, 0.625f * h); close() }
        drawPath(p14, Color(0xFFE58A95L))
        val p15 = Path().apply { moveTo(0.77f * w, 0.475f * h); lineTo(0.83f * w, 0.475f * h); lineTo(0.8f * w, 0.533f * h); close() }
        drawPath(p15, Color(0xFFFFFFFFL))
        val p16 = Path().apply { moveTo(0.8151f * w, 0.466515f * h); lineTo(0.8724f * w, 0.466515f * h); lineTo(0.84375f * w, 0.524515f * h); close() }
        drawPath(p16, Color(0xFFFFFFFFL))
        val p17 = Path().apply { moveTo(0.8602f * w, 0.463f * h); lineTo(0.9148f * w, 0.463f * h); lineTo(0.8875f * w, 0.521f * h); close() }
        drawPath(p17, Color(0xFFFFFFFFL))
        val p18 = Path().apply { moveTo(0.9053f * w, 0.466515f * h); lineTo(0.9572f * w, 0.466515f * h); lineTo(0.93125f * w, 0.524515f * h); close() }
        drawPath(p18, Color(0xFFFFFFFFL))
        val p19 = Path().apply { moveTo(0.9504f * w, 0.475f * h); lineTo(0.9996f * w, 0.475f * h); lineTo(0.975f * w, 0.533f * h); close() }
        drawPath(p19, Color(0xFFFFFFFFL))
        val p20 = Path().apply { moveTo(0.787f * w, 0.665f * h); lineTo(0.843f * w, 0.665f * h); lineTo(0.815f * w, 0.613f * h); close() }
        drawPath(p20, Color(0xFFFFFFFFL))
        val p21 = Path().apply { moveTo(0.836733f * w, 0.67366f * h); lineTo(0.889933f * w, 0.67366f * h); lineTo(0.863333f * w, 0.62166f * h); close() }
        drawPath(p21, Color(0xFFFFFFFFL))
        val p22 = Path().apply { moveTo(0.886467f * w, 0.67366f * h); lineTo(0.936867f * w, 0.67366f * h); lineTo(0.911667f * w, 0.62166f * h); close() }
        drawPath(p22, Color(0xFFFFFFFFL))
        val p23 = Path().apply { moveTo(0.9362f * w, 0.665f * h); lineTo(0.9838f * w, 0.665f * h); lineTo(0.96f * w, 0.613f * h); close() }
        drawPath(p23, Color(0xFFFFFFFFL))
    }
    drawCircle(Color(0xFFFFFFFFL), radius = 0.085f * w, center = Offset(0.66f * w, 0.45f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.052f * w, center = Offset(0.668f * w, 0.456f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.022f * w, center = Offset(0.642f * w, 0.43f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.01f * w, center = Offset(0.68f * w, 0.468f * h))
    val p24 = Path().apply { moveTo(0.61f * w, 0.35f * h); cubicTo(0.65f * w, 0.315f * h, 0.71f * w, 0.32f * h, 0.745f * w, 0.355f * h) }
    drawPath(p24, Color(0xFF333C45L), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x66FF8FA3L), radius = 0.05f * w, center = Offset(0.6f * w, 0.62f * h))
}


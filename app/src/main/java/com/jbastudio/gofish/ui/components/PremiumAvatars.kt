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
    val p0 = Path().apply { moveTo(0.8f * w, 0.46f * h); cubicTo(0.92f * w, 0.38f * h, 0.99f * w, 0.42f * h, 0.98f * w, 0.5f * h); cubicTo(0.99f * w, 0.58f * h, 0.92f * w, 0.62f * h, 0.8f * w, 0.55f * h); close() }
    drawPath(p0, Color(0xFFDE8E9BL))
    val p1 = Path().apply { moveTo(0.22f * w, 0.58f * h); cubicTo(0.06f * w, 0.6f * h, 0.03f * w, 0.72f * h, 0.12f * w, 0.78f * h); cubicTo(0.18f * w, 0.74f * h, 0.24f * w, 0.66f * h, 0.28f * w, 0.62f * h); close() }
    drawPath(p1, Color(0xFFDE8E9BL))
    val p2 = Path().apply { moveTo(0.78f * w, 0.58f * h); cubicTo(0.94f * w, 0.6f * h, 0.97f * w, 0.72f * h, 0.88f * w, 0.78f * h); cubicTo(0.82f * w, 0.74f * h, 0.76f * w, 0.66f * h, 0.72f * w, 0.62f * h); close() }
    drawPath(p2, Color(0xFFDE8E9BL))
    val p3 = Path().apply { moveTo(0.5f * w, 0.13f * h); cubicTo(0.71f * w, 0.12f * h, 0.87f * w, 0.25f * h, 0.87f * w, 0.44f * h); cubicTo(0.87f * w, 0.6f * h, 0.78f * w, 0.8f * h, 0.6f * w, 0.85f * h); cubicTo(0.53f * w, 0.87f * h, 0.47f * w, 0.87f * h, 0.4f * w, 0.85f * h); cubicTo(0.22f * w, 0.8f * h, 0.13f * w, 0.6f * h, 0.13f * w, 0.44f * h); cubicTo(0.13f * w, 0.25f * h, 0.29f * w, 0.12f * h, 0.5f * w, 0.13f * h); close() }
    drawPath(p3, Brush.verticalGradient(0f to Color(0xFFF8C2CAL), 0.5f to Color(0xFFF2A7AFL), 1f to Color(0xFFDE8E9BL), startY = 0.1f * h, endY = 0.86f * h))
    val p4 = Path().apply { moveTo(0.3f * w, 0.58f * h); cubicTo(0.36f * w, 0.82f * h, 0.64f * w, 0.82f * h, 0.7f * w, 0.58f * h); cubicTo(0.6f * w, 0.7f * h, 0.4f * w, 0.7f * h, 0.3f * w, 0.58f * h); close() }
    drawPath(p4, Color(0x8CF8C2CAL))
    drawCircle(Brush.radialGradient(0f to Color(0x80FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.43f * w, 0.26f * h), radius = 0.36f * w), radius = 0.36f * w, center = Offset(0.43f * w, 0.26f * h))
    drawPath(p3, Color(0x80C16E80L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x73E98C99L), radius = 0.05f * w, center = Offset(0.27f * w, 0.56f * h))
    drawCircle(Color(0x73E98C99L), radius = 0.05f * w, center = Offset(0.73f * w, 0.56f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.052f * w, center = Offset(0.37f * w, 0.38f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.018f * w, center = Offset(0.355f * w, 0.364f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.052f * w, center = Offset(0.63f * w, 0.38f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.018f * w, center = Offset(0.615f * w, 0.364f * h))
    val p5 = Path().apply { moveTo(0.39f * w, 0.45f * h); cubicTo(0.41f * w, 0.36f * h, 0.59f * w, 0.36f * h, 0.61f * w, 0.45f * h); cubicTo(0.67f * w, 0.53f * h, 0.64f * w, 0.67f * h, 0.5f * w, 0.7f * h); cubicTo(0.36f * w, 0.67f * h, 0.33f * w, 0.53f * h, 0.39f * w, 0.45f * h); close() }
    drawPath(p5, Brush.verticalGradient(0f to Color(0xFFF8C2CAL), 1f to Color(0xFFDE8E9BL), startY = 0.38f * h, endY = 0.7f * h))
    drawCircle(Color(0x66FFFFFFL), radius = 0.05f * w, center = Offset(0.45f * w, 0.48f * h))
    drawCircle(Color(0xA6C16E80L), radius = 0.016f * w, center = Offset(0.45f * w, 0.62f * h))
    drawCircle(Color(0xA6C16E80L), radius = 0.016f * w, center = Offset(0.55f * w, 0.62f * h))
    val p6 = Path().apply { moveTo(0.35f * w, 0.73f * h); cubicTo(0.42f * w, 0.71f * h, 0.58f * w, 0.71f * h, 0.65f * w, 0.73f * h); cubicTo(0.63f * w, 0.84f * h, 0.37f * w, 0.84f * h, 0.35f * w, 0.73f * h); close() }
    drawPath(p6, Color(0xFFE07F8EL))
    val p7 = Path().apply { moveTo(0.4f * w, 0.77f * h); cubicTo(0.46f * w, 0.81f * h, 0.54f * w, 0.81f * h, 0.6f * w, 0.77f * h); cubicTo(0.54f * w, 0.79f * h, 0.46f * w, 0.79f * h, 0.4f * w, 0.77f * h); close() }
    drawPath(p7, Color(0xD9EC97A4L))
    val p8 = Path().apply { moveTo(0.35f * w, 0.73f * h); cubicTo(0.42f * w, 0.71f * h, 0.58f * w, 0.71f * h, 0.65f * w, 0.73f * h) }
    drawPath(p8, Color(0xB31F3D52L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
    val p0 = Path().apply { moveTo(0.04f * w, 0.8f * h); cubicTo(0.18f * w, 0.71f * h, 0.3f * w, 0.79f * h, 0.3f * w, 0.92f * h); cubicTo(0.18f * w, 0.98f * h, 0.07f * w, 0.96f * h, 0.04f * w, 0.88f * h); close() }
    drawPath(p0, Brush.verticalGradient(0f to Color(0xFFFFF1A0L), 1f to Color(0xFFFFE45EL), startY = 0.71f * h, endY = 0.98f * h))
    val p1 = Path().apply { moveTo(0.04f * w, 0.8f * h); cubicTo(0.18f * w, 0.71f * h, 0.3f * w, 0.79f * h, 0.3f * w, 0.92f * h) }
    drawPath(p1, Color(0xFFE8B23AL), style = Stroke(width = 0.022f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p2 = Path().apply { moveTo(0.1f * w, 0.82f * h); cubicTo(0.19f * w, 0.78f * h, 0.26f * w, 0.82f * h, 0.26f * w, 0.9f * h) }
    drawPath(p2, Color(0xB3FFFFFFL), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawLine(Color(0x80FFFFFFL), Offset(0.12f * w, 0.83f * h), Offset(0.2f * w, 0.9f * h), strokeWidth = 0.008f * w, cap = StrokeCap.Round)
    drawLine(Color(0x80FFFFFFL), Offset(0.145f * w, 0.83f * h), Offset(0.21f * w, 0.9f * h), strokeWidth = 0.008f * w, cap = StrokeCap.Round)
    drawLine(Color(0x80FFFFFFL), Offset(0.17f * w, 0.83f * h), Offset(0.22f * w, 0.9f * h), strokeWidth = 0.008f * w, cap = StrokeCap.Round)
    val p3 = Path().apply { moveTo(0.24f * w, 0.5f * h); cubicTo(0.12f * w, 0.4f * h, 0.04f * w, 0.34f * h, -0.02f * w, 0.26f * h); cubicTo(0.02f * w, 0.4f * h, 0.02f * w, 0.46f * h, 0.02f * w, 0.5f * h); cubicTo(0.02f * w, 0.54f * h, 0.02f * w, 0.6f * h, -0.02f * w, 0.74f * h); cubicTo(0.06f * w, 0.64f * h, 0.14f * w, 0.56f * h, 0.24f * w, 0.5f * h); close() }
    drawPath(p3, Brush.horizontalGradient(0f to Color(0xFF7A481DL), 0.5f to Color(0xFF9A5C24L), 1f to Color(0xFFCB8638L), startX = -0.02f * w, endX = 0.24f * w))
    val p4 = Path().apply { moveTo(0.4f * w, 0.2f * h); cubicTo(0.46f * w, 0.07f * h, 0.56f * w, 0.07f * h, 0.58f * w, 0.16f * h); cubicTo(0.52f * w, 0.16f * h, 0.46f * w, 0.18f * h, 0.4f * w, 0.2f * h); close() }
    drawPath(p4, Brush.verticalGradient(0f to Color(0xFF7A481DL), 1f to Color(0xFFCB8638L), startY = 0.06f * h, endY = 0.2f * h))
    val p5 = Path().apply { moveTo(0.4f * w, 0.8f * h); cubicTo(0.46f * w, 0.93f * h, 0.56f * w, 0.93f * h, 0.58f * w, 0.84f * h); cubicTo(0.52f * w, 0.84f * h, 0.46f * w, 0.82f * h, 0.4f * w, 0.8f * h); close() }
    drawPath(p5, Brush.verticalGradient(0f to Color(0xFF7A481DL), 1f to Color(0xFFCB8638L), startY = 0.94f * h, endY = 0.8f * h))
    val p6 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.26f * w, 0.3f * h, 0.34f * w, 0.22f * h, 0.44f * w, 0.2f * h); cubicTo(0.5f * w, 0.23f * h, 0.54f * w, 0.2f * h, 0.6f * w, 0.18f * h); cubicTo(0.7f * w, 0.2f * h, 0.78f * w, 0.2f * h, 0.86f * w, 0.26f * h); cubicTo(0.93f * w, 0.31f * h, 0.97f * w, 0.36f * h, 0.95f * w, 0.42f * h); cubicTo(0.99f * w, 0.46f * h, 0.99f * w, 0.54f * h, 0.95f * w, 0.58f * h); cubicTo(0.97f * w, 0.64f * h, 0.93f * w, 0.7f * h, 0.86f * w, 0.74f * h); cubicTo(0.78f * w, 0.8f * h, 0.7f * w, 0.8f * h, 0.6f * w, 0.82f * h); cubicTo(0.54f * w, 0.8f * h, 0.5f * w, 0.82f * h, 0.44f * w, 0.8f * h); cubicTo(0.34f * w, 0.78f * h, 0.27f * w, 0.7f * h, 0.22f * w, 0.5f * h); close() }
    drawPath(p6, Brush.radialGradient(0f to Color(0xFFE8C079L), 0.45f to Color(0xFFCB8638L), 0.8f to Color(0xFFCB8638L), 1f to Color(0xFF9A5C24L), center = Offset(0.56f * w, 0.42f * h), radius = 0.62f * w))
    drawPath(p6, Brush.verticalGradient(0f to Color(0x529A5C24L), 0.4f to Color(0x009A5C24L), 0.7f to Color(0x009A5C24L), 1f to Color(0x4D7A481DL), startY = 0.18f * h, endY = 0.82f * h))
    clipPath(p6) {
        drawCircle(Brush.radialGradient(0f to Color(0x8CF2DCA6L), 1f to Color(0x00F2DCA6L), center = Offset(0.55f * w, 0.36f * h), radius = 0.34f * w), radius = 0.34f * w, center = Offset(0.55f * w, 0.36f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.6f * w, 0.5f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.473009f * w, 0.439321f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.573091f * w, 0.586346f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.666597f * w, 0.477674f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.36699f * w, 0.415326f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.740803f * w, 0.630817f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.500239f * w, 0.442337f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.446207f * w, 0.418372f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.804899f * w, 0.661502f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.306905f * w, 0.401102f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.681064f * w, 0.437947f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.649899f * w, 0.680045f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.291331f * w, 0.358272f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.873565f * w, 0.47033f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.369782f * w, 0.68586f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.515211f * w, 0.31756f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.829724f * w, 0.512564f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.198782f * w, 0.678426f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.82213f * w, 0.282298f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.543626f * w, 0.561644f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.309925f * w, 0.657744f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.954035f * w, 0.255403f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.227718f * w, 0.614376f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.649356f * w, 0.624473f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.771284f * w, 0.239286f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.14985f * w, 0.667425f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.956542f * w, 0.579957f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.389915f * w, 0.23575f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.404839f * w, 0.71743f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.595495f * w, 0.502277f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.446054f * w, 0.435825f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.640646f * w, 0.582764f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.590556f * w, 0.487521f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.404406f * w, 0.407682f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.773562f * w, 0.628099f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.404077f * w, 0.454619f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.559754f * w, 0.406865f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.736252f * w, 0.661247f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.287041f * w, 0.414199f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.787212f * w, 0.42292f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.508463f * w, 0.683259f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.393047f * w, 0.370804f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.870356f * w, 0.452375f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.265855f * w, 0.693173f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.6766f * w, 0.328259f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.695098f * w, 0.492509f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.232112f * w, 0.690132f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.913581f * w, 0.290017f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.370458f * w, 0.540509f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.475335f * w, 0.673812f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.88585f * w, 0.259161f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.157732f * w, 0.593318f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.82568f * w, 0.644559f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.57903f * w, 0.238314f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.255517f * w, 0.647673f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.997295f * w, 0.603427f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.219357f * w, 0.229532f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.618379f * w, 0.700217f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.582996f * w, 0.504535f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.444763f * w, 0.432872f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.690036f * w, 0.578483f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.507633f * w, 0.497474f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.476868f * w, 0.400818f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.758219f * w, 0.6243f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.343033f * w, 0.467284f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.673357f * w, 0.396144f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.627907f * w, 0.65963f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.328658f * w, 0.428021f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.842184f * w, 0.408544f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.378635f * w, 0.684925f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.532367f * w, 0.384428f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.797243f * w, 0.434823f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.228181f * w, 0.698854f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.811726f * w, 0.340408f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.530043f * w, 0.472518f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.339295f * w, 0.700232f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.925392f * w, 0.299509f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.239982f * w, 0.519032f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.659814f * w, 0.688411f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.744272f * w, 0.264953f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.178352f * w, 0.571472f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.942163f * w, 0.663424f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.382491f * w, 0.239552f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.429765f * w, 0.626674f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.939553f * w, 0.626024f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.125524f * w, 0.225599f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.81877f * w, 0.681313f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.565317f * w, 0.506754f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.469428f * w, 0.430486f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.710138f * w, 0.57354f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.6712f * w, 0.642208f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.312072f * w, 0.338032f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.822191f * w, 0.675187f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.430966f * w, 0.319362f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.467933f * w, 0.677496f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.844396f * w, 0.334631f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.223216f * w, 0.644321f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.767859f * w, 0.385127f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.605107f * w, 0.577986f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.268597f * w, 0.46498f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.955094f * w, 0.487681f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.269678f * w, 0.562053f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.581937f * w, 0.38798f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.833007f * w, 0.659965f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.124372f * w, 0.296351f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.931113f * w, 0.740944f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.456425f * w, 0.230063f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.328387f * w, 0.78902f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.598043f * w, 0.475252f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.440121f * w, 0.57887f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.626309f * w, 0.405759f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.626423f * w, 0.597459f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.36265f * w, 0.409357f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.79733f * w, 0.574846f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.415866f * w, 0.448885f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.513265f * w, 0.520724f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.796059f * w, 0.514781f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.24574f * w, 0.446386f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.785066f * w, 0.593801f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.55712f * w, 0.366736f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.320469f * w, 0.669912f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.929104f * w, 0.298269f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.253999f * w, 0.726882f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.63186f * w, 0.256213f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.775842f * w, 0.751208f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.156874f * w, 0.251687f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.942353f * w, 0.734722f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.40618f * w, 0.289464f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.391059f * w, 0.676344f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.975708f * w, 0.366793f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.110314f * w, 0.582626f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.580278f * w, 0.497793f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.587485f * w, 0.491107f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.421731f * w, 0.531833f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.757408f * w, 0.440285f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.41499f * w, 0.589653f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.55124f * w, 0.380766f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.745715f * w, 0.646279f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.277636f * w, 0.331175f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.790925f * w, 0.685175f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.516422f * w, 0.306047f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.37275f * w, 0.694161f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.895541f * w, 0.314774f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.248752f * w, 0.667027f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.67421f * w, 0.360094f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.719322f * w, 0.60466f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.195924f * w, 0.437491f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.943487f * w, 0.515053f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.36406f * w, 0.535802f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.452029f * w, 0.412081f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.929234f * w, 0.639027f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.115967f * w, 0.313183f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.843009f * w, 0.72905f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.597513f * w, 0.236341f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.53071f * w, 0.52407f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.691743f * w, 0.4184f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.43401f * w, 0.604012f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.578784f * w, 0.384212f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.694759f * w, 0.618161f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.318889f * w, 0.388556f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.784295f * w, 0.596014f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.484857f * w, 0.427456f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.423616f * w, 0.542057f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.85545f * w, 0.494081f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.254111f * w, 0.465796f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.707711f * w, 0.576411f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.665185f * w, 0.381355f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.240193f * w, 0.658788f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.93477f * w, 0.305242f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.330992f * w, 0.7246f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.850005f * w, 0.573269f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.245036f * w, 0.350458f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.763677f * w, 0.675619f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.62601f * w, 0.274735f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.275216f * w, 0.713832f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.95964f * w, 0.281128f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.291764f * w, 0.660512f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.578242f * w, 0.378453f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.852369f * w, 0.52724f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.133714f * w, 0.535179f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.594157f * w, 0.481518f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.545608f * w, 0.544853f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.483445f * w, 0.399898f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.759143f * w, 0.607207f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.370472f * w, 0.35885f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.658459f * w, 0.618458f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.666967f * w, 0.382193f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.31417f * w, 0.560228f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.860252f * w, 0.470959f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.405108f * w, 0.449506f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.503936f * w, 0.591757f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.852398f * w, 0.332481f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.208476f * w, 0.690785f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.818157f * w, 0.264366f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.579475f * w, 0.717958f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.290476f * w, 0.283983f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.984596f * w, 0.650737f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.237449f * w, 0.39428f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.635642f * w, 0.506947f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.586732f * w, 0.49452f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.447188f * w, 0.448959f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.715558f * w, 0.567537f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.507209f * w, 0.379534f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.479755f * w, 0.623496f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.794937f * w, 0.348227f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.318334f * w, 0.622416f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.706353f * w, 0.385158f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.642732f * w, 0.550869f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.304207f * w, 0.485457f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.900657f * w, 0.43171f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.351984f * w, 0.610623f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.545148f * w, 0.314907f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.843396f * w, 0.704836f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.179543f * w, 0.255686f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.872327f * w, 0.720017f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.526026f * w, 0.289049f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.316293f * w, 0.638856f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(1.000629f * w, 0.411874f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.544401f * w, 0.489695f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.604977f * w, 0.514938f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.64401f * w, 0.429357f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.386269f * w, 0.587556f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.777305f * w, 0.361613f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.462154f * w, 0.637171f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.494723f * w, 0.340369f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.814078f * w, 0.623602f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.27348f * w, 0.390702f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.757799f * w, 0.539326f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.607565f * w, 0.501567f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.306392f * w, 0.413009f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.933666f * w, 0.629623f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.29828f * w, 0.29796f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.594177f * w, 0.717585f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.823468f * w, 0.248827f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.159194f * w, 0.719943f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.924417f * w, 0.296322f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.467244f * w, 0.624942f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.555475f * w, 0.486073f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.689797f * w, 0.482559f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.418063f * w, 0.530362f * h))
    }
    val p7 = Path().apply { moveTo(0.32f * w, 0.24f * h); cubicTo(0.46f * w, 0.18f * h, 0.64f * w, 0.17f * h, 0.82f * w, 0.23f * h) }
    drawPath(p7, Color(0x73F2DCA6L), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.085f * w, center = Offset(0.78f * w, 0.42f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.054f * w, center = Offset(0.79f * w, 0.426f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.022f * w, center = Offset(0.76f * w, 0.398f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.01f * w, center = Offset(0.8f * w, 0.44f * h))
    val p8 = Path().apply { moveTo(0.7f * w, 0.3f * h); cubicTo(0.76f * w, 0.27f * h, 0.84f * w, 0.28f * h, 0.88f * w, 0.32f * h) }
    drawPath(p8, Color(0x997A481DL), style = Stroke(width = 0.015f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x6BFF8FA3L), radius = 0.045f * w, center = Offset(0.69f * w, 0.54f * h))
    val p9 = Path().apply { moveTo(0.8f * w, 0.55f * h); cubicTo(0.85f * w, 0.61f * h, 0.91f * w, 0.59f * h, 0.93f * w, 0.53f * h) }
    drawPath(p9, Color(0xFF1F3D52L), style = Stroke(width = 0.016f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** MEGALODON — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawMegalodonShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.22f * w, 0.5f * h); cubicTo(0.1f * w, 0.4f * h, 0.02f * w, 0.22f * h, -0.04f * w, 0.1f * h); cubicTo(0.02f * w, 0.3f * h, 0.06f * w, 0.42f * h, 0.1f * w, 0.5f * h); cubicTo(0.05f * w, 0.6f * h, 0f * w, 0.78f * h, -0.05f * w, 0.92f * h); cubicTo(0.06f * w, 0.78f * h, 0.14f * w, 0.62f * h, 0.22f * w, 0.52f * h); close() }
    drawPath(p0, Brush.verticalGradient(0f to Color(0xFF333C45L), 0.5f to Color(0xFF5B6772L), 1f to Color(0xFF333C45L), startY = 0.1f * h, endY = 0.92f * h))
    val p1 = Path().apply { moveTo(0.46f * w, 0.3f * h); cubicTo(0.5f * w, 0.02f * h, 0.6f * w, -0.06f * h, 0.64f * w, 0.04f * h); cubicTo(0.62f * w, 0.1f * h, 0.62f * w, 0.18f * h, 0.66f * w, 0.26f * h); cubicTo(0.58f * w, 0.22f * h, 0.5f * w, 0.24f * h, 0.46f * w, 0.3f * h); close() }
    drawPath(p1, Brush.verticalGradient(0f to Color(0xFF333C45L), 1f to Color(0xFF5B6772L), startY = -0.04f * h, endY = 0.3f * h))
    val p2 = Path().apply { moveTo(0.78f * w, 0.6f * h); cubicTo(0.84f * w, 0.7f * h, 0.92f * w, 0.74f * h, 0.965f * w, 0.74f * h); cubicTo(0.99f * w, 0.74f * h, 0.99f * w, 0.8f * h, 0.95f * w, 0.82f * h); cubicTo(0.86f * w, 0.85f * h, 0.8f * w, 0.78f * h, 0.76f * w, 0.7f * h); cubicTo(0.75f * w, 0.66f * h, 0.75f * w, 0.62f * h, 0.78f * w, 0.6f * h); close() }
    drawPath(p2, Brush.verticalGradient(0f to Color(0xFF5B6772L), 1f to Color(0xFF333C45L), startY = 0.58f * h, endY = 0.84f * h))
    val p3 = Path().apply { moveTo(0.2f * w, 0.48f * h); cubicTo(0.3f * w, 0.22f * h, 0.52f * w, 0.18f * h, 0.7f * w, 0.22f * h); cubicTo(0.84f * w, 0.255f * h, 0.94f * w, 0.31f * h, 0.985f * w, 0.4f * h); cubicTo(0.995f * w, 0.44f * h, 0.99f * w, 0.47f * h, 0.96f * w, 0.49f * h); cubicTo(0.9f * w, 0.51f * h, 0.84f * w, 0.52f * h, 0.78f * w, 0.55f * h); cubicTo(0.74f * w, 0.64f * h, 0.66f * w, 0.74f * h, 0.54f * w, 0.82f * h); cubicTo(0.42f * w, 0.88f * h, 0.3f * w, 0.82f * h, 0.24f * w, 0.66f * h); cubicTo(0.21f * w, 0.58f * h, 0.2f * w, 0.53f * h, 0.2f * w, 0.48f * h); close() }
    drawPath(p3, Brush.verticalGradient(0f to Color(0xFF333C45L), 0.45f to Color(0xFF5B6772L), 1f to Color(0xFFC8D1D7L), startY = 0.18f * h, endY = 0.88f * h))
    clipPath(p3) {
        val p4 = Path().apply { moveTo(0.22f * w, 0.66f * h); cubicTo(0.34f * w, 0.92f * h, 0.62f * w, 0.9f * h, 0.78f * w, 0.76f * h); cubicTo(0.58f * w, 0.82f * h, 0.4f * w, 0.82f * h, 0.22f * w, 0.66f * h); close() }
        drawPath(p4, Color(0xE6C8D1D7L))
        drawCircle(Brush.radialGradient(0f to Color(0x4DFFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.5f * w, 0.3f * h), radius = 0.34f * w), radius = 0.34f * w, center = Offset(0.5f * w, 0.3f * h))
    }
    val p5 = Path().apply { moveTo(0.24f * w, 0.46f * h); cubicTo(0.32f * w, 0.24f * h, 0.5f * w, 0.2f * h, 0.68f * w, 0.235f * h); cubicTo(0.82f * w, 0.265f * h, 0.92f * w, 0.31f * h, 0.965f * w, 0.39f * h) }
    drawPath(p5, Color(0x4DFFFFFFL), style = Stroke(width = 0.022f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p6 = Path().apply { moveTo(0.4f * w, 0.44f * h); cubicTo(0.388f * w, 0.52f * h, 0.388f * w, 0.58f * h, 0.394f * w, 0.64f * h) }
    drawPath(p6, Color(0x4D222A31L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p7 = Path().apply { moveTo(0.445f * w, 0.44f * h); cubicTo(0.433f * w, 0.52f * h, 0.433f * w, 0.58f * h, 0.439f * w, 0.64f * h) }
    drawPath(p7, Color(0x4D222A31L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p8 = Path().apply { moveTo(0.49f * w, 0.44f * h); cubicTo(0.478f * w, 0.52f * h, 0.478f * w, 0.58f * h, 0.484f * w, 0.64f * h) }
    drawPath(p8, Color(0x4D222A31L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p9 = Path().apply { moveTo(0.78f * w, 0.55f * h); cubicTo(0.84f * w, 0.535f * h, 0.9f * w, 0.52f * h, 0.955f * w, 0.495f * h); cubicTo(0.985f * w, 0.55f * h, 0.985f * w, 0.62f * h, 0.95f * w, 0.66f * h); cubicTo(0.88f * w, 0.64f * h, 0.83f * w, 0.62f * h, 0.78f * w, 0.6f * h); close() }
    drawPath(p9, Brush.radialGradient(0f to Color(0xFF222A31L), 1f to Color(0xFF333C45L), center = Offset(0.88f * w, 0.58f * h), radius = 0.14f * w))
    clipPath(p9) {
        val p10 = Path().apply { moveTo(0.78f * w, 0.555f * h); cubicTo(0.86f * w, 0.535f * h, 0.92f * w, 0.52f * h, 0.955f * w, 0.5f * h); cubicTo(0.95f * w, 0.545f * h, 0.88f * w, 0.565f * h, 0.78f * w, 0.585f * h); close() }
        drawPath(p10, Color(0xFFE58A95L))
        val p11 = Path().apply { moveTo(0.8f * w, 0.605f * h); cubicTo(0.86f * w, 0.63f * h, 0.92f * w, 0.635f * h, 0.95f * w, 0.625f * h); cubicTo(0.9f * w, 0.6f * h, 0.84f * w, 0.59f * h, 0.8f * w, 0.585f * h); close() }
        drawPath(p11, Color(0xFFE58A95L))
        val p12 = Path().apply { moveTo(0.774f * w, 0.555f * h); lineTo(0.826f * w, 0.555f * h); lineTo(0.8f * w, 0.605f * h); close() }
        drawPath(p12, Color(0xFFFFFFFFL))
        val p13 = Path().apply { moveTo(0.813725f * w, 0.550757f * h); lineTo(0.863775f * w, 0.550757f * h); lineTo(0.83875f * w, 0.600757f * h); close() }
        drawPath(p13, Color(0xFFFFFFFFL))
        val p14 = Path().apply { moveTo(0.85345f * w, 0.549f * h); lineTo(0.90155f * w, 0.549f * h); lineTo(0.8775f * w, 0.599f * h); close() }
        drawPath(p14, Color(0xFFFFFFFFL))
        val p15 = Path().apply { moveTo(0.893175f * w, 0.550757f * h); lineTo(0.939325f * w, 0.550757f * h); lineTo(0.91625f * w, 0.600757f * h); close() }
        drawPath(p15, Color(0xFFFFFFFFL))
        val p16 = Path().apply { moveTo(0.9329f * w, 0.555f * h); lineTo(0.9771f * w, 0.555f * h); lineTo(0.955f * w, 0.605f * h); close() }
        drawPath(p16, Color(0xFFFFFFFFL))
        val p17 = Path().apply { moveTo(0.791f * w, 0.615f * h); lineTo(0.839f * w, 0.615f * h); lineTo(0.815f * w, 0.571f * h); close() }
        drawPath(p17, Color(0xFFFFFFFFL))
        val p18 = Path().apply { moveTo(0.835293f * w, 0.620196f * h); lineTo(0.881373f * w, 0.620196f * h); lineTo(0.858333f * w, 0.576196f * h); close() }
        drawPath(p18, Color(0xFFFFFFFFL))
        val p19 = Path().apply { moveTo(0.879587f * w, 0.620196f * h); lineTo(0.923747f * w, 0.620196f * h); lineTo(0.901667f * w, 0.576196f * h); close() }
        drawPath(p19, Color(0xFFFFFFFFL))
        val p20 = Path().apply { moveTo(0.92388f * w, 0.615f * h); lineTo(0.96612f * w, 0.615f * h); lineTo(0.945f * w, 0.571f * h); close() }
        drawPath(p20, Color(0xFFFFFFFFL))
    }
    drawCircle(Color(0xFFFFFFFFL), radius = 0.072f * w, center = Offset(0.66f * w, 0.4f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.044f * w, center = Offset(0.668f * w, 0.406f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.02f * w, center = Offset(0.644f * w, 0.382f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.009f * w, center = Offset(0.676f * w, 0.416f * h))
    val p21 = Path().apply { moveTo(0.6f * w, 0.3f * h); cubicTo(0.64f * w, 0.27f * h, 0.7f * w, 0.275f * h, 0.74f * w, 0.31f * h) }
    drawPath(p21, Color(0xFF333C45L), style = Stroke(width = 0.018f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x66FF8FA3L), radius = 0.045f * w, center = Offset(0.56f * w, 0.52f * h))
}


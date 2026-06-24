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
    val p0 = Path().apply { moveTo(0.82f * w, 0.48f * h); cubicTo(0.93f * w, 0.4f * h, 0.99f * w, 0.44f * h, 0.98f * w, 0.52f * h); cubicTo(0.99f * w, 0.6f * h, 0.93f * w, 0.64f * h, 0.82f * w, 0.56f * h); close() }
    drawPath(p0, Color(0xFFD98794L))
    val p1 = Path().apply { moveTo(0.24f * w, 0.62f * h); cubicTo(0.08f * w, 0.66f * h, 0.05f * w, 0.79f * h, 0.15f * w, 0.84f * h); cubicTo(0.21f * w, 0.8f * h, 0.27f * w, 0.72f * h, 0.31f * w, 0.66f * h); close() }
    drawPath(p1, Color(0xFFD98794L))
    val p2 = Path().apply { moveTo(0.76f * w, 0.62f * h); cubicTo(0.92f * w, 0.66f * h, 0.95f * w, 0.79f * h, 0.85f * w, 0.84f * h); cubicTo(0.79f * w, 0.8f * h, 0.73f * w, 0.72f * h, 0.69f * w, 0.66f * h); close() }
    drawPath(p2, Color(0xFFD98794L))
    val p3 = Path().apply { moveTo(0.5f * w, 0.12f * h); cubicTo(0.67f * w, 0.12f * h, 0.81f * w, 0.22f * h, 0.83f * w, 0.4f * h); cubicTo(0.85f * w, 0.58f * h, 0.79f * w, 0.82f * h, 0.58f * w, 0.88f * h); cubicTo(0.52f * w, 0.9f * h, 0.48f * w, 0.9f * h, 0.42f * w, 0.88f * h); cubicTo(0.21f * w, 0.82f * h, 0.15f * w, 0.58f * h, 0.17f * w, 0.4f * h); cubicTo(0.19f * w, 0.22f * h, 0.33f * w, 0.12f * h, 0.5f * w, 0.12f * h); close() }
    drawPath(p3, Brush.verticalGradient(0f to Color(0xFFF9CBD0L), 0.45f to Color(0xFFF2A7AFL), 1f to Color(0xFFD98794L), startY = 0.1f * h, endY = 0.9f * h))
    drawCircle(Brush.radialGradient(0f to Color(0x73FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.42f * w, 0.24f * h), radius = 0.36f * w), radius = 0.36f * w, center = Offset(0.42f * w, 0.24f * h))
    drawPath(p3, Color(0x73BF6678L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p4 = Path().apply { moveTo(0.26f * w, 0.37f * h); cubicTo(0.4f * w, 0.29f * h, 0.6f * w, 0.29f * h, 0.74f * w, 0.37f * h); cubicTo(0.72f * w, 0.45f * h, 0.62f * w, 0.46f * h, 0.5f * w, 0.45f * h); cubicTo(0.38f * w, 0.46f * h, 0.28f * w, 0.45f * h, 0.26f * w, 0.37f * h); close() }
    drawPath(p4, Color(0x66D98794L))
    drawCircle(Color(0x73E98C99L), radius = 0.05f * w, center = Offset(0.28f * w, 0.62f * h))
    drawCircle(Color(0x73E98C99L), radius = 0.05f * w, center = Offset(0.72f * w, 0.62f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.046f * w, center = Offset(0.4f * w, 0.44f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.015f * w, center = Offset(0.387f * w, 0.426f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.046f * w, center = Offset(0.6f * w, 0.44f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.015f * w, center = Offset(0.587f * w, 0.426f * h))
    val p5 = Path().apply { moveTo(0.4f * w, 0.49f * h); cubicTo(0.42f * w, 0.43f * h, 0.58f * w, 0.43f * h, 0.6f * w, 0.49f * h); cubicTo(0.69f * w, 0.57f * h, 0.65f * w, 0.73f * h, 0.5f * w, 0.75f * h); cubicTo(0.35f * w, 0.73f * h, 0.31f * w, 0.57f * h, 0.4f * w, 0.49f * h); close() }
    drawPath(p5, Brush.verticalGradient(0f to Color(0xFFFBD7DCL), 0.55f to Color(0xFFF2A7AFL), 1f to Color(0xFFD98794L), startY = 0.45f * h, endY = 0.75f * h))
    drawCircle(Color(0x8CFFFFFFL), radius = 0.065f * w, center = Offset(0.46f * w, 0.53f * h))
    val p6 = Path().apply { moveTo(0.37f * w, 0.66f * h); cubicTo(0.44f * w, 0.75f * h, 0.56f * w, 0.75f * h, 0.63f * w, 0.66f * h); cubicTo(0.56f * w, 0.71f * h, 0.44f * w, 0.71f * h, 0.37f * w, 0.66f * h); close() }
    drawPath(p6, Color(0x73D98794L))
    drawCircle(Color(0xA6BF6678L), radius = 0.016f * w, center = Offset(0.45f * w, 0.67f * h))
    drawCircle(Color(0xA6BF6678L), radius = 0.016f * w, center = Offset(0.55f * w, 0.67f * h))
    val p7 = Path().apply { moveTo(0.33f * w, 0.78f * h); cubicTo(0.42f * w, 0.745f * h, 0.58f * w, 0.745f * h, 0.67f * w, 0.78f * h); cubicTo(0.62f * w, 0.9f * h, 0.38f * w, 0.9f * h, 0.33f * w, 0.78f * h); close() }
    drawPath(p7, Brush.verticalGradient(0f to Color(0xFFD86F80L), 1f to Color(0xFFEC9CA8L), startY = 0.745f * h, endY = 0.9f * h))
    drawCircle(Color(0x47FFFFFFL), radius = 0.055f * w, center = Offset(0.5f * w, 0.84f * h))
    val p8 = Path().apply { moveTo(0.33f * w, 0.78f * h); cubicTo(0.42f * w, 0.745f * h, 0.58f * w, 0.745f * h, 0.67f * w, 0.78f * h) }
    drawPath(p8, Color(0x991F3D52L), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
    val p0 = Path().apply { moveTo(0.03f * w, 0.82f * h); cubicTo(0.16f * w, 0.74f * h, 0.27f * w, 0.82f * h, 0.27f * w, 0.93f * h); cubicTo(0.16f * w, 0.98f * h, 0.06f * w, 0.96f * h, 0.03f * w, 0.89f * h); close() }
    drawPath(p0, Brush.verticalGradient(0f to Color(0xFFFFF1A0L), 1f to Color(0xFFFFE45EL), startY = 0.74f * h, endY = 0.98f * h))
    val p1 = Path().apply { moveTo(0.03f * w, 0.82f * h); cubicTo(0.16f * w, 0.74f * h, 0.27f * w, 0.82f * h, 0.27f * w, 0.93f * h) }
    drawPath(p1, Color(0xFFE8B23AL), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p2 = Path().apply { moveTo(0.08f * w, 0.84f * h); cubicTo(0.16f * w, 0.8f * h, 0.23f * w, 0.84f * h, 0.23f * w, 0.91f * h) }
    drawPath(p2, Color(0xB3FFFFFFL), style = Stroke(width = 0.012f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p3 = Path().apply { moveTo(0.05f * w, 0.33f * h); cubicTo(0.13f * w, 0.4f * h, 0.19f * w, 0.42f * h, 0.24f * w, 0.44f * h); cubicTo(0.27f * w, 0.34f * h, 0.31f * w, 0.28f * h, 0.37f * w, 0.26f * h); cubicTo(0.41f * w, 0.13f * h, 0.51f * w, 0.12f * h, 0.55f * w, 0.22f * h); cubicTo(0.62f * w, 0.18f * h, 0.72f * w, 0.18f * h, 0.81f * w, 0.24f * h); cubicTo(0.91f * w, 0.3f * h, 0.975f * w, 0.38f * h, 0.965f * w, 0.47f * h); cubicTo(0.99f * w, 0.51f * h, 0.96f * w, 0.57f * h, 0.91f * w, 0.61f * h); cubicTo(0.86f * w, 0.67f * h, 0.78f * w, 0.73f * h, 0.64f * w, 0.77f * h); cubicTo(0.59f * w, 0.87f * h, 0.47f * w, 0.88f * h, 0.43f * w, 0.78f * h); cubicTo(0.37f * w, 0.8f * h, 0.3f * w, 0.78f * h, 0.24f * w, 0.72f * h); cubicTo(0.19f * w, 0.74f * h, 0.13f * w, 0.76f * h, 0.05f * w, 0.83f * h); cubicTo(0.11f * w, 0.66f * h, 0.12f * w, 0.58f * h, 0.1f * w, 0.5f * h); cubicTo(0.12f * w, 0.42f * h, 0.11f * w, 0.38f * h, 0.05f * w, 0.33f * h); close() }
    drawPath(p3, Brush.radialGradient(0f to Color(0xFFE8C079L), 0.45f to Color(0xFFCB8638L), 0.8f to Color(0xFFCB8638L), 1f to Color(0xFF9A5C24L), center = Offset(0.55f * w, 0.42f * h), radius = 0.68f * w))
    drawPath(p3, Brush.verticalGradient(0f to Color(0x579A5C24L), 0.4f to Color(0x009A5C24L), 0.7f to Color(0x009A5C24L), 1f to Color(0x527A481DL), startY = 0.14f * h, endY = 0.86f * h))
    clipPath(p3) {
        drawCircle(Brush.radialGradient(0f to Color(0x80F2DCA6L), 1f to Color(0x00F2DCA6L), center = Offset(0.54f * w, 0.36f * h), radius = 0.38f * w), radius = 0.38f * w, center = Offset(0.54f * w, 0.36f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.57f * w, 0.5f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.416929f * w, 0.423466f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.535417f * w, 0.608247f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.64514f * w, 0.472099f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.293878f * w, 0.3944f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.731508f * w, 0.662906f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.45017f * w, 0.428273f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.387154f * w, 0.398556f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.80569f * w, 0.700557f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.224936f * w, 0.377263f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.661063f * w, 0.423031f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.624699f * w, 0.723214f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.207231f * w, 0.324364f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.884899f * w, 0.463245f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.298716f * w, 0.730163f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.467912f * w, 0.274142f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.83359f * w, 0.515549f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.100143f * w, 0.720771f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.824611f * w, 0.230695f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.500977f * w, 0.576239f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.229519f * w, 0.695053f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.977614f * w, 0.19761f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.134171f * w, 0.641376f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.623739f * w, 0.653831f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.765253f * w, 0.177845f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.043979f * w, 0.706851f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.980164f * w, 0.598772f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.322652f * w, 0.173612f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.339991f * w, 0.768524f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.993509f * w, 0.532333f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(-0.003289f * w, 0.186269f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.546829f * w, 0.52931f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.544399f * w, 0.489393f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.379478f * w, 0.411245f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.724744f * w, 0.630733f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.365045f * w, 0.451991f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.51975f * w, 0.399062f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.702594f * w, 0.677826f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.233473f * w, 0.404123f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.760979f * w, 0.412975f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.464884f * w, 0.708627f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.340232f * w, 0.351912f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.856094f * w, 0.445098f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.199912f * w, 0.723773f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.647415f * w, 0.300222f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.668165f * w, 0.491255f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.159253f * w, 0.722682f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.91011f * w, 0.253375f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.310352f * w, 0.547697f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.426143f * w, 0.705113f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.881958f * w, 0.215214f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.072338f * w, 0.610549f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.816154f * w, 0.671537f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.541246f * w, 0.189005f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.179601f * w, 0.675744f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(1.009507f * w, 0.623246f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.138232f * w, 0.177323f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.585499f * w, 0.739128f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.817006f * w, 0.562345f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.007073f * w, 0.181932f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.981669f * w, 0.796617f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.357931f * w, 0.491678f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.498791f * w, 0.473064f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.649254f * w, 0.586282f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.353782f * w, 0.47332f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.61458f * w, 0.407756f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.579832f * w, 0.649721f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.308223f * w, 0.429857f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.78578f * w, 0.408303f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.34536f * w, 0.689556f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.492917f * w, 0.37942f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.755917f * w, 0.431006f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.18602f * w, 0.713063f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.77598f * w, 0.327241f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.489267f * w, 0.469987f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.291836f * w, 0.720353f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.900287f * w, 0.277874f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.184952f * w, 0.521211f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.625057f * w, 0.711101f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.714873f * w, 0.235395f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.114666f * w, 0.580805f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.927458f * w, 0.685482f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.330072f * w, 0.203352f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.380204f * w, 0.644747f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.928629f * w, 0.644431f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.050962f * w, 0.184659f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.800061f * w, 0.708891f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.586327f * w, 0.589693f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.130127f * w, 0.181467f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(1.036079f * w, 0.769092f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.149562f * w, 0.523783f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.541931f * w, 0.195073f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.869364f * w, 0.82138f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.470411f * w, 0.495445f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.63431f * w, 0.431313f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.482145f * w, 0.613274f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.416755f * w, 0.45404f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.739163f * w, 0.410414f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.292982f * w, 0.6654f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.622628f * w, 0.406445f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.619112f * w, 0.421673f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.249957f * w, 0.697839f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.828289f * w, 0.354779f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.341357f * w, 0.452281f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.458611f * w, 0.713669f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.807107f * w, 0.30381f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.147732f * w, 0.497271f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.77994f * w, 0.712881f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.521191f * w, 0.257824f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.242631f * w, 0.552621f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.006f * w, center = Offset(0.938776f * w, 0.695478f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.01f * w, center = Offset(0.178873f * w, 0.220627f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.014f * w, center = Offset(0.595305f * w, 0.614326f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.008f * w, center = Offset(0.763842f * w, 0.662073f * h))
        drawCircle(Color(0x8C7A481DL), radius = 0.012f * w, center = Offset(0.073823f * w, 0.19544f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.652655f * w, 0.680591f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.22444f * w, 0.294457f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.832391f * w, 0.722195f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.36633f * w, 0.270995f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.410397f * w, 0.724936f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.858454f * w, 0.2905f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.119314f * w, 0.682782f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.767236f * w, 0.35455f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.573641f * w, 0.598722f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.173541f * w, 0.455677f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.989654f * w, 0.484412f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.174947f * w, 0.578509f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.546069f * w, 0.358295f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.84438f * w, 0.702329f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.002466f * w, 0.242452f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.960835f * w, 0.804677f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.39698f * w, 0.1587f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.244933f * w, 0.86539f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.567554f * w, 0.46707f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.375843f * w, 0.600962f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.59943f * w, 0.379827f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.599411f * w, 0.624032f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.28436f * w, 0.384788f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.803122f * w, 0.595047f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.348175f * w, 0.435134f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.464317f * w, 0.526286f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.801128f * w, 0.518739f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.145883f * w, 0.432057f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.787846f * w, 0.618832f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.516574f * w, 0.331221f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.235092f * w, 0.715139f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.958928f * w, 0.244629f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.156185f * w, 0.787151f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.605421f * w, 0.19151f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.776532f * w, 0.817828f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.04095f * w, 0.185883f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.974302f * w, 0.796883f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.337259f * w, 0.233742f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.319319f * w, 0.722989f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(1.013754f * w, 0.331577f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(-0.014054f * w, 0.604458f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.545347f * w, 0.497064f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.553051f * w, 0.488616f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.35437f * w, 0.540592f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.756007f * w, 0.424003f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.346855f * w, 0.613954f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.50955f * w, 0.348583f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.741395f * w, 0.685633f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.183574f * w, 0.285873f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.795015f * w, 0.734757f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.468121f * w, 0.254209f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.297157f * w, 0.745974f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.919207f * w, 0.265412f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.149789f * w, 0.711486f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.655816f * w, 0.322894f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.709424f * w, 0.632461f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.087216f * w, 0.4209f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.975781f * w, 0.519045f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.287157f * w, 0.545289f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.391712f * w, 0.388797f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.958657f * w, 0.675822f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(-0.007456f * w, 0.263768f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.856141f * w, 0.789604f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.564551f * w, 0.166672f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.483387f * w, 0.532029f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.678423f * w, 0.395544f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.369078f * w, 0.632632f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.542457f * w, 0.352641f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.680905f * w, 0.65019f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.232368f * w, 0.358477f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.787386f * w, 0.621845f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.43047f * w, 0.40799f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.357578f * w, 0.553318f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.871724f * w, 0.492499f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.155967f * w, 0.456668f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.695738f * w, 0.596774f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.645112f * w, 0.349775f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.139694f * w, 0.701009f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.965577f * w, 0.253507f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.247775f * w, 0.784211f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.460198f * w, 0.188018f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.898547f * w, 0.828018f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.010325f * w, 0.168984f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.892081f * w, 0.820225f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.493013f * w, 0.204501f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.171336f * w, 0.757315f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(1.073284f * w, 0.293229f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.47885f * w, 0.512767f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.55533f * w, 0.479322f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.620487f * w, 0.500118f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.293687f * w, 0.530766f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.762597f * w, 0.432146f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.406298f * w, 0.607977f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.414018f * w, 0.351766f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.817782f * w, 0.685885f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.174878f * w, 0.281608f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.006f * w, center = Offset(0.723695f * w, 0.743489f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.01275f * w, center = Offset(0.585714f * w, 0.240725f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.0105f * w, center = Offset(0.196648f * w, 0.764292f * h))
        drawCircle(Color(0x6B9A5C24L), radius = 0.00825f * w, center = Offset(0.944214f * w, 0.242404f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.8671f * w, 0.596932f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.138977f * w, 0.310898f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.762943f * w, 0.728134f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.597339f * w, 0.213943f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.175719f * w, 0.776942f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.998133f * w, 0.22232f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.195809f * w, 0.708456f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.539896f * w, 0.347125f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.868985f * w, 0.537687f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.006351f * w, 0.54784f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.562209f * w, 0.477937f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.500135f * w, 0.561639f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.424728f * w, 0.373108f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.759287f * w, 0.641555f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.288628f * w, 0.320769f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.636853f * w, 0.655514f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.646999f * w, 0.35125f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.221593f * w, 0.580305f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.87965f * w, 0.465533f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.331486f * w, 0.437999f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.450507f * w, 0.620605f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.869651f * w, 0.287916f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.095355f * w, 0.747488f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.828243f * w, 0.200748f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.541384f * w, 0.782139f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.194262f * w, 0.226055f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(1.027844f * w, 0.695879f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.13077f * w, 0.367426f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.608786f * w, 0.511696f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.552309f * w, 0.496428f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.37963f * w, 0.436399f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.707035f * w, 0.590592f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.453882f * w, 0.347161f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.420829f * w, 0.662259f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.801709f * w, 0.30733f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.226344f * w, 0.660423f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.694376f * w, 0.355185f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.617617f * w, 0.568214f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.210009f * w, 0.484166f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.927874f * w, 0.415185f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.267784f * w, 0.644749f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.500122f * w, 0.265459f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.858576f * w, 0.76541f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.060886f * w, 0.189715f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.893127f * w, 0.784694f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.477196f * w, 0.232613f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.225423f * w, 0.680616f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(1.046859f * w, 0.389979f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.495868f * w, 0.489566f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.572825f * w, 0.522569f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.620014f * w, 0.411325f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.307273f * w, 0.616146f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.780781f * w, 0.324334f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.399729f * w, 0.679625f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.439172f * w, 0.297421f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.82424f * w, 0.661797f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.172799f * w, 0.362406f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.756093f * w, 0.553342f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.575202f * w, 0.504846f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.212949f * w, 0.391227f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.96722f * w, 0.669054f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.203438f * w, 0.243813f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.559048f * w, 0.781661f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.834441f * w, 0.181014f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.036707f * w, 0.784521f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.95548f * w, 0.241987f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.406668f * w, 0.66276f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.510633f * w, 0.484415f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.676679f * w, 0.480282f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.345207f * w, 0.542364f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.625314f * w, 0.387591f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.617557f * w, 0.639822f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.269518f * w, 0.30401f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.841485f * w, 0.69398f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.340264f * w, 0.290815f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.473169f * w, 0.659848f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.830505f * w, 0.372686f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.12823f * w, 0.535951f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.819542f * w, 0.52728f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.522196f * w, 0.366432f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.229569f * w, 0.693216f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.008f * w, center = Offset(0.996611f * w, 0.22325f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.01f * w, center = Offset(0.140634f * w, 0.796023f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.012f * w, center = Offset(0.625319f * w, 0.174792f * h))
        drawCircle(Color(0xB3F2DCA6L), radius = 0.006f * w, center = Offset(0.797907f * w, 0.78156f * h))
    }
    val p4 = Path().apply { moveTo(0.3f * w, 0.26f * h); cubicTo(0.48f * w, 0.16f * h, 0.66f * w, 0.17f * h, 0.84f * w, 0.25f * h) }
    drawPath(p4, Color(0x73F2DCA6L), style = Stroke(width = 0.02f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.078f * w, center = Offset(0.8f * w, 0.42f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.05f * w, center = Offset(0.81f * w, 0.426f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.02f * w, center = Offset(0.782f * w, 0.4f * h))
    drawCircle(Color(0xD9FFFFFFL), radius = 0.009f * w, center = Offset(0.818f * w, 0.438f * h))
    val p5 = Path().apply { moveTo(0.72f * w, 0.3f * h); cubicTo(0.78f * w, 0.27f * h, 0.86f * w, 0.28f * h, 0.9f * w, 0.32f * h) }
    drawPath(p5, Color(0x997A481DL), style = Stroke(width = 0.015f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Color(0x6BFF8FA3L), radius = 0.042f * w, center = Offset(0.71f * w, 0.54f * h))
    val p6 = Path().apply { moveTo(0.82f * w, 0.56f * h); cubicTo(0.87f * w, 0.62f * h, 0.92f * w, 0.6f * h, 0.94f * w, 0.54f * h) }
    drawPath(p6, Color(0xFF1F3D52L), style = Stroke(width = 0.016f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** MEGALODON — feste Farben. Generiert aus verifizierter Vorschau. */
internal fun DrawScope.drawMegalodonShape() {
    val w = size.width
    val h = size.height
    val p0 = Path().apply { moveTo(0.24f * w, 0.5f * h); cubicTo(0.1f * w, 0.38f * h, 0f * w, 0.15f * h, -0.07f * w, 0.03f * h); cubicTo(0f * w, 0.26f * h, 0.06f * w, 0.42f * h, 0.12f * w, 0.5f * h); cubicTo(0.06f * w, 0.58f * h, 0f * w, 0.8f * h, -0.06f * w, 0.97f * h); cubicTo(0.06f * w, 0.8f * h, 0.16f * w, 0.62f * h, 0.24f * w, 0.52f * h); close() }
    drawPath(p0, Brush.verticalGradient(0f to Color(0xFF2E3741L), 0.5f to Color(0xFF586573L), 1f to Color(0xFF2E3741L), startY = 0.03f * h, endY = 0.97f * h))
    val p1 = Path().apply { moveTo(0.4f * w, 0.28f * h); cubicTo(0.46f * w, -0.02f * h, 0.58f * w, -0.1f * h, 0.66f * w, 0f * h); cubicTo(0.6f * w, 0.06f * h, 0.6f * w, 0.15f * h, 0.62f * w, 0.25f * h); cubicTo(0.54f * w, 0.21f * h, 0.46f * w, 0.22f * h, 0.4f * w, 0.28f * h); close() }
    drawPath(p1, Brush.verticalGradient(0f to Color(0xFF2E3741L), 1f to Color(0xFF586573L), startY = -0.1f * h, endY = 0.28f * h))
    val p2 = Path().apply { moveTo(0.48f * w, 0.7f * h); cubicTo(0.42f * w, 0.95f * h, 0.3f * w, 1.04f * h, 0.26f * w, 0.92f * h); cubicTo(0.3f * w, 0.82f * h, 0.4f * w, 0.74f * h, 0.48f * w, 0.66f * h); close() }
    drawPath(p2, Brush.verticalGradient(0f to Color(0xFF586573L), 1f to Color(0xFF2E3741L), startY = 0.66f * h, endY = 1.02f * h))
    val p3 = Path().apply { moveTo(0.55f * w, 0.56f * h); cubicTo(0.66f * w, 0.76f * h, 0.82f * w, 0.86f * h, 0.95f * w, 0.85f * h); cubicTo(0.99f * w, 0.85f * h, 1f * w, 0.93f * h, 0.95f * w, 0.95f * h); cubicTo(0.78f * w, 0.98f * h, 0.6f * w, 0.9f * h, 0.5f * w, 0.72f * h); cubicTo(0.48f * w, 0.65f * h, 0.5f * w, 0.59f * h, 0.55f * w, 0.56f * h); close() }
    drawPath(p3, Brush.verticalGradient(0f to Color(0xFF586573L), 1f to Color(0xFF2E3741L), startY = 0.56f * h, endY = 0.95f * h))
    val p4 = Path().apply { moveTo(0.18f * w, 0.46f * h); cubicTo(0.28f * w, 0.15f * h, 0.5f * w, 0.09f * h, 0.7f * w, 0.15f * h); cubicTo(0.86f * w, 0.2f * h, 0.96f * w, 0.27f * h, 0.995f * w, 0.37f * h); cubicTo(1f * w, 0.4f * h, 0.995f * w, 0.43f * h, 0.97f * w, 0.445f * h); cubicTo(0.86f * w, 0.46f * h, 0.7f * w, 0.48f * h, 0.55f * w, 0.53f * h); cubicTo(0.55f * w, 0.65f * h, 0.53f * w, 0.78f * h, 0.45f * w, 0.86f * h); cubicTo(0.35f * w, 0.92f * h, 0.26f * w, 0.84f * h, 0.2f * w, 0.66f * h); cubicTo(0.17f * w, 0.58f * h, 0.16f * w, 0.52f * h, 0.18f * w, 0.46f * h); close() }
    drawPath(p4, Brush.verticalGradient(0f to Color(0xFF2E3741L), 0.4f to Color(0xFF586573L), 1f to Color(0xFFC4CED6L), startY = 0.12f * h, endY = 0.88f * h))
    clipPath(p4) {
        val p5 = Path().apply { moveTo(0.2f * w, 0.64f * h); cubicTo(0.32f * w, 0.92f * h, 0.5f * w, 0.9f * h, 0.58f * w, 0.74f * h); cubicTo(0.42f * w, 0.8f * h, 0.3f * w, 0.78f * h, 0.2f * w, 0.64f * h); close() }
        drawPath(p5, Color(0xE6C4CED6L))
        drawCircle(Brush.radialGradient(0f to Color(0x47FFFFFFL), 1f to Color(0x00FFFFFFL), center = Offset(0.5f * w, 0.24f * h), radius = 0.38f * w), radius = 0.38f * w, center = Offset(0.5f * w, 0.24f * h))
    }
    val p6 = Path().apply { moveTo(0.22f * w, 0.44f * h); cubicTo(0.3f * w, 0.17f * h, 0.5f * w, 0.12f * h, 0.7f * w, 0.17f * h); cubicTo(0.86f * w, 0.22f * h, 0.95f * w, 0.28f * h, 0.985f * w, 0.36f * h) }
    drawPath(p6, Color(0x57FFFFFFL), style = Stroke(width = 0.022f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p7 = Path().apply { moveTo(0.32f * w, 0.4f * h); cubicTo(0.306f * w, 0.5f * h, 0.306f * w, 0.58f * h, 0.314f * w, 0.66f * h) }
    drawPath(p7, Color(0x66161D23L), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p8 = Path().apply { moveTo(0.356f * w, 0.4f * h); cubicTo(0.342f * w, 0.5f * h, 0.342f * w, 0.58f * h, 0.35f * w, 0.66f * h) }
    drawPath(p8, Color(0x66161D23L), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p9 = Path().apply { moveTo(0.392f * w, 0.4f * h); cubicTo(0.378f * w, 0.5f * h, 0.378f * w, 0.58f * h, 0.386f * w, 0.66f * h) }
    drawPath(p9, Color(0x66161D23L), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p10 = Path().apply { moveTo(0.428f * w, 0.4f * h); cubicTo(0.414f * w, 0.5f * h, 0.414f * w, 0.58f * h, 0.422f * w, 0.66f * h) }
    drawPath(p10, Color(0x66161D23L), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p11 = Path().apply { moveTo(0.464f * w, 0.4f * h); cubicTo(0.45f * w, 0.5f * h, 0.45f * w, 0.58f * h, 0.458f * w, 0.66f * h) }
    drawPath(p11, Color(0x66161D23L), style = Stroke(width = 0.013f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p12 = Path().apply { moveTo(0.34f * w, 0.28f * h); lineTo(0.43f * w, 0.42f * h) }
    drawPath(p12, Color(0x80C4CED6L), style = Stroke(width = 0.011f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p13 = Path().apply { moveTo(0.29f * w, 0.4f * h); lineTo(0.34f * w, 0.5f * h) }
    drawPath(p13, Color(0x66C4CED6L), style = Stroke(width = 0.009f * w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    val p14 = Path().apply { moveTo(0.55f * w, 0.53f * h); cubicTo(0.7f * w, 0.48f * h, 0.86f * w, 0.46f * h, 0.965f * w, 0.45f * h); cubicTo(0.99f * w, 0.53f * h, 0.97f * w, 0.64f * h, 0.91f * w, 0.72f * h); cubicTo(0.78f * w, 0.72f * h, 0.64f * w, 0.66f * h, 0.55f * w, 0.57f * h); close() }
    drawPath(p14, Brush.radialGradient(0f to Color(0xFF161D23L), 0.7f to Color(0xFF2E3741L), 1f to Color(0xFF2E3741L), center = Offset(0.78f * w, 0.58f * h), radius = 0.24f * w))
    clipPath(p14) {
        val p15 = Path().apply { moveTo(0.55f * w, 0.535f * h); cubicTo(0.72f * w, 0.485f * h, 0.86f * w, 0.465f * h, 0.965f * w, 0.45f * h); cubicTo(0.95f * w, 0.5f * h, 0.8f * w, 0.54f * h, 0.57f * w, 0.575f * h); close() }
        drawPath(p15, Color(0xFFB85563L))
        val p16 = Path().apply { moveTo(0.58f * w, 0.6f * h); cubicTo(0.72f * w, 0.66f * h, 0.84f * w, 0.695f * h, 0.91f * w, 0.7f * h); cubicTo(0.83f * w, 0.63f * h, 0.7f * w, 0.595f * h, 0.58f * w, 0.58f * h); close() }
        drawPath(p16, Color(0xFFB85563L))
        val p17 = Path().apply { moveTo(0.545f * w, 0.54f * h); lineTo(0.605f * w, 0.54f * h); lineTo(0.575f * w, 0.622f * h); close() }
        drawPath(p17, Color(0xFFFFFFFFL))
        val p18 = Path().apply { moveTo(0.575f * w, 0.54f * h); lineTo(0.605f * w, 0.54f * h); lineTo(0.575f * w, 0.622f * h); close() }
        drawPath(p18, Color(0x8CCBD3DAL))
        val p19 = Path().apply { moveTo(0.600429f * w, 0.534793f * h); lineTo(0.659571f * w, 0.534793f * h); lineTo(0.63f * w, 0.616793f * h); close() }
        drawPath(p19, Color(0xFFFFFFFFL))
        val p20 = Path().apply { moveTo(0.63f * w, 0.534793f * h); lineTo(0.659571f * w, 0.534793f * h); lineTo(0.63f * w, 0.616793f * h); close() }
        drawPath(p20, Color(0x8CCBD3DAL))
        val p21 = Path().apply { moveTo(0.655857f * w, 0.530618f * h); lineTo(0.714143f * w, 0.530618f * h); lineTo(0.685f * w, 0.612618f * h); close() }
        drawPath(p21, Color(0xFFFFFFFFL))
        val p22 = Path().apply { moveTo(0.685f * w, 0.530618f * h); lineTo(0.714143f * w, 0.530618f * h); lineTo(0.685f * w, 0.612618f * h); close() }
        drawPath(p22, Color(0x8CCBD3DAL))
        val p23 = Path().apply { moveTo(0.711286f * w, 0.528301f * h); lineTo(0.768714f * w, 0.528301f * h); lineTo(0.74f * w, 0.610301f * h); close() }
        drawPath(p23, Color(0xFFFFFFFFL))
        val p24 = Path().apply { moveTo(0.74f * w, 0.528301f * h); lineTo(0.768714f * w, 0.528301f * h); lineTo(0.74f * w, 0.610301f * h); close() }
        drawPath(p24, Color(0x8CCBD3DAL))
        val p25 = Path().apply { moveTo(0.766714f * w, 0.528301f * h); lineTo(0.823286f * w, 0.528301f * h); lineTo(0.795f * w, 0.610301f * h); close() }
        drawPath(p25, Color(0xFFFFFFFFL))
        val p26 = Path().apply { moveTo(0.795f * w, 0.528301f * h); lineTo(0.823286f * w, 0.528301f * h); lineTo(0.795f * w, 0.610301f * h); close() }
        drawPath(p26, Color(0x8CCBD3DAL))
        val p27 = Path().apply { moveTo(0.822143f * w, 0.530618f * h); lineTo(0.877857f * w, 0.530618f * h); lineTo(0.85f * w, 0.612618f * h); close() }
        drawPath(p27, Color(0xFFFFFFFFL))
        val p28 = Path().apply { moveTo(0.85f * w, 0.530618f * h); lineTo(0.877857f * w, 0.530618f * h); lineTo(0.85f * w, 0.612618f * h); close() }
        drawPath(p28, Color(0x8CCBD3DAL))
        val p29 = Path().apply { moveTo(0.877571f * w, 0.534793f * h); lineTo(0.932429f * w, 0.534793f * h); lineTo(0.905f * w, 0.616793f * h); close() }
        drawPath(p29, Color(0xFFFFFFFFL))
        val p30 = Path().apply { moveTo(0.905f * w, 0.534793f * h); lineTo(0.932429f * w, 0.534793f * h); lineTo(0.905f * w, 0.616793f * h); close() }
        drawPath(p30, Color(0x8CCBD3DAL))
        val p31 = Path().apply { moveTo(0.933f * w, 0.54f * h); lineTo(0.987f * w, 0.54f * h); lineTo(0.96f * w, 0.622f * h); close() }
        drawPath(p31, Color(0xFFFFFFFFL))
        val p32 = Path().apply { moveTo(0.96f * w, 0.54f * h); lineTo(0.987f * w, 0.54f * h); lineTo(0.96f * w, 0.622f * h); close() }
        drawPath(p32, Color(0x8CCBD3DAL))
        val p33 = Path().apply { moveTo(0.583f * w, 0.605f * h); lineTo(0.637f * w, 0.605f * h); lineTo(0.61f * w, 0.535f * h); close() }
        drawPath(p33, Color(0xFFFFFFFFL))
        val p34 = Path().apply { moveTo(0.61f * w, 0.605f * h); lineTo(0.637f * w, 0.605f * h); lineTo(0.61f * w, 0.535f * h); close() }
        drawPath(p34, Color(0x8CCBD3DAL))
        val p35 = Path().apply { moveTo(0.64554f * w, 0.613229f * h); lineTo(0.69846f * w, 0.613229f * h); lineTo(0.672f * w, 0.543229f * h); close() }
        drawPath(p35, Color(0xFFFFFFFFL))
        val p36 = Path().apply { moveTo(0.672f * w, 0.613229f * h); lineTo(0.69846f * w, 0.613229f * h); lineTo(0.672f * w, 0.543229f * h); close() }
        drawPath(p36, Color(0x8CCBD3DAL))
        val p37 = Path().apply { moveTo(0.70808f * w, 0.618315f * h); lineTo(0.75992f * w, 0.618315f * h); lineTo(0.734f * w, 0.548315f * h); close() }
        drawPath(p37, Color(0xFFFFFFFFL))
        val p38 = Path().apply { moveTo(0.734f * w, 0.618315f * h); lineTo(0.75992f * w, 0.618315f * h); lineTo(0.734f * w, 0.548315f * h); close() }
        drawPath(p38, Color(0x8CCBD3DAL))
        val p39 = Path().apply { moveTo(0.77062f * w, 0.618315f * h); lineTo(0.82138f * w, 0.618315f * h); lineTo(0.796f * w, 0.548315f * h); close() }
        drawPath(p39, Color(0xFFFFFFFFL))
        val p40 = Path().apply { moveTo(0.796f * w, 0.618315f * h); lineTo(0.82138f * w, 0.618315f * h); lineTo(0.796f * w, 0.548315f * h); close() }
        drawPath(p40, Color(0x8CCBD3DAL))
        val p41 = Path().apply { moveTo(0.83316f * w, 0.613229f * h); lineTo(0.88284f * w, 0.613229f * h); lineTo(0.858f * w, 0.543229f * h); close() }
        drawPath(p41, Color(0xFFFFFFFFL))
        val p42 = Path().apply { moveTo(0.858f * w, 0.613229f * h); lineTo(0.88284f * w, 0.613229f * h); lineTo(0.858f * w, 0.543229f * h); close() }
        drawPath(p42, Color(0x8CCBD3DAL))
        val p43 = Path().apply { moveTo(0.8957f * w, 0.605f * h); lineTo(0.9443f * w, 0.605f * h); lineTo(0.92f * w, 0.535f * h); close() }
        drawPath(p43, Color(0xFFFFFFFFL))
        val p44 = Path().apply { moveTo(0.92f * w, 0.605f * h); lineTo(0.9443f * w, 0.605f * h); lineTo(0.92f * w, 0.535f * h); close() }
        drawPath(p44, Color(0x8CCBD3DAL))
    }
    drawCircle(Color(0xFFFFFFFFL), radius = 0.052f * w, center = Offset(0.585f * w, 0.33f * h))
    drawCircle(Color(0xFF1F3D52L), radius = 0.03f * w, center = Offset(0.593f * w, 0.334f * h))
    drawCircle(Color(0xFFFFFFFFL), radius = 0.012f * w, center = Offset(0.573f * w, 0.318f * h))
    val p45 = Path().apply { moveTo(0.49f * w, 0.23f * h); cubicTo(0.58f * w, 0.21f * h, 0.67f * w, 0.26f * h, 0.71f * w, 0.33f * h); cubicTo(0.64f * w, 0.3f * h, 0.56f * w, 0.285f * h, 0.49f * w, 0.295f * h); close() }
    drawPath(p45, Color(0xFF2E3741L))
}


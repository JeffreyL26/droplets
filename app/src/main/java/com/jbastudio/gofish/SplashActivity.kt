package com.jbastudio.gofish

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashContent(
                onFinished = {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}

// ── Pastell-Hintergrund (lavendel → foam → seafoam) ────────────────────────
private val PastelTop = Color(0xFFF4ECFC)
private val PastelMid = Color(0xFFEAF4FB)
private val PastelBot = Color(0xFFE5F7EF)

@Composable
private fun SplashContent(onFinished: () -> Unit) {
    // Eintritts-Animation (Scale-Pop) + Gesamt-Opacity (ein-/ausblenden)
    val appear = remember { Animatable(0f) }
    val alpha  = remember { Animatable(0f) }

    // Overshoot-Easing für ein verspieltes "Pop"
    val overshoot = remember { CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(420)) }
        appear.animateTo(1f, tween(660, easing = overshoot))
        delay(1450)
        alpha.animateTo(0f, tween(460))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PastelTop, PastelMid, PastelBot))),
        contentAlignment = Alignment.Center
    ) {
        // Dezente Pastell-Blasen für Tiefe
        SoftBubbles()

        Image(
            painter = painterResource(R.drawable.logo_jbateam),
            contentDescription = "jba~team",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .graphicsLayer {
                    val s = 0.84f + 0.16f * appear.value
                    scaleX = s; scaleY = s
                    this.alpha = alpha.value
                }
        )
    }
}

/** Sehr dezente, halbtransparente Pastell-Kreise als Hintergrund-Deko. */
@Composable
private fun SoftBubbles() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(Color(0x33B590E0), radius = w * 0.17f, center = Offset(w * 0.16f, h * 0.24f))
        drawCircle(Color(0x2BFF8FA3), radius = w * 0.12f, center = Offset(w * 0.86f, h * 0.74f))
        drawCircle(Color(0x2B7FD1B0), radius = w * 0.09f, center = Offset(w * 0.80f, h * 0.18f))
        drawCircle(Color(0x22FFD56B), radius = w * 0.07f, center = Offset(w * 0.22f, h * 0.80f))
    }
}

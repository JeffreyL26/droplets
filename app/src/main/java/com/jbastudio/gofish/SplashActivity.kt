package com.jbastudio.gofish

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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

@Composable
private fun SplashContent(onFinished: () -> Unit) {
    val textAlpha = remember { Animatable(0f) }
    val scale     = remember { Animatable(0.94f) }

    LaunchedEffect(Unit) {
        // sanft erscheinen
        textAlpha.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        scale.animateTo(1.02f, tween(900))
        scale.animateTo(1f,    tween(300))
        delay(700)
        // sanft verschwinden
        textAlpha.animateTo(0f, tween(450, easing = FastOutSlowInEasing))
        onFinished()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()

        // 1. Tiefer Linear-Gradient — Basis
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color(0xFF26262B),
                        0.35f to Color(0xFF17171B),
                        0.70f to Color(0xFF0B0B0E),
                        1.00f to Color(0xFF050507)
                    )
                )
        )

        // 2. Subtiler radialer "Sheen" oben — metallischer Look
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.50f, h * 0.28f),
                        radius = w * 0.80f
                    )
                )
        )

        // 3. Vignette unten — verstärkt Tiefe
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        ),
                        center = Offset(w * 0.50f, h * 0.55f),
                        radius = w * 1.1f
                    )
                )
        )

        // 4. Text — weiß mit weichem Glow
        Text(
            text = "<jba~studio>",
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(textAlpha.value)
                .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
            style = TextStyle(
                color = Color.White,
                fontSize = 36.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                shadow = Shadow(
                    color = Color.White.copy(alpha = 0.40f),
                    offset = Offset.Zero,
                    blurRadius = 28f
                )
            )
        )
    }
}

package com.sophia.os.app

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun SophiaCharacter(
    state: SophiaState,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "character")

    val glowDuration = when (state) {
        SophiaState.IDLE -> 4200
        SophiaState.THINKING -> 1300
        SophiaState.SPEAKING -> 2000
    }
    val floatDuration = 5200

    val glowPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(glowDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "glow",
    )
    val floatPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(floatDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "float",
    )

    val glowStrength = 0.62f + 0.18f * sin(glowPhase)
    val floatOffsetY = (6f * sin(floatPhase)).dp

    val glowColor = when (state) {
        SophiaState.IDLE -> Color(0xFFB8860B)
        SophiaState.THINKING -> Color(0xFF00D4C8)
        SophiaState.SPEAKING -> Color(0xFFFFC94D)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val radius = size.minDimension * 0.55f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.55f * glowStrength),
                        glowColor.copy(alpha = 0.15f * glowStrength),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
        }

        Image(
            painter = painterResource(id = R.drawable.sophia_character),
            contentDescription = "Sophia",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = floatOffsetY),
        )
    }
}

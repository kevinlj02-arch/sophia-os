package com.sophia.os.app

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sin

enum class SophiaState { IDLE, THINKING, SPEAKING }

@Composable
fun SophiaAvatar(state: SophiaState, modifier: Modifier = Modifier, avatarSize: androidx.compose.ui.unit.Dp = 160.dp) {
    val transition = rememberInfiniteTransition(label = "sophia")

    val pulseDuration = when (state) {
        SophiaState.IDLE -> 3200
        SophiaState.THINKING -> 900
        SophiaState.SPEAKING -> 1400
    }

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )

    val breath = 0.925f + 0.075f * sin(phase)

    val coreColor = when (state) {
        SophiaState.IDLE -> Color(0xFF6C63FF)
        SophiaState.THINKING -> Color(0xFF00D4C8)
        SophiaState.SPEAKING -> Color(0xFF9C7BFF)
    }

    Canvas(modifier = modifier.size(avatarSize)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(coreColor.copy(alpha = 0.35f), Color.Transparent),
                center = center,
                radius = maxRadius * breath,
            ),
            radius = maxRadius * breath,
            center = center,
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(coreColor.copy(alpha = 0.55f), Color.Transparent),
                center = center,
                radius = maxRadius * 0.62f * breath,
            ),
            radius = maxRadius * 0.62f * breath,
            center = center,
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.9f), coreColor),
                center = center,
                radius = maxRadius * 0.28f,
            ),
            radius = maxRadius * 0.28f * breath,
            center = center,
        )
    }
}

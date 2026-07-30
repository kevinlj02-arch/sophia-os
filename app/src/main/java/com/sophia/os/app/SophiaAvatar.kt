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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

enum class SophiaState { IDLE, THINKING, SPEAKING }

@Composable
fun SophiaAvatar(state: SophiaState, modifier: Modifier = Modifier, avatarSize: Dp = 160.dp) {
    val transition = rememberInfiniteTransition(label = "sophia")

    val breathDuration = when (state) {
        SophiaState.IDLE -> 3600
        SophiaState.THINKING -> 1100
        SophiaState.SPEAKING -> 1700
    }
    val rotationDuration = when (state) {
        SophiaState.IDLE -> 14000
        SophiaState.THINKING -> 4500
        SophiaState.SPEAKING -> 9000
    }

    val breathPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(breathDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "breath",
    )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    val accent = when (state) {
        SophiaState.IDLE -> Color(0xFF6C63FF)
        SophiaState.THINKING -> Color(0xFF00D4C8)
        SophiaState.SPEAKING -> Color(0xFF9C7BFF)
    }
    val accentSoft = when (state) {
        SophiaState.IDLE -> Color(0xFF8B7BFF)
        SophiaState.THINKING -> Color(0xFF4DE5DB)
        SophiaState.SPEAKING -> Color(0xFFB9A3FF)
    }

    val breath = 0.94f + 0.06f * sin(breathPhase)

    Canvas(modifier = modifier.size(avatarSize)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val unit = size.minDimension / 2f

        drawOuterHalo(center, unit, breath, accent)
        drawRotatingArc(center, unit, rotation, accent)
        drawOrbitingParticles(center, unit, rotation, accentSoft)
        drawCore(center, unit, breath, accent, accentSoft)
    }
}

private fun DrawScope.drawOuterHalo(center: Offset, unit: Float, breath: Float, accent: Color) {
    val radius = unit * breath
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accent.copy(alpha = 0.28f), Color.Transparent),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawRotatingArc(center: Offset, unit: Float, rotation: Float, accent: Color) {
    val ringRadius = unit * 0.82f
    rotate(degrees = rotation, pivot = center) {
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(Color.Transparent, accent.copy(alpha = 0.9f), Color.Transparent),
                center = center,
            ),
            startAngle = 0f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
            size = Size(ringRadius * 2f, ringRadius * 2f),
            style = Stroke(width = unit * 0.05f),
        )
    }
}

private fun DrawScope.drawOrbitingParticles(center: Offset, unit: Float, rotation: Float, color: Color) {
    val count = 7
    val orbitRadius = unit * 0.66f
    val baseAngle = Math.toRadians(rotation.toDouble())
    for (i in 0 until count) {
        val angle = baseAngle + (2 * Math.PI / count) * i
        val px = center.x + (orbitRadius * cos(angle)).toFloat()
        val py = center.y + (orbitRadius * sin(angle)).toFloat()
        val dotRadius = unit * (0.03f + 0.015f * ((i % 3) / 2f))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.95f), Color.Transparent),
                center = Offset(px, py),
                radius = dotRadius * 2.4f,
            ),
            radius = dotRadius * 2.4f,
            center = Offset(px, py),
        )
    }
}

private fun DrawScope.drawCore(
    center: Offset,
    unit: Float,
    breath: Float,
    accent: Color,
    accentSoft: Color,
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accentSoft.copy(alpha = 0.55f), accent.copy(alpha = 0.15f), Color.Transparent),
            center = center,
            radius = unit * 0.5f * breath,
        ),
        radius = unit * 0.5f * breath,
        center = center,
    )
    val coreCenter = Offset(center.x - unit * 0.06f, center.y - unit * 0.06f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.95f), accent),
            center = coreCenter,
            radius = unit * 0.26f,
        ),
        radius = unit * 0.26f * breath,
        center = center,
    )
}

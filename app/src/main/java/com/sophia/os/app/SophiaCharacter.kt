package com.sophia.os.app

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun SophiaCharacter(
    state: SophiaState,
    modifier: Modifier = Modifier,
) {
    var idlePose by remember { mutableStateOf(0) }
    LaunchedEffect(state) {
        if (state == SophiaState.IDLE) {
            while (true) {
                delay(4200)
                idlePose = (idlePose + 1) % 3
            }
        }
    }

    val activePose = when (state) {
        SophiaState.SPEAKING -> 1
        SophiaState.THINKING -> 2
        SophiaState.IDLE -> idlePose
    }

    val transition = rememberInfiniteTransition(label = "character")

    val breathPhase by transition.animateFloat(
        0f, (2 * Math.PI).toFloat(),
        infiniteRepeatable(tween(3800, easing = EaseInOutSine), RepeatMode.Restart),
        label = "breath",
    )
    val swayPhase by transition.animateFloat(
        0f, (2 * Math.PI).toFloat(),
        infiniteRepeatable(tween(6000, easing = EaseInOutSine), RepeatMode.Restart),
        label = "sway",
    )
    val hoverPhase by transition.animateFloat(
        0f, (2 * Math.PI).toFloat(),
        infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Restart),
        label = "hover",
    )
    val ringRotation by transition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring",
    )
    val particlePhase by transition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "particles",
    )

    val auraDuration = when (state) {
        SophiaState.IDLE -> 4200
        SophiaState.THINKING -> 1300
        SophiaState.SPEAKING -> 2000
    }
    val auraPhase by transition.animateFloat(
        0f, (2 * Math.PI).toFloat(),
        infiniteRepeatable(tween(auraDuration, easing = LinearEasing), RepeatMode.Restart),
        label = "aura",
    )

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val entrance by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "entrance",
    )

    val breathScale = 1f + 0.02f * sin(breathPhase)
    val swayX = 7f * sin(swayPhase)
    val swayRot = 0.9f * sin(swayPhase)
    val hoverY = 6f * sin(hoverPhase)
    val breathLift = -4f * sin(breathPhase)
    val entranceLift = (1f - entrance) * 45f

    val auraStrength = 0.6f + 0.2f * sin(auraPhase)
    val auraColor = when (state) {
        SophiaState.IDLE -> Color(0xFFB8860B)
        SophiaState.THINKING -> Color(0xFF00D4C8)
        SophiaState.SPEAKING -> Color(0xFFFFC94D)
    }

    val w0 by animateFloatAsState(if (activePose == 0) 1f else 0f, tween(600), label = "w0")
    val w1 by animateFloatAsState(if (activePose == 1) 1f else 0f, tween(600), label = "w1")
    val w2 by animateFloatAsState(if (activePose == 2) 1f else 0f, tween(600), label = "w2")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val center = Offset(cx, size.height * 0.42f)
            val radius = size.minDimension * 0.58f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = 0.5f * auraStrength * entrance),
                        auraColor.copy(alpha = 0.14f * auraStrength * entrance),
                        Color.Transparent,
                    ),
                    center = center, radius = radius,
                ),
                radius = radius, center = center,
            )

            val ringCy = size.height * 0.9f
            val ringRx = size.width * 0.32f
            val ringRy = size.height * 0.03f
            rotate(degrees = ringRotation, pivot = Offset(cx, ringCy)) {
                drawOval(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color.Transparent, auraColor.copy(alpha = 0.6f * entrance), Color.Transparent),
                        center = Offset(cx, ringCy),
                    ),
                    topLeft = Offset(cx - ringRx, ringCy - ringRy),
                    size = Size(ringRx * 2f, ringRy * 2f),
                    style = Stroke(width = size.minDimension * 0.008f),
                )
            }

            val moteCount = 9
            for (i in 0 until moteCount) {
                val seed = i * 0.137f
                val p = (particlePhase + seed) % 1f
                val mx = cx + (sin((seed + p) * 6.28f) * size.width * 0.28f)
                val my = size.height * (0.92f - p * 0.75f)
                val moteAlpha = (1f - p) * 0.5f * entrance
                val moteR = size.minDimension * (0.006f + 0.004f * ((i % 3) / 2f))
                drawCircle(
                    color = auraColor.copy(alpha = moteAlpha),
                    radius = moteR,
                    center = Offset(mx, my),
                )
            }
        }

        val motion: Modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = breathScale
                scaleY = breathScale
                translationX = swayX
                translationY = breathLift + hoverY + entranceLift
                rotationZ = swayRot
            }

        PoseLayer(R.drawable.sophia_listening, w0 * entrance, motion)
        PoseLayer(R.drawable.sophia_speaking, w1 * entrance, motion)
        PoseLayer(R.drawable.sophia_greeting, w2 * entrance, motion)
    }
}

@Composable
private fun PoseLayer(resId: Int, alpha: Float, motion: Modifier) {
    if (alpha <= 0.01f) return
    Image(
        painter = painterResource(id = resId),
        contentDescription = "Sophia",
        contentScale = ContentScale.Fit,
        modifier = motion.graphicsLayer { this.alpha = alpha },
    )
}

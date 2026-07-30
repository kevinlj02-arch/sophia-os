package com.sophia.os.app

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlin.math.sin

@Composable
fun SophiaCharacter(
    state: SophiaState,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "character")

    val breathPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "breath",
    )
    val swayPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Restart),
        label = "sway",
    )

    val auraDuration = when (state) {
        SophiaState.IDLE -> 4200
        SophiaState.THINKING -> 1300
        SophiaState.SPEAKING -> 2000
    }
    val auraPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(auraDuration, easing = LinearEasing), RepeatMode.Restart),
        label = "aura",
    )

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val entrance by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label = "entrance",
    )

    val breathScale = 1f + 0.012f * sin(breathPhase)
    val breathLift = -3f * sin(breathPhase)
    val swayX = 4f * sin(swayPhase)
    val swayRot = 0.6f * sin(swayPhase)
    val entranceLift = (1f - entrance) * 40f

    val auraStrength = 0.6f + 0.2f * sin(auraPhase)
    val auraColor = when (state) {
        SophiaState.IDLE -> Color(0xFFB8860B)
        SophiaState.THINKING -> Color(0xFF00D4C8)
        SophiaState.SPEAKING -> Color(0xFFFFC94D)
    }

    val idleWeight by animateFloatAsState(
        targetValue = if (state == SophiaState.IDLE) 1f else 0f,
        animationSpec = tween(450), label = "idleW",
    )
    val speakWeight by animateFloatAsState(
        targetValue = if (state == SophiaState.SPEAKING) 1f else 0f,
        animationSpec = tween(450), label = "speakW",
    )
    val thinkWeight by animateFloatAsState(
        targetValue = if (state == SophiaState.THINKING) 1f else 0f,
        animationSpec = tween(450), label = "thinkW",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val radius = size.minDimension * 0.58f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor.copy(alpha = 0.5f * auraStrength * entrance),
                        auraColor.copy(alpha = 0.14f * auraStrength * entrance),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
        }

        val motion: Modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = breathScale
                scaleY = breathScale
                translationX = swayX
                translationY = breathLift + entranceLift
                rotationZ = swayRot
            }

        PoseLayer(R.drawable.sophia_listening, idleWeight * entrance, motion)
        PoseLayer(R.drawable.sophia_speaking, speakWeight * entrance, motion)
        PoseLayer(R.drawable.sophia_greeting, thinkWeight * entrance, motion)
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

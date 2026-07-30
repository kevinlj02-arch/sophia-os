package com.sophia.os.app

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PanelBg = Color(0xE60F0D13)
private val PanelGold = Color(0xFFD4AF37)
private val PanelGoldBright = Color(0xFFFFC94D)
private val PanelGoldDim = Color(0xFF6E5A1C)
private val PanelText = Color(0xFFF5F2E8)
private val PanelMuted = Color(0xFF9A927E)
private val PanelGreen = Color(0xFF4ADE80)

@Composable
fun HoloPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "panelGlow")
    val glow by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            PanelGold.copy(alpha = glow),
                            PanelGoldDim.copy(alpha = glow * 0.8f),
                        )
                    )
                )
                .padding(1.5.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(PanelBg)
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PanelGoldBright)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        title.uppercase(),
                        color = PanelGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                    )
                }
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label.uppercase(), color = PanelMuted, fontSize = 11.sp, letterSpacing = 0.5.sp)
        Spacer(Modifier.weight(1f))
        Text(
            value,
            color = if (highlight) PanelGreen else PanelGoldBright,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
fun StatBar(label: String, fraction: Float) {
    var started by remember { mutableStateOf(false) }
    val animatedFraction by animateFloatAsState(
        targetValue = if (started) fraction.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(900, easing = LinearEasing),
        label = "fill",
    )
    LaunchedEffect(Unit) { started = true }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label.uppercase(), color = PanelMuted, fontSize = 11.sp, letterSpacing = 0.5.sp)
            Spacer(Modifier.weight(1f))
            Text("${(animatedFraction * 100).toInt()}%", color = PanelGoldBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF241F14))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(PanelGoldDim, PanelGoldBright)))
            )
        }
    }
}

package com.sophia.os.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime

private val Ink = Color(0xFF050507)
private val Panel = Color(0xFF121016)
private val Gold = Color(0xFFD4AF37)
private val GoldBright = Color(0xFFFFC94D)
private val GoldDim = Color(0xFF8A7223)
private val TextPrimary = Color(0xFFF5F2E8)
private val TextMuted = Color(0xFF9A927E)

@Composable
fun HomeScreen(
    onOpenChat: () -> Unit,
    sophiaState: SophiaState = SophiaState.IDLE,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Ink, Color(0xFF0C0A0F), Ink)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = "SOPHIA OS",
                color = Gold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
            )
            Text(
                text = greetingLine(),
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
            )

            SophiaCharacter(
                state = sophiaState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp),
            )

            Text(
                text = when (sophiaState) {
                    SophiaState.THINKING -> "Processing…"
                    SophiaState.SPEAKING -> "Responding"
                    SophiaState.IDLE -> "All systems operational"
                },
                color = if (sophiaState == SophiaState.IDLE) GoldBright else Gold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
            )

            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                PrimaryActionButton(text = "Talk to Sophia", onClick = onOpenChat)

                Spacer(Modifier.height(24.dp))

                SectionLabel("Command modules")
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModuleCard("Conversation", "Ask, plan, think", true, Modifier.weight(1f), onOpenChat)
                    ModuleCard("Memory Vault", "What Sophia knows", false, Modifier.weight(1f)) {}
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ModuleCard("Knowledge Graph", "Coming soon", false, Modifier.weight(1f)) {}
                    ModuleCard("Security Center", "Coming soon", false, Modifier.weight(1f)) {}
                }

                Spacer(Modifier.height(24.dp))

                SectionLabel("Live system feed")
                Spacer(Modifier.height(12.dp))
                FeedRow("Voice print", "RECOGNIZED")
                Spacer(Modifier.height(8.dp))
                FeedRow("Core status", "OPTIMAL")
                Spacer(Modifier.height(8.dp))
                FeedRow("Threat detection", "NONE")

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

private fun greetingLine(): String {
    val hour = LocalTime.now().hour
    val g = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
    return "$g, Commander"
}

@Composable
private fun PrimaryActionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(GoldDim, Gold, GoldBright)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = Gold,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Panel)
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (active) GoldBright else GoldDim)
        )
        Column {
            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun FeedRow(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Panel)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label.uppercase(), color = TextMuted, fontSize = 12.sp, letterSpacing = 1.sp)
        Spacer(Modifier.weight(1f))
        Text(status, color = GoldBright, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

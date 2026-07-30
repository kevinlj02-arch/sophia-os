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
import androidx.compose.foundation.layout.width
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

private val Ink = Color(0xFF07080F)
private val Panel = Color(0xFF13141F)
private val Violet = Color(0xFF6C63FF)
private val Cyan = Color(0xFF00D4C8)
private val TextPrimary = Color(0xFFF2F3FA)
private val TextMuted = Color(0xFF8A8FA6)

@Composable
fun HomeScreen(
    onOpenChat: () -> Unit,
    sophiaState: SophiaState = SophiaState.IDLE,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Ink, Color(0xFF0B0D18), Ink),
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))
            GreetingHeader()
            Spacer(Modifier.height(8.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 16.dp)) {
                SophiaAvatar(state = sophiaState, avatarSize = 200.dp)
            }

            Text(
                text = "Sophia",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Online — ready when you are",
                color = Cyan,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(Modifier.height(28.dp))

            PrimaryActionButton(text = "Talk to Sophia", onClick = onOpenChat)

            Spacer(Modifier.height(24.dp))

            SectionLabel("Quick actions")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard("Conversation", "Ask, plan, or think out loud", Violet, Modifier.weight(1f), onOpenChat)
                QuickActionCard("Memory", "What Sophia knows", Cyan, Modifier.weight(1f)) {}
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard("Tasks", "Coming soon", TextMuted, Modifier.weight(1f)) {}
                QuickActionCard("Knowledge", "Coming soon", TextMuted, Modifier.weight(1f)) {}
            }

            Spacer(Modifier.height(28.dp))

            SectionLabel("Recent")
            Spacer(Modifier.height(12.dp))
            RecentActivityRow("Welcome to Sophia OS", "Tap “Talk to Sophia” to begin")

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun GreetingHeader() {
    val hour = LocalTime.now().hour
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = greeting,
            color = TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Your operating environment is ready.",
            color = TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PrimaryActionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(colors = listOf(Violet, Color(0xFF8B7BFF)))
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = TextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(104.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Panel)
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(accent)
        )
        Column {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun RecentActivityRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Panel)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(Violet, Cyan)))
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

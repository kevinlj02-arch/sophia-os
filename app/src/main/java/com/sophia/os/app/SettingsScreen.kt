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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SInk = Color(0xFF050507)
private val SPanel = Color(0xFF121016)
private val SInputBg = Color(0xFF1A1720)
private val SGold = Color(0xFFD4AF37)
private val SGoldBright = Color(0xFFFFC94D)
private val SGoldDim = Color(0xFF8A7223)
private val STextPrimary = Color(0xFFF5F2E8)
private val STextMuted = Color(0xFF9A927E)
private val SRed = Color(0xFFE05252)

@Composable
fun SettingsScreen(
    voiceOutputDefault: Boolean,
    onToggleVoiceDefault: (Boolean) -> Unit,
    onClearConversations: () -> Unit,
    onClearMemory: () -> Unit,
    onClearTasks: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SInk, Color(0xFF0C0A0F), SInk)))
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SPanel)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = STextPrimary, fontSize = 20.sp)
            }
            Text(
                "SETTINGS",
                color = SGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 12.dp),
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionLabel("Voice")
            ToggleRow(
                label = "Speak replies aloud",
                subtitle = "Sophia reads her responses using text-to-speech",
                checked = voiceOutputDefault,
                onToggle = onToggleVoiceDefault,
            )

            Spacer(Modifier.height(4.dp))

            SectionLabel("Data")
            ActionRow("Clear conversation history", "Delete all chat messages", onClearConversations)
            ActionRow("Clear memory", "Forget everything Sophia has stored", onClearMemory)
            ActionRow("Clear tasks", "Remove all tasks", onClearTasks)

            Spacer(Modifier.height(4.dp))

            SectionLabel("About")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SPanel)
                    .padding(16.dp),
            ) {
                InfoLine("Version", "0.5.0-demo")
                Spacer(Modifier.height(8.dp))
                InfoLine("Assistant", "Sophia")
                Spacer(Modifier.height(8.dp))
                InfoLine("Build", "Sophia OS Demo")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        color = SGold,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ToggleRow(
    label: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SPanel)
            .clickable { onToggle(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = STextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = STextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        Spacer(Modifier.size(12.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (checked) Brush.horizontalGradient(listOf(SGold, SGoldBright)) else Brush.horizontalGradient(listOf(SInputBg, SInputBg))),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (checked) SInk else STextMuted),
            )
        }
    }
}

@Composable
private fun ActionRow(label: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SPanel)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = SRed, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = STextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        Text("›", color = STextMuted, fontSize = 22.sp)
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label.uppercase(), color = STextMuted, fontSize = 11.sp, letterSpacing = 0.5.sp)
        Spacer(Modifier.weight(1f))
        Text(value, color = SGoldBright, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

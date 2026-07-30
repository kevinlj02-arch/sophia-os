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

private val Ink = Color(0xFF050507)
private val Gold = Color(0xFFD4AF37)
private val GoldBright = Color(0xFFFFC94D)
private val GoldDim = Color(0xFF8A7223)
private val TextPrimary = Color(0xFFF5F2E8)
private val TextMuted = Color(0xFF9A927E)
private val Green = Color(0xFF4ADE80)
private val ReadoutBg = Color(0xCC0F0D13)

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
            TopStatusBar()

            Text(
                text = "SOPHIA OS",
                color = Gold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 5.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "MAINFRAME COMMAND CENTER",
                color = TextMuted,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 2.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp),
                contentAlignment = Alignment.Center,
            ) {
                SophiaCharacter(
                    state = sophiaState,
                    modifier = Modifier.fillMaxSize(),
                )

                FloatingReadout(
                    title = "NEURAL NODES",
                    value = "128.7B",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 16.dp),
                )
                FloatingReadout(
                    title = "QUANTUM LINK",
                    value = "SECURE",
                    valueColor = Green,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 16.dp),
                )
                FloatingReadout(
                    title = "COHERENCE",
                    value = "STABLE",
                    valueColor = Green,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp),
                )
                FloatingReadout(
                    title = "QUBITS",
                    value = "1.02M",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                )
            }

            Text(
                text = when (sophiaState) {
                    SophiaState.THINKING -> "PROCESSING…"
                    SophiaState.SPEAKING -> "RESPONDING"
                    SophiaState.IDLE -> "ALL SYSTEMS OPERATIONAL"
                },
                color = if (sophiaState == SophiaState.IDLE) Green else GoldBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                PrimaryActionButton(text = "Talk to Sophia", onClick = onOpenChat)

                HoloPanel(title = "System Overview") {
                    Column {
                        StatBar("CPU Usage", 0.23f)
                        StatBar("Memory", 0.41f)
                        StatBar("Storage", 0.68f)
                        StatBar("Network", 0.18f)
                    }
                }

                HoloPanel(title = "AI Consciousness Core") {
                    Column {
                        StatRow("Learning Rate", "ADAPTIVE")
                        StatRow("Neural Nodes", "128.7B")
                        StatRow("Awareness", "EXPANDING")
                        StatRow("Decision Engine", "ACTIVE", highlight = true)
                        StatRow("Emotional Intel", "ACTIVE", highlight = true)
                    }
                }

                HoloPanel(title = "Command Modules") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ModuleChip("Conversation", true, Modifier.weight(1f), onOpenChat)
                            ModuleChip("Memory Vault", false, Modifier.weight(1f)) {}
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ModuleChip("Knowledge", false, Modifier.weight(1f)) {}
                            ModuleChip("Security", false, Modifier.weight(1f)) {}
                        }
                    }
                }

                HoloPanel(title = "Live System Feed") {
                    Column {
                        StatRow("Voice Print", "RECOGNIZED", highlight = true)
                        StatRow("Satellite Uplink", "SECURE", highlight = true)
                        StatRow("Threat Detection", "NONE", highlight = true)
                        StatRow("Biometric Systems", "NOMINAL", highlight = true)
                    }
                }

                Spacer(Modifier.height(8.dp))
            }

            CommandBar()
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FloatingReadout(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = GoldBright,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ReadoutBg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(title, color = TextMuted, fontSize = 8.sp, letterSpacing = 1.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun TopStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0810))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatusChip("STATUS", "OPTIMAL", Green)
        StatusChip("CORE", "32°C", GoldBright)
        StatusChip("LINK", "SECURE", Green)
        StatusChip("UPTIME", "127d", GoldBright)
    }
}

@Composable
private fun StatusChip(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextMuted, fontSize = 8.sp, letterSpacing = 1.sp)
        Text(value, color = valueColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PrimaryActionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(GoldDim, Gold, GoldBright)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun ModuleChip(
    title: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF16131B))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .height(8.dp)
                .width(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (active) GoldBright else GoldDim),
        )
        Spacer(Modifier.width(8.dp))
        Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CommandBar() {
    val items = listOf("FILES", "SIMS", "3D", "HOLO", "ANALYTICS", "TASKS")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A0810))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items.forEach { label ->
            Text(label, color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
        }
    }
}

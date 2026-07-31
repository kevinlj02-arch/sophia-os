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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    onOpenMemory: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenSettings: () -> Unit,
    sophiaState: SophiaState = SophiaState.IDLE,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B0910), Ink, Color(0xFF0A0812), Ink)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopStatusBar(onOpenSettings = onOpenSettings)

            Spacer(Modifier.height(6.dp))
            Text(
                text = "SOPHIA OS",
                color = Gold,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 3.dp)) {
                Divider(width = 24.dp)
                Text(
                    text = "MAINFRAME COMMAND CENTER",
                    color = TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 2.5.sp,
                    modifier = Modifier.padding(horizontal = 10.dp),
                )
                Divider(width = 24.dp)
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(430.dp),
                contentAlignment = Alignment.Center,
            ) {
                SophiaCharacter(state = sophiaState, modifier = Modifier.fillMaxSize())

                FloatingReadout("NEURAL NODES", "128.7B", Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 16.dp))
                FloatingReadout("QUANTUM LINK", "SECURE", Modifier.align(Alignment.TopEnd).padding(end = 12.dp, top = 16.dp), Green)
                FloatingReadout("COHERENCE", "STABLE", Modifier.align(Alignment.CenterStart).padding(start = 12.dp), Green)
                FloatingReadout("QUBITS", "1.02M", Modifier.align(Alignment.CenterEnd).padding(end = 12.dp))
            }

            StatusPill(sophiaState)

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
                            ModuleChip("Conversation", Icons.Filled.Chat, true, Modifier.weight(1f), onOpenChat)
                            ModuleChip("Memory Vault", Icons.Filled.Memory, true, Modifier.weight(1f), onOpenMemory)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ModuleChip("Task Manager", Icons.Filled.CheckCircle, true, Modifier.weight(1f), onOpenTasks)
                            ModuleChip("Notes", Icons.Filled.Description, true, Modifier.weight(1f), onOpenNotes)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ModuleChip("Settings", Icons.Filled.Tune, true, Modifier.weight(1f), onOpenSettings)
                            ModuleChip("Security", Icons.Filled.Shield, false, Modifier.weight(1f)) {}
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

            CommandBar(onOpenTasks = onOpenTasks, onOpenNotes = onOpenNotes, onOpenSettings = onOpenSettings)
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun Divider(width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(1.dp)
            .background(Brush.horizontalGradient(listOf(Color.Transparent, GoldDim)))
    )
}

@Composable
private fun StatusPill(sophiaState: SophiaState) {
    val (label, color) = when (sophiaState) {
        SophiaState.THINKING -> "PROCESSING" to GoldBright
        SophiaState.SPEAKING -> "RESPONDING" to GoldBright
        SophiaState.IDLE -> "ALL SYSTEMS OPERATIONAL" to Green
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x33000000))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
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
private fun TopStatusBar(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0810))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusChip("STATUS", "OPTIMAL", Green)
        StatusChip("CORE", "32°C", GoldBright)
        StatusChip("LINK", "SECURE", Green)
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onOpenSettings() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Gold, modifier = Modifier.size(18.dp))
        }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Chat, contentDescription = null, tint = Ink, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun ModuleChip(
    title: String,
    icon: ImageVector,
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
        Icon(
            icon,
            contentDescription = null,
            tint = if (active) GoldBright else GoldDim,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CommandBar(onOpenTasks: () -> Unit, onOpenNotes: () -> Unit, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0A0810))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        CommandBarItem("FILES") {}
        CommandBarItem("TASKS", onOpenTasks)
        CommandBarItem("NOTES", onOpenNotes)
        CommandBarItem("3D") {}
        CommandBarItem("SETTINGS", onOpenSettings)
    }
}

@Composable
private fun CommandBarItem(label: String, onClick: () -> Unit) {
    Text(
        label,
        color = Gold,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        modifier = Modifier.clickable { onClick() }.padding(4.dp),
    )
}

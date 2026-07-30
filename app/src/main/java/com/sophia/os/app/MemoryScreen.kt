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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val MemInk = Color(0xFF050507)
private val MemPanel = Color(0xFF121016)
private val MemGold = Color(0xFFD4AF37)
private val MemGoldBright = Color(0xFFFFC94D)
private val MemTextPrimary = Color(0xFFF5F2E8)
private val MemTextMuted = Color(0xFF9A927E)
private val MemRed = Color(0xFFE05252)

@Composable
fun MemoryScreen(
    facts: List<MemoryFact>,
    onBack: () -> Unit,
    onForget: (MemoryFact) -> Unit,
    onClearAll: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MemInk, Color(0xFF0C0A0F), MemInk)))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MemPanel)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = MemTextPrimary, fontSize = 20.sp)
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text("MEMORY VAULT", color = MemGold, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(
                    "${facts.size} ${if (facts.size == 1) "memory" else "memories"} stored",
                    color = MemTextMuted,
                    fontSize = 12.sp,
                )
            }
        }

        if (facts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Nothing remembered yet", color = MemTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "As you talk with Sophia, she'll remember what matters — preferences, projects, and details worth keeping.",
                    color = MemTextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(facts) { fact ->
                    MemoryCard(fact = fact, onForget = { onForget(fact) })
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MemPanel)
                    .clickable { onClearAll() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Clear all memories", color = MemRed, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun MemoryCard(fact: MemoryFact, onForget: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MemPanel)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MemGoldBright)
        )
        Spacer(Modifier.size(12.dp))
        Text(fact.text, color = MemTextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Spacer(Modifier.size(12.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1720))
                .clickable { onForget() },
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", color = MemTextMuted, fontSize = 14.sp)
        }
    }
}

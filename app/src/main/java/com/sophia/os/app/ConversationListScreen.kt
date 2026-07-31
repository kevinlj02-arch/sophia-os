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

private val CInk = Color(0xFF050507)
private val CPanel = Color(0xFF121016)
private val CGold = Color(0xFFD4AF37)
private val CGoldBright = Color(0xFFFFC94D)
private val CGoldDim = Color(0xFF8A7223)
private val CTextPrimary = Color(0xFFF5F2E8)
private val CTextMuted = Color(0xFF9A927E)

@Composable
fun ConversationListScreen(
    conversations: List<Conversation>,
    onOpen: (Long) -> Unit,
    onNew: () -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CInk, Color(0xFF0C0A0F), CInk)))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CPanel)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = CTextPrimary, fontSize = 20.sp)
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text("CONVERSATIONS", color = CGold, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(
                    "${conversations.size} ${if (conversations.size == 1) "conversation" else "conversations"}",
                    color = CTextMuted,
                    fontSize = 12.sp,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(CGoldDim, CGold, CGoldBright)))
                .clickable { onNew() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("+  New Conversation", color = CInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        if (conversations.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("No conversations yet", color = CTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Start a new conversation and it'll show up here.",
                    color = CTextMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(conversations, key = { it.id }) { convo ->
                    ConversationRow(
                        convo = convo,
                        onOpen = { onOpen(convo.id) },
                        onDelete = { onDelete(convo.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(convo: Conversation, onOpen: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CPanel)
            .clickable { onOpen() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SophiaEmblem(emblemSize = 32.dp)
        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
            Text(
                convo.title,
                color = CTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
            val preview = convo.messages.lastOrNull()?.text?.take(50) ?: "Empty conversation"
            Text(preview, color = CTextMuted, fontSize = 12.sp, maxLines = 1, modifier = Modifier.padding(top = 2.dp))
        }
        Spacer(Modifier.size(12.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1720))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", color = CTextMuted, fontSize = 13.sp)
        }
    }
}

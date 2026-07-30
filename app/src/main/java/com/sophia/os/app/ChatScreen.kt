package com.sophia.os.app

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ChatInk = Color(0xFF07080F)
private val ChatPanel = Color(0xFF13141F)
private val ChatInputBg = Color(0xFF1B1D2B)
private val ChatViolet = Color(0xFF6C63FF)
private val ChatVioletSoft = Color(0xFF8B7BFF)
private val ChatCyan = Color(0xFF00D4C8)
private val ChatTextPrimary = Color(0xFFF2F3FA)
private val ChatTextMuted = Color(0xFF8A8FA6)

@Composable
fun ChatScreen(
    messages: List<PersistedMessage>,
    draft: String,
    sophiaState: SophiaState,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()
    val isThinking = sophiaState == SophiaState.THINKING

    androidx.compose.runtime.LaunchedEffect(messages.size, isThinking) {
        val itemCount = messages.size + if (isThinking) 1 else 0
        if (itemCount > 0) listState.animateScrollToItem(itemCount - 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(ChatInk, Color(0xFF0B0D18), ChatInk)))
    ) {
        ChatHeader(sophiaState = sophiaState, onBack = onBack)

        if (messages.isEmpty() && !isThinking) {
            EmptyChatState(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(messages) { message ->
                    if (message.fromUser) UserBubble(message.text) else SophiaBubble(message.text)
                }
                if (isThinking) {
                    item { ThinkingRow() }
                }
            }
        }

        ChatInputBar(
            draft = draft,
            enabled = !isThinking,
            onDraftChange = onDraftChange,
            onSend = onSend,
        )
    }
}

@Composable
private fun ChatHeader(sophiaState: SophiaState, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ChatPanel)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Text("←", color = ChatTextPrimary, fontSize = 20.sp)
        }
        SophiaAvatar(
            state = sophiaState,
            modifier = Modifier.padding(start = 12.dp),
            avatarSize = 44.dp,
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text("Sophia", color = ChatTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = when (sophiaState) {
                    SophiaState.THINKING -> "Thinking…"
                    SophiaState.SPEAKING -> "Speaking"
                    SophiaState.IDLE -> "Online"
                },
                color = if (sophiaState == SophiaState.IDLE) ChatCyan else ChatVioletSoft,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun EmptyChatState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SophiaAvatar(state = SophiaState.IDLE, avatarSize = 96.dp)
        Spacer(Modifier.height(20.dp))
        Text(
            "Say hello to Sophia",
            color = ChatTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Ask a question, think out loud, or just start a conversation.",
            color = ChatTextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp))
                .background(Brush.horizontalGradient(listOf(ChatViolet, ChatVioletSoft)))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(text, color = Color.White, fontSize = 15.sp)
        }
    }
}

@Composable
private fun SophiaBubble(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        SophiaAvatar(state = SophiaState.IDLE, avatarSize = 28.dp)
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(ChatPanel)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(text, color = ChatTextPrimary, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ThinkingRow() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        SophiaAvatar(state = SophiaState.THINKING, avatarSize = 28.dp)
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ChatPanel)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            TypingDots()
        }
    }
}

@Composable
private fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "typing")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 0 until 3) {
            val alpha by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = i * 180, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dot$i",
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ChatTextMuted.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    draft: String,
    enabled: Boolean,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            value = draft,
            onValueChange = onDraftChange,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = { Text("Message Sophia…", color = ChatTextMuted) },
            enabled = enabled,
            textStyle = LocalTextStyle.current.copy(color = ChatTextPrimary, fontSize = 15.sp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ChatInputBg,
                unfocusedContainerColor = ChatInputBg,
                disabledContainerColor = ChatInputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = ChatViolet,
            ),
        )
        Spacer(Modifier.size(10.dp))
        val sendActive = enabled && draft.isNotBlank()
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (sendActive) Brush.horizontalGradient(listOf(ChatViolet, ChatVioletSoft))
                    else Brush.horizontalGradient(listOf(ChatPanel, ChatPanel))
                )
                .clickable(enabled = sendActive) { onSend() },
            contentAlignment = Alignment.Center,
        ) {
            Text("↑", color = if (sendActive) Color.White else ChatTextMuted, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

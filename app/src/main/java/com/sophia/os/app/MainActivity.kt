package com.sophia.os.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val SophiaPrimary = Color(0xFF6C63FF)
private val SophiaSecondary = Color(0xFF00D4C8)
private val SophiaBackground = Color(0xFF07080F)
private val SophiaSurface = Color(0xFF13141F)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = SophiaPrimary,
                    secondary = SophiaSecondary,
                    background = SophiaBackground,
                    surface = SophiaSurface,
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SophiaDemoApp()
                }
            }
        }
    }
}

@Composable
private fun SophiaDemoApp() {
    var showChat by remember { mutableStateOf(false) }

    if (showChat) {
        ChatScreen(onBack = { showChat = false })
    } else {
        HomeScreen(onOpenChat = { showChat = true })
    }
}

@Composable
private fun ChatScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val chatStore = remember { ChatStore(context) }
    val ai = remember { SophiaAI(BuildConfig.ANTHROPIC_API_KEY) }
    val scope = rememberCoroutineScope()
    val messages by chatStore.messages.collectAsState(initial = emptyList())
    var draft by remember { mutableStateOf("") }
    var sophiaState by remember { mutableStateOf(SophiaState.IDLE) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message Sophia…") },
                )
                Button(
                    onClick = {
                        val text = draft.trim()
                        if (text.isNotEmpty() && sophiaState != SophiaState.THINKING) {
                            draft = ""
                            scope.launch {
                                chatStore.addMessage(fromUser = true, text = text)
                                sophiaState = SophiaState.THINKING

                                val history = messages + PersistedMessage(true, text)

                                if (BuildConfig.ANTHROPIC_API_KEY.isBlank()) {
                                    chatStore.addMessage(
                                        fromUser = false,
                                        text = "My API key isn't set up yet, so I can't think properly. Add the ANTHROPIC_API_KEY secret to the repo and rebuild.",
                                    )
                                    sophiaState = SophiaState.IDLE
                                    return@launch
                                }

                                val result = ai.generateReply(history)
                                result.fold(
                                    onSuccess = { reply ->
                                        sophiaState = SophiaState.SPEAKING
                                        chatStore.addMessage(fromUser = false, text = reply)
                                        sophiaState = SophiaState.IDLE
                                    },
                                    onFailure = { error ->
                                        chatStore.addMessage(
                                            fromUser = false,
                                            text = "I hit an error reaching my reasoning service: ${error.message}",
                                        )
                                        sophiaState = SophiaState.IDLE
                                    },
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(if (sophiaState == SophiaState.THINKING) "…" else "Send")
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = onBack) { Text("←") }
                SophiaAvatar(
                    state = sophiaState,
                    modifier = Modifier.padding(start = 12.dp),
                    avatarSize = 48.dp,
                )
                Text(
                    text = if (sophiaState == SophiaState.THINKING) "Sophia is thinking…" else "Sophia",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(messages) { message ->
                    MessageBubble(fromUser = message.fromUser, text = message.text)
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(fromUser: Boolean, text: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .align(if (fromUser) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (fromUser) SophiaPrimary else SophiaSurface,
            ),
        ) {
            Text(text = text, modifier = Modifier.padding(12.dp))
        }
    }
}

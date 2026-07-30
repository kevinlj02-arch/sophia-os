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
private val SophiaBackground = Color(0xFF0B0C14)
private val SophiaSurface = Color(0xFF15161F)

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
        DashboardScreen(onOpenChat = { showChat = true })
    }
}

@Composable
private fun DashboardScreen(onOpenChat: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            Text(text = "Good day.", style = MaterialTheme.typography.headlineLarge)
            Text(
                text = "I'm Sophia. Your conversation history now persists across restarts.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            )
            Button(onClick = onOpenChat) {
                Text("Start a conversation")
            }
        }
    }
}

@Composable
private fun ChatScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val chatStore = remember { ChatStore(context) }
    val scope = rememberCoroutineScope()
    val messages by chatStore.messages.collectAsState(initial = emptyList())
    var draft by remember { mutableStateOf("") }

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
                        if (text.isNotEmpty()) {
                            draft = ""
                            scope.launch {
                                chatStore.addMessage(fromUser = true, text = text)
                                chatStore.addMessage(
                                    fromUser = false,
                                    text = "This is a demo build, so I can't reason yet — but I've saved that, and it'll still be here next time you open the app.",
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("Send")
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.padding(12.dp)) {
                Button(onClick = onBack) { Text("← Back") }
            }
            LazyColumn(
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

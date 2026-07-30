package com.sophia.os.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

private val SophiaPrimary = Color(0xFF6C63FF)
private val SophiaSecondary = Color(0xFF00D4C8)
private val SophiaBackground = Color(0xFF07080F)
private val SophiaSurface = Color(0xFF13141F)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
        ChatContainer(onBack = { showChat = false })
    } else {
        HomeScreen(onOpenChat = { showChat = true })
    }
}

@Composable
private fun ChatContainer(onBack: () -> Unit) {
    val context = LocalContext.current
    val chatStore = remember { ChatStore(context) }
    val ai = remember { SophiaAI(BuildConfig.ANTHROPIC_API_KEY) }
    val scope = rememberCoroutineScope()
    val messages by chatStore.messages.collectAsState(initial = emptyList())
    var draft by remember { mutableStateOf("") }
    var sophiaState by remember { mutableStateOf(SophiaState.IDLE) }

    ChatScreen(
        messages = messages,
        draft = draft,
        sophiaState = sophiaState,
        onDraftChange = { draft = it },
        onBack = onBack,
        onSend = {
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
    )
}

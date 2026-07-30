package com.sophia.os.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

private val SophiaPrimary = Color(0xFFD4AF37)
private val SophiaSecondary = Color(0xFFFFC94D)
private val SophiaBackground = Color(0xFF050507)
private val SophiaSurface = Color(0xFF121016)

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

private enum class Screen { HOME, CHAT, MEMORY }

@Composable
private fun SophiaDemoApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }

    when (screen) {
        Screen.HOME -> HomeScreen(
            onOpenChat = { screen = Screen.CHAT },
            onOpenMemory = { screen = Screen.MEMORY },
        )
        Screen.CHAT -> ChatContainer(onBack = { screen = Screen.HOME })
        Screen.MEMORY -> MemoryContainer(onBack = { screen = Screen.HOME })
    }
}

@Composable
private fun MemoryContainer(onBack: () -> Unit) {
    val context = LocalContext.current
    val memoryStore = remember { MemoryStore(context) }
    val scope = rememberCoroutineScope()
    val facts by memoryStore.facts.collectAsState(initial = emptyList())

    MemoryScreen(
        facts = facts,
        onBack = onBack,
        onForget = { fact -> scope.launch { memoryStore.forget(fact) } },
        onClearAll = { scope.launch { memoryStore.clearAll() } },
    )
}

@Composable
private fun ChatContainer(onBack: () -> Unit) {
    val context = LocalContext.current
    val chatStore = remember { ChatStore(context) }
    val memoryStore = remember { MemoryStore(context) }
    val ai = remember { SophiaAI(BuildConfig.ANTHROPIC_API_KEY) }
    val voice = remember { VoiceManager(context) }
    val scope = rememberCoroutineScope()
    val messages by chatStore.messages.collectAsState(initial = emptyList())
    var draft by remember { mutableStateOf("") }
    var sophiaState by remember { mutableStateOf(SophiaState.IDLE) }
    var isListening by remember { mutableStateOf(false) }
    var voiceOutputEnabled by remember { mutableStateOf(false) }
    var micGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || sophiaState == SophiaState.THINKING) return
        draft = ""
        scope.launch {
            chatStore.addMessage(fromUser = true, text = trimmed)
            sophiaState = SophiaState.THINKING
            val history = messages + PersistedMessage(true, trimmed)

            if (BuildConfig.ANTHROPIC_API_KEY.isBlank()) {
                chatStore.addMessage(
                    fromUser = false,
                    text = "My API key isn't set up yet. Add the ANTHROPIC_API_KEY secret and rebuild.",
                )
                sophiaState = SophiaState.IDLE
                return@launch
            }

            val knownFacts = memoryStore.currentFactsText()
            val result = ai.generateReply(history, knownFacts)
            result.fold(
                onSuccess = { reply ->
                    sophiaState = SophiaState.SPEAKING
                    chatStore.addMessage(fromUser = false, text = reply)
                    if (voiceOutputEnabled) voice.speak(reply)
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

    val latestSend = rememberUpdatedState { text: String -> sendMessage(text) }

    DisposableEffect(Unit) {
        voice.onResult = { spoken -> latestSend.value(spoken) }
        voice.onListeningChanged = { listening -> isListening = listening }
        voice.onError = { _ -> isListening = false }
        onDispose { voice.shutdown() }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        micGranted = granted
        if (granted) {
            isListening = true
            voice.startListening()
        }
    }

    ChatScreen(
        messages = messages,
        draft = draft,
        sophiaState = sophiaState,
        isListening = isListening,
        voiceOutputEnabled = voiceOutputEnabled,
        onDraftChange = { draft = it },
        onBack = onBack,
        onSend = { sendMessage(draft) },
        onToggleVoiceOutput = {
            voiceOutputEnabled = !voiceOutputEnabled
            if (!voiceOutputEnabled) voice.stopSpeaking()
        },
        onMicToggle = {
            if (isListening) {
                voice.stopListening()
                isListening = false
            } else {
                if (micGranted) {
                    isListening = true
                    voice.startListening()
                } else {
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        },
    )
}

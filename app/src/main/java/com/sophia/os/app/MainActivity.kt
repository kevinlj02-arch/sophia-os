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
import androidx.compose.runtime.LaunchedEffect
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

private enum class Screen { HOME, CONVERSATIONS, CHAT, MEMORY, TASKS, SETTINGS }

@Composable
private fun SophiaDemoApp() {
    val context = LocalContext.current
    val chatStore = remember { ChatStore(context) }
    val scope = rememberCoroutineScope()

    var screen by remember { mutableStateOf(Screen.HOME) }
    var activeConversationId by remember { mutableStateOf<Long?>(null) }

    fun openConversation(id: Long?) {
        if (id != null) {
            activeConversationId = id
            screen = Screen.CHAT
        } else {
            scope.launch {
                val newId = chatStore.createConversation()
                activeConversationId = newId
                screen = Screen.CHAT
            }
        }
    }

    when (screen) {
        Screen.HOME -> HomeScreen(
            onOpenChat = { screen = Screen.CONVERSATIONS },
            onOpenMemory = { screen = Screen.MEMORY },
            onOpenTasks = { screen = Screen.TASKS },
            onOpenSettings = { screen = Screen.SETTINGS },
        )
        Screen.CONVERSATIONS -> ConversationListContainer(
            chatStore = chatStore,
            onOpen = { id -> openConversation(id) },
            onNew = { openConversation(null) },
            onBack = { screen = Screen.HOME },
        )
        Screen.CHAT -> {
            val id = activeConversationId
            if (id == null) {
                LaunchedEffect(Unit) { screen = Screen.CONVERSATIONS }
            } else {
                ChatContainer(
                    conversationId = id,
                    chatStore = chatStore,
                    onBack = { screen = Screen.CONVERSATIONS },
                    onNewChat = { openConversation(null) },
                )
            }
        }
        Screen.MEMORY -> MemoryContainer(onBack = { screen = Screen.HOME })
        Screen.TASKS -> TaskContainer(onBack = { screen = Screen.HOME })
        Screen.SETTINGS -> SettingsContainer(onBack = { screen = Screen.HOME })
    }
}

@Composable
private fun ConversationListContainer(
    chatStore: ChatStore,
    onOpen: (Long) -> Unit,
    onNew: () -> Unit,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val conversations by chatStore.conversations.collectAsState(initial = emptyList())

    ConversationListScreen(
        conversations = conversations,
        onOpen = onOpen,
        onNew = onNew,
        onDelete = { id -> scope.launch { chatStore.deleteConversation(id) } },
        onBack = onBack,
    )
}

@Composable
private fun SettingsContainer(onBack: () -> Unit) {
    val context = LocalContext.current
    val settingsStore = remember { SettingsStore(context) }
    val chatStore = remember { ChatStore(context) }
    val memoryStore = remember { MemoryStore(context) }
    val taskStore = remember { TaskStore(context) }
    val scope = rememberCoroutineScope()
    val voiceDefault by settingsStore.voiceOutputDefault.collectAsState(initial = false)

    SettingsScreen(
        voiceOutputDefault = voiceDefault,
        onToggleVoiceDefault = { enabled -> scope.launch { settingsStore.setVoiceOutputDefault(enabled) } },
        onClearConversations = { scope.launch { chatStore.clearAll() } },
        onClearMemory = { scope.launch { memoryStore.clearAll() } },
        onClearTasks = { scope.launch { taskStore.clearAll() } },
        onBack = onBack,
    )
}

@Composable
private fun TaskContainer(onBack: () -> Unit) {
    val context = LocalContext.current
    val taskStore = remember { TaskStore(context) }
    val scope = rememberCoroutineScope()
    val tasks by taskStore.tasks.collectAsState(initial = emptyList())
    var draft by remember { mutableStateOf("") }

    TaskScreen(
        tasks = tasks,
        draft = draft,
        onDraftChange = { draft = it },
        onAdd = {
            val t = draft.trim()
            if (t.isNotEmpty()) {
                draft = ""
                scope.launch { taskStore.addTask(t) }
            }
        },
        onToggle = { id -> scope.launch { taskStore.toggleTask(id) } },
        onDelete = { id -> scope.launch { taskStore.deleteTask(id) } },
        onClearCompleted = { scope.launch { taskStore.clearCompleted() } },
        onBack = onBack,
    )
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
private fun ChatContainer(
    conversationId: Long,
    chatStore: ChatStore,
    onBack: () -> Unit,
    onNewChat: () -> Unit,
) {
    val context = LocalContext.current
    val memoryStore = remember { MemoryStore(context) }
    val settingsStore = remember { SettingsStore(context) }
    val ai = remember { SophiaAI(BuildConfig.ANTHROPIC_API_KEY) }
    val voice = remember { VoiceManager(context) }
    val scope = rememberCoroutineScope()
    val messages by chatStore.messagesFor(conversationId).collectAsState(initial = emptyList())
    val voiceDefault by settingsStore.voiceOutputDefault.collectAsState(initial = false)
    var draft by remember { mutableStateOf("") }
    var sophiaState by remember { mutableStateOf(SophiaState.IDLE) }
    var isListening by remember { mutableStateOf(false) }
    var voiceOutputEnabled by remember { mutableStateOf(false) }
    LaunchedEffect(voiceDefault) { voiceOutputEnabled = voiceDefault }
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
            chatStore.addMessage(conversationId, fromUser = true, text = trimmed)
            sophiaState = SophiaState.THINKING
            val history = messages + PersistedMessage(true, trimmed)

            if (BuildConfig.ANTHROPIC_API_KEY.isBlank()) {
                chatStore.addMessage(
                    conversationId,
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
                    chatStore.addMessage(conversationId, fromUser = false, text = reply)
                    if (voiceOutputEnabled) voice.speak(reply)
                    sophiaState = SophiaState.IDLE
                },
                onFailure = { error ->
                    chatStore.addMessage(
                        conversationId,
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
        onNewChat = onNewChat,
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

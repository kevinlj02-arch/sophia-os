package com.sophia.os.app

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.chatDataStore by preferencesDataStore(name = "sophia_chat")

class ChatStore(private val context: Context) {

    private val conversationsKey = stringPreferencesKey("conversations_json")

    val conversations: Flow<List<Conversation>> = context.chatDataStore.data.map { prefs ->
        parseConversations(prefs[conversationsKey] ?: "[]").sortedByDescending { it.createdAt }
    }

    fun messagesFor(conversationId: Long): Flow<List<PersistedMessage>> =
        context.chatDataStore.data.map { prefs ->
            parseConversations(prefs[conversationsKey] ?: "[]")
                .firstOrNull { it.id == conversationId }
                ?.messages
                ?: emptyList()
        }

    suspend fun createConversation(): Long {
        val newId = System.currentTimeMillis()
        context.chatDataStore.edit { prefs ->
            val current = parseConversations(prefs[conversationsKey] ?: "[]").toMutableList()
            current.add(Conversation(id = newId, title = "New conversation", createdAt = newId, messages = emptyList()))
            prefs[conversationsKey] = serializeConversations(current)
        }
        return newId
    }

    suspend fun addMessage(conversationId: Long, fromUser: Boolean, text: String) {
        context.chatDataStore.edit { prefs ->
            val current = parseConversations(prefs[conversationsKey] ?: "[]").toMutableList()
            val idx = current.indexOfFirst { it.id == conversationId }
            if (idx >= 0) {
                val convo = current[idx]
                val newMessages = convo.messages + PersistedMessage(fromUser, text)
                val newTitle = if (convo.title == "New conversation" && fromUser) {
                    text.trim().take(40).ifEmpty { "New conversation" }
                } else {
                    convo.title
                }
                current[idx] = convo.copy(title = newTitle, messages = newMessages)
                prefs[conversationsKey] = serializeConversations(current)
            }
        }
    }

    suspend fun deleteConversation(conversationId: Long) {
        context.chatDataStore.edit { prefs ->
            val current = parseConversations(prefs[conversationsKey] ?: "[]").toMutableList()
            current.removeAll { it.id == conversationId }
            prefs[conversationsKey] = serializeConversations(current)
        }
    }

    suspend fun clearAll() {
        context.chatDataStore.edit { prefs ->
            prefs[conversationsKey] = "[]"
        }
    }

    suspend fun latestConversationId(): Long? =
        conversations.first().firstOrNull()?.id

    private fun parseConversations(json: String): List<Conversation> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            val messagesArray = obj.optJSONArray("messages") ?: JSONArray()
            val messages = (0 until messagesArray.length()).map { j ->
                val m = messagesArray.getJSONObject(j)
                PersistedMessage(m.getBoolean("fromUser"), m.getString("text"))
            }
            Conversation(
                id = obj.getLong("id"),
                title = obj.optString("title", "Conversation"),
                createdAt = obj.optLong("createdAt", obj.getLong("id")),
                messages = messages,
            )
        }
    }

    private fun serializeConversations(conversations: List<Conversation>): String {
        val array = JSONArray()
        conversations.forEach { convo ->
            val messagesArray = JSONArray()
            convo.messages.forEach { m ->
                messagesArray.put(JSONObject().apply {
                    put("fromUser", m.fromUser)
                    put("text", m.text)
                })
            }
            array.put(JSONObject().apply {
                put("id", convo.id)
                put("title", convo.title)
                put("createdAt", convo.createdAt)
                put("messages", messagesArray)
            })
        }
        return array.toString()
    }
}

data class PersistedMessage(val fromUser: Boolean, val text: String)

data class Conversation(
    val id: Long,
    val title: String,
    val createdAt: Long,
    val messages: List<PersistedMessage>,
)

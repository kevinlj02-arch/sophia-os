package com.sophia.os.app

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.chatDataStore by preferencesDataStore(name = "sophia_chat")

class ChatStore(private val context: Context) {

    private val messagesKey = stringPreferencesKey("messages_json")

    val messages: Flow<List<PersistedMessage>> = context.chatDataStore.data.map { prefs ->
        val json = prefs[messagesKey] ?: "[]"
        parseMessages(json)
    }

    suspend fun addMessage(fromUser: Boolean, text: String) {
        context.chatDataStore.edit { prefs ->
            val current = parseMessages(prefs[messagesKey] ?: "[]").toMutableList()
            current.add(PersistedMessage(fromUser, text))
            prefs[messagesKey] = serializeMessages(current)
        }
    }

    private fun parseMessages(json: String): List<PersistedMessage> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            PersistedMessage(obj.getBoolean("fromUser"), obj.getString("text"))
        }
    }

    private fun serializeMessages(messages: List<PersistedMessage>): String {
        val array = JSONArray()
        messages.forEach { message ->
            val obj = JSONObject()
            obj.put("fromUser", message.fromUser)
            obj.put("text", message.text)
            array.put(obj)
        }
        return array.toString()
    }
}

data class PersistedMessage(val fromUser: Boolean, val text: String)

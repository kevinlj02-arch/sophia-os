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

private val Context.memoryDataStore by preferencesDataStore(name = "sophia_memory")

class MemoryStore(private val context: Context) {

    private val factsKey = stringPreferencesKey("facts_json")

    val facts: Flow<List<MemoryFact>> = context.memoryDataStore.data.map { prefs ->
        parseFacts(prefs[factsKey] ?: "[]")
    }

    suspend fun remember(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        context.memoryDataStore.edit { prefs ->
            val current = parseFacts(prefs[factsKey] ?: "[]").toMutableList()
            if (current.none { it.text.equals(clean, ignoreCase = true) }) {
                current.add(MemoryFact(clean, System.currentTimeMillis()))
                val trimmed = current.takeLast(50)
                prefs[factsKey] = serializeFacts(trimmed)
            }
        }
    }

    suspend fun forget(fact: MemoryFact) {
        context.memoryDataStore.edit { prefs ->
            val current = parseFacts(prefs[factsKey] ?: "[]").toMutableList()
            current.removeAll { it.text == fact.text && it.timestamp == fact.timestamp }
            prefs[factsKey] = serializeFacts(current)
        }
    }

    suspend fun clearAll() {
        context.memoryDataStore.edit { prefs ->
            prefs[factsKey] = "[]"
        }
    }

    suspend fun currentFactsText(): String {
        val facts = context.memoryDataStore.data.map { prefs ->
            parseFacts(prefs[factsKey] ?: "[]")
        }.first()
        return facts.joinToString("\n") { "- ${it.text}" }
    }

    private fun parseFacts(json: String): List<MemoryFact> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            MemoryFact(obj.getString("text"), obj.optLong("timestamp", 0L))
        }
    }

    private fun serializeFacts(facts: List<MemoryFact>): String {
        val array = JSONArray()
        facts.forEach { fact ->
            array.put(JSONObject().apply {
                put("text", fact.text)
                put("timestamp", fact.timestamp)
            })
        }
        return array.toString()
    }
}

data class MemoryFact(val text: String, val timestamp: Long)

package com.sophia.os.app

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.taskDataStore by preferencesDataStore(name = "sophia_tasks")

class TaskStore(private val context: Context) {

    private val tasksKey = stringPreferencesKey("tasks_json")

    val tasks: Flow<List<Task>> = context.taskDataStore.data.map { prefs ->
        parseTasks(prefs[tasksKey] ?: "[]")
    }

    suspend fun addTask(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        context.taskDataStore.edit { prefs ->
            val current = parseTasks(prefs[tasksKey] ?: "[]").toMutableList()
            current.add(Task(id = System.currentTimeMillis(), text = clean, done = false))
            prefs[tasksKey] = serializeTasks(current)
        }
    }

    suspend fun toggleTask(id: Long) {
        context.taskDataStore.edit { prefs ->
            val current = parseTasks(prefs[tasksKey] ?: "[]").toMutableList()
            val idx = current.indexOfFirst { it.id == id }
            if (idx >= 0) {
                current[idx] = current[idx].copy(done = !current[idx].done)
                prefs[tasksKey] = serializeTasks(current)
            }
        }
    }

    suspend fun deleteTask(id: Long) {
        context.taskDataStore.edit { prefs ->
            val current = parseTasks(prefs[tasksKey] ?: "[]").toMutableList()
            current.removeAll { it.id == id }
            prefs[tasksKey] = serializeTasks(current)
        }
    }

    suspend fun clearCompleted() {
        context.taskDataStore.edit { prefs ->
            val current = parseTasks(prefs[tasksKey] ?: "[]").filterNot { it.done }
            prefs[tasksKey] = serializeTasks(current)
        }
    }

    suspend fun clearAll() {
        context.taskDataStore.edit { prefs ->
            prefs[tasksKey] = "[]"
        }
    }

    private fun parseTasks(json: String): List<Task> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            Task(
                id = obj.getLong("id"),
                text = obj.getString("text"),
                done = obj.optBoolean("done", false),
            )
        }
    }

    private fun serializeTasks(tasks: List<Task>): String {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(JSONObject().apply {
                put("id", task.id)
                put("text", task.text)
                put("done", task.done)
            })
        }
        return array.toString()
    }
}

data class Task(val id: Long, val text: String, val done: Boolean)

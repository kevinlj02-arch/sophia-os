package com.sophia.os.app

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.noteDataStore by preferencesDataStore(name = "sophia_notes")

class NoteStore(private val context: Context) {

    private val notesKey = stringPreferencesKey("notes_json")

    val notes: Flow<List<Note>> = context.noteDataStore.data.map { prefs ->
        parseNotes(prefs[notesKey] ?: "[]").sortedByDescending { it.updatedAt }
    }

    suspend fun createNote(): Long {
        val newId = System.currentTimeMillis()
        context.noteDataStore.edit { prefs ->
            val current = parseNotes(prefs[notesKey] ?: "[]").toMutableList()
            current.add(Note(id = newId, title = "", body = "", updatedAt = newId))
            prefs[notesKey] = serializeNotes(current)
        }
        return newId
    }

    suspend fun updateNote(id: Long, title: String, body: String) {
        context.noteDataStore.edit { prefs ->
            val current = parseNotes(prefs[notesKey] ?: "[]").toMutableList()
            val idx = current.indexOfFirst { it.id == id }
            if (idx >= 0) {
                current[idx] = current[idx].copy(
                    title = title,
                    body = body,
                    updatedAt = System.currentTimeMillis(),
                )
                prefs[notesKey] = serializeNotes(current)
            }
        }
    }

    suspend fun deleteNote(id: Long) {
        context.noteDataStore.edit { prefs ->
            val current = parseNotes(prefs[notesKey] ?: "[]").toMutableList()
            current.removeAll { it.id == id }
            prefs[notesKey] = serializeNotes(current)
        }
    }

    suspend fun pruneEmpty() {
        context.noteDataStore.edit { prefs ->
            val current = parseNotes(prefs[notesKey] ?: "[]")
                .filterNot { it.title.isBlank() && it.body.isBlank() }
            prefs[notesKey] = serializeNotes(current)
        }
    }

    suspend fun clearAll() {
        context.noteDataStore.edit { prefs ->
            prefs[notesKey] = "[]"
        }
    }

    private fun parseNotes(json: String): List<Note> {
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            Note(
                id = obj.getLong("id"),
                title = obj.optString("title", ""),
                body = obj.optString("body", ""),
                updatedAt = obj.optLong("updatedAt", obj.getLong("id")),
            )
        }
    }

    private fun serializeNotes(notes: List<Note>): String {
        val array = JSONArray()
        notes.forEach { note ->
            array.put(JSONObject().apply {
                put("id", note.id)
                put("title", note.title)
                put("body", note.body)
                put("updatedAt", note.updatedAt)
            })
        }
        return array.toString()
    }
}

data class Note(
    val id: Long,
    val title: String,
    val body: String,
    val updatedAt: Long,
)

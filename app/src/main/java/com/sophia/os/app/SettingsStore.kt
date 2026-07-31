package com.sophia.os.app

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "sophia_settings")

class SettingsStore(private val context: Context) {

    private val voiceOutputKey = booleanPreferencesKey("voice_output_default")

    val voiceOutputDefault: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[voiceOutputKey] ?: false
    }

    suspend fun setVoiceOutputDefault(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[voiceOutputKey] = enabled
        }
    }
}

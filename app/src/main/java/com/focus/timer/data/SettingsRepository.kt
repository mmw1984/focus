package com.focus.timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    
    companion object {
        private val FOCUS_DURATION = intPreferencesKey("focus_duration")
        private val BREAK_DURATION = intPreferencesKey("break_duration")
        private val MICRO_BREAK_ENABLED = booleanPreferencesKey("micro_break_enabled")
        private val MICRO_BREAK_INTERVAL = intPreferencesKey("micro_break_interval")
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    }
    
    val settingsFlow: Flow<TimerSettings> = context.dataStore.data.map { preferences ->
        TimerSettings(
            focusDurationMinutes = preferences[FOCUS_DURATION] ?: 90,
            breakDurationMinutes = preferences[BREAK_DURATION] ?: 20,
            microBreakEnabled = preferences[MICRO_BREAK_ENABLED] ?: true,
            microBreakIntervalMinutes = preferences[MICRO_BREAK_INTERVAL] ?: 10,
            notificationEnabled = preferences[NOTIFICATION_ENABLED] ?: true,
            vibrationEnabled = preferences[VIBRATION_ENABLED] ?: true,
            soundEnabled = preferences[SOUND_ENABLED] ?: true
        )
    }
    
    suspend fun updateFocusDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[FOCUS_DURATION] = minutes
        }
    }
    
    suspend fun updateBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[BREAK_DURATION] = minutes
        }
    }
    
    suspend fun updateMicroBreakEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MICRO_BREAK_ENABLED] = enabled
        }
    }
    
    suspend fun updateMicroBreakInterval(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[MICRO_BREAK_INTERVAL] = minutes
        }
    }
    
    suspend fun updateNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = enabled
        }
    }
    
    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }
    
    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }
}

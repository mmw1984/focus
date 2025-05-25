package com.focus.timer.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.focus.timer.timer.SoundType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceManager(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        private val FOCUS_TIME_KEY = longPreferencesKey("focus_time")
        private val BREAK_TIME_KEY = longPreferencesKey("break_time")
        private val MICRO_BREAK_DURATION_KEY = longPreferencesKey("micro_break_duration")
        private val MICRO_BREAK_MIN_INTERVAL_KEY = longPreferencesKey("micro_break_min_interval")
        private val MICRO_BREAK_MAX_INTERVAL_KEY = longPreferencesKey("micro_break_max_interval")
        private val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")
        private val START_SOUND_KEY = stringPreferencesKey("start_sound")
        private val END_SOUND_KEY = stringPreferencesKey("end_sound")
    }

    val focusTime: Flow<Long> = dataStore.data.map { preferences ->
        preferences[FOCUS_TIME_KEY] ?: 90 * 60 * 1000L // 90 minutes default
    }

    val breakTime: Flow<Long> = dataStore.data.map { preferences ->
        preferences[BREAK_TIME_KEY] ?: 20 * 60 * 1000L // 20 minutes default
    }

    val microBreakDuration: Flow<Long> = dataStore.data.map { preferences ->
        preferences[MICRO_BREAK_DURATION_KEY] ?: 10 * 1000L // 10 seconds default
    }

    val microBreakInterval: Flow<Pair<Long, Long>> = dataStore.data.map { preferences ->
        val min = preferences[MICRO_BREAK_MIN_INTERVAL_KEY] ?: 3 * 60 * 1000L // 3 minutes
        val max = preferences[MICRO_BREAK_MAX_INTERVAL_KEY] ?: 5 * 60 * 1000L // 5 minutes
        Pair(min, max)
    }

    val isNotificationEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_ENABLED_KEY] ?: true
    }

    val startSound: Flow<SoundType> = dataStore.data.map { preferences ->
        val soundName = preferences[START_SOUND_KEY] ?: SoundType.TINK.name
        SoundType.valueOf(soundName)
    }

    val endSound: Flow<SoundType> = dataStore.data.map { preferences ->
        val soundName = preferences[END_SOUND_KEY] ?: SoundType.GLASS.name
        SoundType.valueOf(soundName)
    }

    suspend fun setFocusTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[FOCUS_TIME_KEY] = time
        }
    }

    suspend fun setBreakTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[BREAK_TIME_KEY] = time
        }
    }

    suspend fun setMicroBreakDuration(duration: Long) {
        dataStore.edit { preferences ->
            preferences[MICRO_BREAK_DURATION_KEY] = duration
        }
    }

    suspend fun setMicroBreakInterval(interval: Pair<Long, Long>) {
        dataStore.edit { preferences ->
            preferences[MICRO_BREAK_MIN_INTERVAL_KEY] = interval.first
            preferences[MICRO_BREAK_MAX_INTERVAL_KEY] = interval.second
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED_KEY] = enabled
        }
    }

    suspend fun setStartSound(sound: SoundType) {
        dataStore.edit { preferences ->
            preferences[START_SOUND_KEY] = sound.name
        }
    }

    suspend fun setEndSound(sound: SoundType) {
        dataStore.edit { preferences ->
            preferences[END_SOUND_KEY] = sound.name
        }
    }
}

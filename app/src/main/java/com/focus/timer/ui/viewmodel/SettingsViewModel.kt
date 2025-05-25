package com.focus.timer.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.focus.timer.data.SettingsRepository
import com.focus.timer.data.TimerSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings: StateFlow<TimerSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimerSettings()
        )
    
    fun updateFocusDuration(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateFocusDuration(minutes)
        }
    }
    
    fun updateBreakDuration(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateBreakDuration(minutes)
        }
    }
    
    fun updateMicroBreakEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateMicroBreakEnabled(enabled)
        }
    }
    
    fun updateMicroBreakInterval(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.updateMicroBreakInterval(minutes)
        }
    }
    
    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotificationEnabled(enabled)
        }
    }
    
    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVibrationEnabled(enabled)
        }
    }
    
    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSoundEnabled(enabled)
        }
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(
                    settingsRepository = SettingsRepository(context)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

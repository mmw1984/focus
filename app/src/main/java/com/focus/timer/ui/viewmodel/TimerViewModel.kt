package com.focus.timer.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.focus.timer.data.SettingsRepository
import com.focus.timer.data.TimerSettings
import com.focus.timer.notification.NotificationManager
import com.focus.timer.timer.TimerData
import com.focus.timer.timer.TimerManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class TimerViewModel(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val timerManager: TimerManager,
    private val notificationManager: NotificationManager
) : ViewModel() {
    
    val timerData: StateFlow<TimerData> = timerManager.timerData
    
    val settings: StateFlow<TimerSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimerSettings()
        )
    
    fun startFocusSession() {
        val currentSettings = settings.value
        val durationMs = currentSettings.focusDurationMinutes * 60 * 1000L
        
        timerManager.startTimer(
            durationMs = durationMs,
            isBreakTime = false
        ) {
            // Focus session completed
            onFocusSessionComplete()
        }
        
        // Start micro break timer if enabled
        if (currentSettings.microBreakEnabled) {
            val microBreakIntervalMs = currentSettings.microBreakIntervalMinutes * 60 * 1000L
            timerManager.startMicroBreakTimer(microBreakIntervalMs) {
                onMicroBreak()
            }
        }
        
        // Show notification
        if (currentSettings.notificationEnabled) {
            notificationManager.showTimerNotification(
                title = "專注時段開始",
                content = "專注時長: ${currentSettings.focusDurationMinutes} 分鐘",
                ongoing = true
            )
        }
    }
    
    fun startBreakSession() {
        val currentSettings = settings.value
        val durationMs = currentSettings.breakDurationMinutes * 60 * 1000L
        
        timerManager.startTimer(
            durationMs = durationMs,
            isBreakTime = true
        ) {
            // Break session completed
            onBreakSessionComplete()
        }
        
        // Show notification
        if (currentSettings.notificationEnabled) {
            notificationManager.showTimerNotification(
                title = "休息時段開始",
                content = "休息時長: ${currentSettings.breakDurationMinutes} 分鐘",
                ongoing = true
            )
        }
    }
    
    fun pauseTimer() {
        timerManager.pauseTimer()
        notificationManager.cancelNotification()
    }
    
    fun resumeTimer() {
        timerManager.resumeTimer()
        val currentSettings = settings.value
        
        if (currentSettings.notificationEnabled) {
            val title = if (timerData.value.isBreakTime) "休息時段繼續" else "專注時段繼續"
            notificationManager.showTimerNotification(
                title = title,
                content = "剩餘時間: ${formatTime(timerData.value.remainingTimeMs)}",
                ongoing = true
            )
        }
    }
    
    fun stopTimer() {
        timerManager.stopTimer()
        notificationManager.cancelNotification()
    }
    
    private fun onFocusSessionComplete() {
        val currentSettings = settings.value
        
        if (currentSettings.notificationEnabled) {
            notificationManager.showTimerNotification(
                title = "專注時段完成！",
                content = "恭喜！現在可以開始休息時段。"
            )
        }
        
        if (currentSettings.vibrationEnabled) {
            notificationManager.vibrate()
        }
        
        // Auto start break session
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // 2 second delay
            startBreakSession()
        }
    }
    
    private fun onBreakSessionComplete() {
        val currentSettings = settings.value
        
        if (currentSettings.notificationEnabled) {
            notificationManager.showTimerNotification(
                title = "休息時段完成！",
                content = "休息結束，準備開始下一個專注時段。"
            )
        }
        
        if (currentSettings.vibrationEnabled) {
            notificationManager.vibrate()
        }
    }
    
    private fun onMicroBreak() {
        val currentSettings = settings.value
        
        if (currentSettings.notificationEnabled) {
            notificationManager.showMicroBreakNotification()
        }
        
        if (currentSettings.vibrationEnabled) {
            notificationManager.vibrate(longArrayOf(0, 200, 100, 200))
        }
    }
    
    fun formatTime(timeMs: Long): String {
        return timerManager.formatTime(timeMs)
    }
    
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
                return TimerViewModel(
                    context = context,
                    settingsRepository = SettingsRepository(context),
                    timerManager = TimerManager(),
                    notificationManager = NotificationManager(context)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

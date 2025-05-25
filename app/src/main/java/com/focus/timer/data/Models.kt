package com.focus.timer.data

data class TimerSettings(
    val focusDurationMinutes: Int = 90,
    val breakDurationMinutes: Int = 20,
    val microBreakEnabled: Boolean = true,
    val microBreakIntervalMinutes: Int = 10,
    val notificationEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

data class FocusSession(
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int,
    val sessionType: SessionType,
    val completed: Boolean = false
)

enum class SessionType {
    FOCUS, BREAK
}

data class StatisticsData(
    val totalFocusTimeMinutes: Int = 0,
    val focusSessionsToday: Int = 0,
    val averageFocusTimeMinutes: Int = 0,
    val totalSessionsCompleted: Int = 0,
    val streakDays: Int = 0
)

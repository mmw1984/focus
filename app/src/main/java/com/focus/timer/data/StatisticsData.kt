package com.focus.timer.data

data class SessionData(
    val startTime: Long,
    val duration: Long,
    val type: String // "focus" or "break"
)

data class DailyStatistics(
    val date: String,
    val totalFocusTime: Long,
    val sessionCount: Int,
    val averageSessionLength: Long
)

data class Statistics(
    val totalSessions: Int = 0,
    val totalFocusTime: Long = 0L,
    val averageSessionLength: Long = 0L,
    val consecutiveDays: Int = 0,
    val dailyStats: List<DailyStatistics> = emptyList()
)

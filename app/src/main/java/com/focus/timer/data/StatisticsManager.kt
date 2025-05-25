package com.focus.timer.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class StatisticsManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("focus_statistics", Context.MODE_PRIVATE)
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private val _statistics = MutableStateFlow(Statistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    fun saveSession(sessionData: SessionData) {
        val today = dateFormat.format(Date(sessionData.startTime))
        
        // Update total statistics
        val currentStats = _statistics.value
        val newTotalSessions = currentStats.totalSessions + 1
        val newTotalFocusTime = currentStats.totalFocusTime + sessionData.duration
        val newAverageSessionLength = newTotalFocusTime / newTotalSessions
        
        // Update daily statistics
        val dailyStats = currentStats.dailyStats.toMutableList()
        val todayStatsIndex = dailyStats.indexOfFirst { it.date == today }
        
        if (todayStatsIndex >= 0) {
            val todayStats = dailyStats[todayStatsIndex]
            dailyStats[todayStatsIndex] = todayStats.copy(
                totalFocusTime = todayStats.totalFocusTime + sessionData.duration,
                sessionCount = todayStats.sessionCount + 1,
                averageSessionLength = (todayStats.totalFocusTime + sessionData.duration) / (todayStats.sessionCount + 1)
            )
        } else {
            dailyStats.add(
                DailyStatistics(
                    date = today,
                    totalFocusTime = sessionData.duration,
                    sessionCount = 1,
                    averageSessionLength = sessionData.duration
                )
            )
        }
        
        // Calculate consecutive days
        val sortedDays = dailyStats.sortedByDescending { it.date }
        var consecutiveDays = 0
        val today_date = Date()
        val calendar = Calendar.getInstance()
        
        for (day in sortedDays) {
            calendar.time = dateFormat.parse(day.date) ?: break
            val daysDiff = ((today_date.time - calendar.time.time) / (1000 * 60 * 60 * 24)).toInt()
            
            if (daysDiff == consecutiveDays) {
                consecutiveDays++
            } else {
                break
            }
        }
        
        val newStats = Statistics(
            totalSessions = newTotalSessions,
            totalFocusTime = newTotalFocusTime,
            averageSessionLength = newAverageSessionLength,
            consecutiveDays = consecutiveDays,
            dailyStats = dailyStats.sortedByDescending { it.date }
        )
        
        _statistics.value = newStats
        saveStatistics(newStats)
    }
    
    private fun loadStatistics() {
        val totalSessions = sharedPreferences.getInt("total_sessions", 0)
        val totalFocusTime = sharedPreferences.getLong("total_focus_time", 0L)
        val averageSessionLength = if (totalSessions > 0) totalFocusTime / totalSessions else 0L
        val consecutiveDays = sharedPreferences.getInt("consecutive_days", 0)
        
        // Load daily statistics
        val dailyStatsJson = sharedPreferences.getString("daily_stats", "")
        val dailyStats = parseDailyStats(dailyStatsJson ?: "")
        
        _statistics.value = Statistics(
            totalSessions = totalSessions,
            totalFocusTime = totalFocusTime,
            averageSessionLength = averageSessionLength,
            consecutiveDays = consecutiveDays,
            dailyStats = dailyStats
        )
    }
    
    private fun saveStatistics(stats: Statistics) {
        sharedPreferences.edit().apply {
            putInt("total_sessions", stats.totalSessions)
            putLong("total_focus_time", stats.totalFocusTime)
            putInt("consecutive_days", stats.consecutiveDays)
            putString("daily_stats", encodeDailyStats(stats.dailyStats))
            apply()
        }
    }
    
    private fun parseDailyStats(json: String): List<DailyStatistics> {
        if (json.isEmpty()) return emptyList()
        
        try {
            // Simple JSON parsing for daily stats
            val stats = mutableListOf<DailyStatistics>()
            val entries = json.split(";")
            
            for (entry in entries) {
                if (entry.isNotEmpty()) {
                    val parts = entry.split(",")
                    if (parts.size == 4) {
                        stats.add(
                            DailyStatistics(
                                date = parts[0],
                                totalFocusTime = parts[1].toLongOrNull() ?: 0L,
                                sessionCount = parts[2].toIntOrNull() ?: 0,
                                averageSessionLength = parts[3].toLongOrNull() ?: 0L
                            )
                        )
                    }
                }
            }
            
            return stats.sortedByDescending { it.date }
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    private fun encodeDailyStats(dailyStats: List<DailyStatistics>): String {
        return dailyStats.joinToString(";") { stats ->
            "${stats.date},${stats.totalFocusTime},${stats.sessionCount},${stats.averageSessionLength}"
        }
    }
    
    fun getDailyStats(days: Int): List<DailyStatistics> {
        return _statistics.value.dailyStats.take(days)
    }
    
    fun getWeeklyStats(): List<DailyStatistics> {
        return getDailyStats(7)
    }
    
    fun getMonthlyStats(): List<DailyStatistics> {
        return getDailyStats(30)
    }
}

package com.focus.timer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focus.timer.data.StatisticsManager
import com.focus.timer.data.StatisticsData
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val statisticsManager = remember { StatisticsManager(context) }
    var selectedPeriod by remember { mutableStateOf("今日") }
    var statistics by remember { mutableStateOf<List<StatisticsData>>(emptyList()) }
    var totalFocusTime by remember { mutableStateOf(0L) }
    var totalSessions by remember { mutableStateOf(0) }
    var averageSessionTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(selectedPeriod) {
        val stats = when (selectedPeriod) {
            "今日" -> statisticsManager.getTodayStatistics().first()
            "本週" -> statisticsManager.getWeeklyStatistics().first()
            "本月" -> statisticsManager.getMonthlyStatistics().first()
            else -> statisticsManager.getTodayStatistics().first()
        }
        statistics = stats
        
        totalFocusTime = stats.sumOf { it.focusTime }
        totalSessions = stats.size
        averageSessionTime = if (totalSessions > 0) totalFocusTime / totalSessions else 0L
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "專注統計",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )        
        // Period selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("今日", "本週", "本月").forEach { period ->
                FilterChip(
                    onClick = { selectedPeriod = period },
                    label = { Text(period) },
                    selected = selectedPeriod == period
                )
            }
        }
        
        // Summary cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Total Focus Time Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "總專注時間",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = formatTime(totalFocusTime),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Total Sessions Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "總場次",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = totalSessions.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        
        // Average Session Time Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "平均專注時間",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = formatTime(averageSessionTime),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        
        // Session List
        Text(
            text = "專注記錄",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (statistics.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "還沒有專注記錄",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "開始你的第一個專注時段吧！",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statistics.sortedByDescending { it.startTime }) { stat ->
                    StatisticsItem(stat)
                }
            }
        }
    }
}

@Composable
fun StatisticsItem(stat: StatisticsData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatDateTime(stat.startTime),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "專注時間: ${formatTime(stat.focusTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (stat.microBreaks > 0) {
                        Text(
                            text = "微休息: ${stat.microBreaks} 次",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Progress indicator
                val progressPercentage = (stat.focusTime.toFloat() / (90 * 60 * 1000)).coerceAtMost(1f)
                CircularProgressIndicator(
                    progress = progressPercentage,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = when {
                        progressPercentage >= 0.9f -> Color(0xFF4CAF50) // Green
                        progressPercentage >= 0.5f -> Color(0xFFFF9800) // Orange
                        else -> Color(0xFFF44336) // Red
                    }
                )
            }
        }
    }
}

private fun formatTime(timeInMillis: Long): String {
    val totalSeconds = timeInMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return when {
        hours > 0 -> "${hours}時${minutes}分"
        minutes > 0 -> "${minutes}分${seconds}秒"
        else -> "${seconds}秒"
    }
}

private fun formatDateTime(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timeInMillis))
}

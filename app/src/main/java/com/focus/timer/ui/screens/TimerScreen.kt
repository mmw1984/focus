package com.focus.timer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focus.timer.timer.TimerManager
import com.focus.timer.timer.TimerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    timerManager: TimerManager = viewModel(factory = TimerManager.provideFactory(LocalContext.current))
) {
    val timerState by timerManager.timerState.collectAsState()
    val remainingTime by timerManager.remainingTime.collectAsState()
    val sessionCount by timerManager.sessionCount.collectAsState()
    val totalFocusTime by timerManager.totalFocusTime.collectAsState()
    val focusTime by timerManager.focusTime.collectAsState()
    val breakTime by timerManager.breakTime.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // Session type indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (timerState) {
                    TimerState.LONG_BREAK -> MaterialTheme.colorScheme.secondary
                    TimerState.MICRO_BREAK -> MaterialTheme.colorScheme.tertiary
                    TimerState.FOCUSING -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Text(
                text = when (timerState) {
                    TimerState.FOCUSING -> "專注時段"
                    TimerState.LONG_BREAK -> "長休息"
                    TimerState.MICRO_BREAK -> "微休息 (10秒)"
                    TimerState.PAUSED -> "已暫停"
                    TimerState.IDLE -> "準備開始"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }
            )
        }        
        // Timer display with circular progress
        Box(
            modifier = Modifier
                .size(280.dp)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Custom circular progress
            Canvas(modifier = Modifier.size(240.dp)) {
                val strokeWidth = 12.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = size.center
                
                // Background circle
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.3f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                
                // Progress arc
                val progress = if (timerState == TimerState.FOCUSING || timerState == TimerState.LONG_BREAK) {
                    val totalTime = if (timerState == TimerState.FOCUSING) focusTime else breakTime
                    if (totalTime > 0) (totalTime - remainingTime).toFloat() / totalTime.toFloat() else 0f
                } else 0f
                
                if (progress > 0) {
                    drawArc(
                        color = when (timerState) {
                            TimerState.FOCUSING -> Color(0xFF4CAF50)
                            TimerState.LONG_BREAK -> Color(0xFF2196F3)
                            TimerState.MICRO_BREAK -> Color(0xFFFF9800)
                            else -> Color.Gray
                        },
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
            
            // Time display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTime(remainingTime),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 48.sp
                )
                
                if (timerState == TimerState.MICRO_BREAK) {
                    Text(
                        text = "閉眼休息",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            // Play/Pause button
            FloatingActionButton(
                onClick = {
                    when (timerState) {
                        TimerState.IDLE -> timerManager.startFocusSession()
                        TimerState.FOCUSING, TimerState.LONG_BREAK -> timerManager.pauseTimer()
                        TimerState.PAUSED -> timerManager.resumeTimer()
                        else -> {}
                    }
                },
                containerColor = when (timerState) {
                    TimerState.FOCUSING, TimerState.LONG_BREAK -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                }
            ) {
                Icon(
                    imageVector = when (timerState) {
                        TimerState.FOCUSING, TimerState.LONG_BREAK -> Icons.Default.Pause
                        else -> Icons.Default.PlayArrow
                    },
                    contentDescription = when (timerState) {
                        TimerState.FOCUSING, TimerState.LONG_BREAK -> "暫停"
                        else -> "開始"
                    },
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            // Stop button
            if (timerState != TimerState.IDLE) {
                FloatingActionButton(
                    onClick = { timerManager.stopTimer() },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "停止",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
            
            // Reset button
            if (timerState == TimerState.IDLE && sessionCount > 0) {
                FloatingActionButton(
                    onClick = { timerManager.resetTimer() },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "重置",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }        
        // Statistics display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "今日統計",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$sessionCount",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "完成次數",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatTime(totalFocusTime),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "專注時間",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(timeInMillis: Long): String {
    val totalSeconds = timeInMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "當前設定",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "專注時長: ${settings.focusDurationMinutes} 分鐘")
                Text(text = "休息時長: ${settings.breakDurationMinutes} 分鐘")
                if (settings.microBreakEnabled) {
                    Text(text = "微休息間隔: ${settings.microBreakIntervalMinutes} 分鐘")
                }
            }
        }
    }
}

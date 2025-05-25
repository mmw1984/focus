package com.focus.timer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focus.timer.timer.TimerManager
import com.focus.timer.timer.SoundType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    timerManager: TimerManager = viewModel(factory = TimerManager.provideFactory(LocalContext.current))
) {
    val focusTime by timerManager.focusTime.collectAsState()
    val breakTime by timerManager.breakTime.collectAsState()
    val microBreakDuration by timerManager.microBreakDuration.collectAsState()
    val microBreakInterval by timerManager.microBreakInterval.collectAsState()
    val isNotificationEnabled by timerManager.isNotificationEnabled.collectAsState()
    val startSound by timerManager.startSound.collectAsState()
    val endSound by timerManager.endSound.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "設定",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Timer Duration Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "計時器設定",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Focus Duration
                TimeSettingRow(
                    title = "專注時長",
                    value = focusTime / (60 * 1000),
                    onValueChange = { timerManager.updateFocusTime(it * 60 * 1000) },
                    minValue = 5,
                    maxValue = 180,
                    step = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Break Duration
                TimeSettingRow(
                    title = "休息時長",
                    value = breakTime / (60 * 1000),
                    onValueChange = { timerManager.updateBreakTime(it * 60 * 1000) },
                    minValue = 5,
                    maxValue = 60,
                    step = 5
                )
            }
        }
        
        // Micro Break Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "微休息設定",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Micro Break Duration
                TimeSettingRow(
                    title = "微休息時長",
                    value = microBreakDuration / 1000,
                    onValueChange = { timerManager.updateMicroBreakDuration(it * 1000) },
                    minValue = 5,
                    maxValue = 30,
                    step = 5,
                    unit = "秒"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Micro Break Interval Min
                TimeSettingRow(
                    title = "最小間隔",
                    value = microBreakInterval.first / (60 * 1000),
                    onValueChange = { 
                        timerManager.updateMicroBreakInterval(
                            Pair(it * 60 * 1000, microBreakInterval.second)
                        )
                    },
                    minValue = 1,
                    maxValue = 10,
                    step = 1
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Micro Break Interval Max
                TimeSettingRow(
                    title = "最大間隔",
                    value = microBreakInterval.second / (60 * 1000),
                    onValueChange = { 
                        timerManager.updateMicroBreakInterval(
                            Pair(microBreakInterval.first, it * 60 * 1000)
                        )
                    },
                    minValue = 2,
                    maxValue = 15,
                    step = 1
                )
            }
        }
        
        // Sound Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "音效設定",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Start Sound
                SoundSelectionRow(
                    title = "開始音效",
                    selectedSound = startSound,
                    onSoundSelected = { timerManager.updateStartSound(it) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // End Sound
                SoundSelectionRow(
                    title = "結束音效",
                    selectedSound = endSound,
                    onSoundSelected = { timerManager.updateEndSound(it) }
                )
            }
        }
        
        // Notification Settings
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "通知設定",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Notification Enabled
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "啟用通知",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = { timerManager.updateNotificationEnabled(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSettingRow(
    title: String,
    value: Long,
    onValueChange: (Long) -> Unit,
    minValue: Long,
    maxValue: Long,
    step: Long,
    unit: String = "分鐘"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    if (value > minValue) {
                        onValueChange(value - step)
                    }
                }
            ) {
                Text("-")
            }
            Text(
                text = "$value $unit",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OutlinedButton(
                onClick = {
                    if (value < maxValue) {
                        onValueChange(value + step)
                    }
                }
            ) {
                Text("+")
            }
        }
    }
}

@Composable
private fun SoundSelectionRow(
    title: String,
    selectedSound: SoundType,
    onSoundSelected: (SoundType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = { expanded = true }
            ) {
                Text(selectedSound.displayName)
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SoundType.values().forEach { sound ->
                DropdownMenuItem(
                    text = { Text(sound.displayName) },
                    onClick = {
                        onSoundSelected(sound)
                        expanded = false
                    }
                )
            }
        }
    }
}

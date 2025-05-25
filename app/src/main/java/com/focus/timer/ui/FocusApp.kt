package com.focus.timer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.focus.timer.timer.TimerManager
import com.focus.timer.ui.screens.TimerScreen
import com.focus.timer.ui.screens.SettingsScreen
import com.focus.timer.ui.screens.StatisticsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusApp() {
    val context = LocalContext.current
    val timerManager = remember { TimerManager(context) }
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Timer, contentDescription = "計時器") },
                    label = { Text("計時器") },
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        navController.navigate("timer") {
                            popUpTo("timer") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Timeline, contentDescription = "統計") },
                    label = { Text("統計") },
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        navController.navigate("statistics") {
                            popUpTo("timer") { saveState = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "設定") },
                    label = { Text("設定") },
                    selected = selectedItem == 2,
                    onClick = {
                        selectedItem = 2
                        navController.navigate("settings") {
                            popUpTo("timer") { saveState = true }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "timer",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("timer") {
                TimerScreen(timerManager = timerManager)
            }
            composable("statistics") {
                StatisticsScreen()
            }
            composable("settings") {
                SettingsScreen(timerManager = timerManager)
            }
        }
    }
}

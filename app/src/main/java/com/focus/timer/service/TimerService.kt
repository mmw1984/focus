package com.focus.timer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Binder
import com.focus.timer.timer.TimerManager
import com.focus.timer.timer.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TimerService : Service() {
    
    private val binder = TimerBinder()
    private var timerManager: TimerManager? = null
    private var serviceJob: Job? = null
    
    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }
    
    override fun onCreate() {
        super.onCreate()
        timerManager = TimerManager(this)
        
        // Monitor timer state for background operations
        serviceJob = CoroutineScope(Dispatchers.Main).launch {
            timerManager?.timerState?.collectLatest { state ->
                when (state) {
                    TimerState.FOCUSING, TimerState.MICRO_BREAK, TimerState.LONG_BREAK -> {
                        // Keep service alive during active timer states
                    }
                    TimerState.IDLE -> {
                        // Can stop service when idle
                    }
                    TimerState.PAUSED -> {
                        // Keep service alive but reduce activity
                    }
                }
            }
        }
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceJob?.cancel()
        timerManager = null
    }
    
    fun getTimerManager(): TimerManager? = timerManager
}

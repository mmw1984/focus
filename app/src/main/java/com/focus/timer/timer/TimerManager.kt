package com.focus.timer.timer

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.focus.timer.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class TimerState {
    IDLE,
    FOCUSING,
    MICRO_BREAK,
    LONG_BREAK,
    PAUSED
}

enum class SoundType(val displayName: String, val resourceId: Int) {
    TINK("叮叮", R.raw.notification),
    GLASS("玻璃", R.raw.notification),
    BELL("鈴聲", R.raw.notification),
    HERO("英雄", R.raw.notification),
    SUBMARINE("潛艇", R.raw.notification),
    BASSO("低音", R.raw.notification),
    BOTTLE("瓶子", R.raw.notification),
    FROG("青蛙", R.raw.notification),
    FUNK("放克", R.raw.notification),
    MORSE("摩爾斯", R.raw.notification),
    PING("乒", R.raw.notification),
    POP("啵", R.raw.notification),
    PURR("咕嚕", R.raw.notification),
    SOSUMI("Sosumi", R.raw.notification)
}

class TimerManager(private val context: Context) : ViewModel() {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundMap = mutableMapOf<SoundType, Int>()

    private var timerJob: Job? = null
    private var microBreakJob: Job? = null

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()

    private val _sessionCount = MutableStateFlow(0)
    val sessionCount: StateFlow<Int> = _sessionCount.asStateFlow()

    private val _totalFocusTime = MutableStateFlow(0L)
    val totalFocusTime: StateFlow<Long> = _totalFocusTime.asStateFlow()

    // Settings
    private val _focusTime = MutableStateFlow(90 * 60 * 1000L) // 90 minutes default
    val focusTime: StateFlow<Long> = _focusTime.asStateFlow()

    private val _breakTime = MutableStateFlow(20 * 60 * 1000L) // 20 minutes default
    val breakTime: StateFlow<Long> = _breakTime.asStateFlow()

    private val _microBreakDuration = MutableStateFlow(10 * 1000L) // 10 seconds default
    val microBreakDuration: StateFlow<Long> = _microBreakDuration.asStateFlow()

    private val _microBreakInterval = MutableStateFlow(Pair(3 * 60 * 1000L, 5 * 60 * 1000L)) // 3-5 minutes
    val microBreakInterval: StateFlow<Pair<Long, Long>> = _microBreakInterval.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(true)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    private val _startSound = MutableStateFlow(SoundType.TINK)
    val startSound: StateFlow<SoundType> = _startSound.asStateFlow()

    private val _endSound = MutableStateFlow(SoundType.GLASS)
    val endSound: StateFlow<SoundType> = _endSound.asStateFlow()

    private var currentSessionStartTime = 0L
    private var nextMicroBreakTime = 0L

    init {
        loadSounds()
    }

    private fun loadSounds() {
        try {
            SoundType.values().forEach { soundType ->
                soundMap[soundType] = soundPool.load(context, soundType.resourceId, 1)
            }
        } catch (e: Exception) {
            // Handle sound loading error
        }
    }

    fun startFocusSession() {
        if (_timerState.value != TimerState.IDLE) return

        _timerState.value = TimerState.FOCUSING
        _remainingTime.value = _focusTime.value
        currentSessionStartTime = System.currentTimeMillis()
        scheduleNextMicroBreak()

        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 && _timerState.value == TimerState.FOCUSING) {
                delay(1000)
                _remainingTime.value -= 1000

                // Check for micro break
                if (System.currentTimeMillis() >= nextMicroBreakTime) {
                    startMicroBreak()
                    break
                }
            }

            if (_remainingTime.value <= 0) {
                completeFocusSession()
            }
        }
    }

    private fun scheduleNextMicroBreak() {
        val (minInterval, maxInterval) = _microBreakInterval.value
        val randomInterval = Random.nextLong(minInterval, maxInterval)
        nextMicroBreakTime = System.currentTimeMillis() + randomInterval
    }

    private fun startMicroBreak() {
        if (_timerState.value != TimerState.FOCUSING) return

        _timerState.value = TimerState.MICRO_BREAK
        playSound(_startSound.value)

        microBreakJob = viewModelScope.launch {
            delay(_microBreakDuration.value)
            endMicroBreak()
        }
    }

    private fun endMicroBreak() {
        if (_timerState.value != TimerState.MICRO_BREAK) return

        _timerState.value = TimerState.FOCUSING
        playSound(_endSound.value)
        scheduleNextMicroBreak()

        // Resume focus timer
        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 && _timerState.value == TimerState.FOCUSING) {
                delay(1000)
                _remainingTime.value -= 1000

                if (System.currentTimeMillis() >= nextMicroBreakTime) {
                    startMicroBreak()
                    break
                }
            }

            if (_remainingTime.value <= 0) {
                completeFocusSession()
            }
        }
    }

    private fun completeFocusSession() {
        val sessionDuration = _focusTime.value
        _sessionCount.value += 1
        _totalFocusTime.value += sessionDuration

        startLongBreak()
    }

    private fun startLongBreak() {
        _timerState.value = TimerState.LONG_BREAK
        _remainingTime.value = _breakTime.value

        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 && _timerState.value == TimerState.LONG_BREAK) {
                delay(1000)
                _remainingTime.value -= 1000
            }

            if (_remainingTime.value <= 0) {
                completeLongBreak()
            }
        }
    }

    private fun completeLongBreak() {
        _timerState.value = TimerState.IDLE
    }

    fun pauseTimer() {
        when (_timerState.value) {
            TimerState.FOCUSING, TimerState.LONG_BREAK -> {
                _timerState.value = TimerState.PAUSED
                timerJob?.cancel()
                microBreakJob?.cancel()
            }
            else -> {}
        }
    }

    fun resumeTimer() {
        if (_timerState.value != TimerState.PAUSED) return

        if (_remainingTime.value > 0) {
            when {
                _sessionCount.value == 0 -> {
                    _timerState.value = TimerState.FOCUSING
                    scheduleNextMicroBreak()
                    startFocusTimer()
                }
                else -> {
                    _timerState.value = TimerState.LONG_BREAK
                    startBreakTimer()
                }
            }
        }
    }

    private fun startFocusTimer() {
        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 && _timerState.value == TimerState.FOCUSING) {
                delay(1000)
                _remainingTime.value -= 1000

                if (System.currentTimeMillis() >= nextMicroBreakTime) {
                    startMicroBreak()
                    break
                }
            }

            if (_remainingTime.value <= 0) {
                completeFocusSession()
            }
        }
    }

    private fun startBreakTimer() {
        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0 && _timerState.value == TimerState.LONG_BREAK) {
                delay(1000)
                _remainingTime.value -= 1000
            }

            if (_remainingTime.value <= 0) {
                completeLongBreak()
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        microBreakJob?.cancel()
        _timerState.value = TimerState.IDLE
        _remainingTime.value = 0L
    }

    fun resetTimer() {
        stopTimer()
        _sessionCount.value = 0
        _totalFocusTime.value = 0L
        _remainingTime.value = 0L
    }

    private fun playSound(soundType: SoundType) {
        soundMap[soundType]?.let { soundId ->
            try {
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            } catch (e: Exception) {
                // Handle sound play error
            }
        }
    }

    // Settings update methods
    fun updateFocusTime(time: Long) {
        _focusTime.value = time
    }

    fun updateBreakTime(time: Long) {
        _breakTime.value = time
    }

    fun updateMicroBreakDuration(duration: Long) {
        _microBreakDuration.value = duration
    }

    fun updateMicroBreakInterval(interval: Pair<Long, Long>) {
        _microBreakInterval.value = interval
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        _isNotificationEnabled.value = enabled
    }

    fun updateStartSound(sound: SoundType) {
        _startSound.value = sound
    }

    fun updateEndSound(sound: SoundType) {
        _endSound.value = sound
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        microBreakJob?.cancel()
        soundPool.release()
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = 
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TimerManager(context) as T
                }
            }
    }
}

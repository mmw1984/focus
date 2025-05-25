# Android Focus Timer App

A comprehensive focus timer app for Android, based on the macOS Focus timer application. This app helps you maintain productivity with structured focus sessions, micro-breaks, and detailed statistics tracking.

## Features

### Core Timer Functionality
- **90-minute focus sessions** with 20-minute long breaks
- **Random micro-breaks** every 3-5 minutes during focus sessions
- **10-second micro-break duration** for quick mental resets
- **Pause/Resume functionality** for interruptions
- **Background operation** continues timing when app is not visible

### Timer States
- **IDLE**: Ready to start a new session
- **FOCUSING**: Active 90-minute focus period
- **MICRO_BREAK**: Random 10-second break during focus
- **LONG_BREAK**: 20-minute break after completing focus session
- **PAUSED**: Timer paused, can be resumed

### Sound & Notifications
- **Multiple sound options**: Gentle Chime, Bell, Notification, Nature, None
- **Separate start/end sounds**: Customizable for session begins and ends
- **Vibration support**: Optional haptic feedback
- **Push notifications**: Alerts for session changes and micro-breaks
- **Background notifications**: Keep track of timer when app is minimized

### Statistics & Analytics
- **Real-time tracking**: All focus sessions automatically recorded
- **Multi-period views**: Today, This Week, This Month statistics
- **Comprehensive metrics**:
  - Total focus time
  - Number of completed sessions
  - Average session duration
  - Micro-break frequency
- **Session history**: Detailed list of all past focus sessions
- **Progress indicators**: Visual representation of session completion

### User Interface
- **Modern Material 3 Design**: Clean, intuitive interface
- **Chinese localization**: Full Chinese language support
- **Circular progress indicator**: Visual timer with customizable colors
- **Bottom navigation**: Easy switching between Timer, Statistics, and Settings
- **Dark/Light theme support**: Follows system preferences

### Settings & Customization
- **Timer durations**: Adjust focus time (30-120 minutes) and break time (10-30 minutes)
- **Micro-break settings**: Configure interval (3-10 minutes) and duration (5-30 seconds)
- **Sound preferences**: Choose different sounds for start and end events
- **Notification controls**: Enable/disable notifications and vibration
- **Data persistence**: All settings saved automatically

## Technical Implementation

### Architecture
- **MVVM Pattern**: Clean separation of concerns
- **Jetpack Compose**: Modern reactive UI framework
- **StateFlow**: Reactive state management
- **DataStore**: Preferences and settings storage
- **Room Database**: Statistics data persistence (via StatisticsManager)

### Key Components

#### TimerManager
- Central timer logic with coroutine-based timing
- State machine handling all timer states
- Sound management with SoundPool
- Automatic statistics recording
- Settings integration

#### StatisticsManager
- Persistent storage of focus session data
- Time-based data aggregation (daily/weekly/monthly)
- Data export capabilities
- Performance optimized queries

#### NotificationManager
- Multi-channel notification system
- Background timer tracking
- Interactive notification actions
- Vibration pattern support

#### TimerService
- Background service for continuous timer operation
- Foreground service notifications
- System integration for reliable timing
- Battery optimization handling

### Data Models

#### StatisticsData
```kotlin
data class StatisticsData(
    val id: Long = 0,
    val startTime: Long,
    val focusTime: Long,
    val microBreaks: Int,
    val completed: Boolean
)
```

#### TimerState
```kotlin
enum class TimerState {
    IDLE, FOCUSING, MICRO_BREAK, LONG_BREAK, PAUSED
}
```

#### SoundType
```kotlin
enum class SoundType {
    GENTLE_CHIME, BELL, NOTIFICATION, NATURE, NONE
}
```

## Installation & Setup

### Prerequisites
- Android SDK 24+ (Android 7.0)
- Compile SDK 34 (Android 14)
- Kotlin 1.9+
- Jetpack Compose BOM 2023.10.01

### Build Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Add actual sound files to `app/src/main/res/raw/` directory
5. Run `./gradlew assembleDebug` to build
6. Install APK on device or emulator

### Required Permissions
- `POST_NOTIFICATIONS`: For timer notifications
- `VIBRATE`: For haptic feedback
- `WAKE_LOCK`: To keep timer running
- `FOREGROUND_SERVICE`: For background timer operation

## Usage Guide

### Starting a Focus Session
1. Open the app and navigate to the Timer tab
2. Adjust settings if needed (Settings tab)
3. Tap the "開始專注" (Start Focus) button
4. The 90-minute timer begins with random micro-breaks

### During Focus Session
- **Micro-breaks**: Automatically triggered every 3-5 minutes for 10 seconds
- **Pause**: Tap pause button if you need to interrupt
- **Background**: Timer continues when app is minimized
- **Notifications**: Receive alerts for state changes

### After Focus Session
- **Long break**: Automatic 20-minute break period
- **Statistics**: Session data automatically recorded
- **Review**: Check Statistics tab for session details

### Viewing Statistics
1. Navigate to Statistics (統計) tab
2. Select time period: Today, This Week, This Month
3. View summary cards for key metrics
4. Scroll through individual session history
5. Monitor progress with visual indicators

### Customizing Settings
1. Go to Settings (設定) tab
2. Adjust timer durations with +/- buttons
3. Select preferred sounds for start/end events
4. Enable/disable notifications and vibration
5. Settings save automatically

## File Structure

```
app/src/main/java/com/focus/timer/
├── MainActivity.kt                    # Main activity and entry point
├── data/
│   ├── PreferenceManager.kt          # Settings storage management
│   ├── StatisticsData.kt             # Data models
│   └── StatisticsManager.kt          # Statistics data operations
├── notification/
│   └── NotificationManager.kt        # Notification system
├── service/
│   └── TimerService.kt               # Background timer service
├── timer/
│   └── TimerManager.kt               # Core timer logic
└── ui/
    ├── FocusApp.kt                   # Main app navigation
    ├── screens/
    │   ├── TimerScreen.kt            # Timer interface
    │   ├── StatisticsScreen.kt       # Statistics display
    │   └── SettingsScreen.kt         # Settings interface
    └── theme/
        └── Theme.kt                  # Material Design theme

app/src/main/res/
├── values/
│   └── strings.xml                   # Chinese localized strings
├── raw/                              # Sound files directory
└── drawable/
    └── ic_timer.xml                  # Timer icon
```

## Dependencies

```kotlin
// Core Android
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
implementation 'androidx.activity:activity-compose:1.8.2'

// Jetpack Compose
implementation platform('androidx.compose:compose-bom:2023.10.01')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'androidx.navigation:navigation-compose:2.7.5'

// Data & Preferences
implementation 'androidx.datastore:datastore-preferences:1.0.0'
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'

// Background Work
implementation 'androidx.work:work-runtime-ktx:2.9.0'

// Permissions
implementation 'com.google.accompanist:accompanist-permissions:0.32.0'
```

## Future Enhancements

### Planned Features
- **Data export**: CSV/JSON export of statistics
- **Custom timer presets**: Multiple timer configurations
- **Achievement system**: Unlockable badges and rewards
- **Focus insights**: Advanced analytics and trends
- **Cloud sync**: Backup and restore across devices
- **Widget support**: Home screen timer widget
- **Do Not Disturb integration**: Automatic DND during focus
- **Website blocking**: Optional distraction blocking

### Technical Improvements
- **Room database migration**: Full database implementation
- **WorkManager integration**: More robust background processing
- **Jetpack Compose animations**: Enhanced UI transitions
- **Accessibility improvements**: Better screen reader support
- **Performance optimization**: Memory and battery usage improvements
- **Unit test coverage**: Comprehensive testing suite

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Submit pull request with detailed description
5. Ensure all CI checks pass

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

Based on the original macOS Focus application, adapted for Android with modern Jetpack Compose architecture and Material Design 3 principles.

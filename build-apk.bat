@echo off
echo Building Focus Timer APK...
echo.

echo Step 1: Cleaning project...
call gradlew clean

echo.
echo Step 2: Building debug APK...
call gradlew assembleDebug --stacktrace --info

echo.
echo Step 3: Looking for APK file...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✅ SUCCESS! APK found at:
    echo %CD%\app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo APK size:
    dir "app\build\outputs\apk\debug\app-debug.apk"
    echo.
    echo You can now install this APK on your Android device!
) else (
    echo ❌ APK not found. Build may have failed.
    echo Check the output above for errors.
)

echo.
echo Press any key to exit...
pause

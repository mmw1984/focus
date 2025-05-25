# APK Build and Location Guide

## Expected APK Locations

After running `.\gradlew assembleDebug`, the APK will be created at:

### Debug APK
```
c:\Users\mmw1984\Desktop\zeenzeen\Android\app\build\outputs\apk\debug\app-debug.apk
```

### Release APK (if you run `.\gradlew assembleRelease`)
```
c:\Users\mmw1984\Desktop\zeenzeen\Android\app\build\outputs\apk\release\app-release.apk
```

## How to Check Build Status

1. **Wait for build completion** - Look for "BUILD SUCCESSFUL" message
2. **Check for APK file**:
   ```powershell
   Get-ChildItem -Path "app\build\outputs\apk" -Recurse -Name "*.apk"
   ```

## If Build Fails

Common issues and solutions:

### 1. Check for Compilation Errors
```powershell
.\gradlew assembleDebug --stacktrace
```

### 2. Check Java/Android SDK Setup
```powershell
.\gradlew --version
```

### 3. Clean and Rebuild
```powershell
.\gradlew clean
.\gradlew assembleDebug
```

### 4. Check Build Logs
Look for error messages in the console output during build.

## Alternative: Use Android Studio

If Gradle build fails:
1. Open Android Studio
2. File → Open → Select the Android folder
3. Build → Generate Signed Bundle/APK
4. Choose APK → Debug
5. APK will be created in same location

## Manual APK Location

You can also navigate manually in File Explorer to:
```
C:\Users\mmw1984\Desktop\zeenzeen\Android\app\build\outputs\apk\debug\
```

The APK file will be named `app-debug.apk` (approximately 10-20 MB in size).

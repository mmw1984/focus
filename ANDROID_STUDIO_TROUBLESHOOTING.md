# Android Studio Build Issues - Troubleshooting Guide

## Common Android Studio Build Problems & Solutions

### Issue 1: Project Sync Failures

**Symptoms:**
- "Gradle sync failed" errors
- "SDK not found" messages  
- Missing dependencies

**Solutions:**
```
1. File → Invalidate Caches and Restart → Invalidate and Restart
2. File → Sync Project with Gradle Files
3. Build → Clean Project, then Build → Rebuild Project
4. Tools → SDK Manager → Install missing SDK components
```

### Issue 2: SDK/Build Tools Missing

**Error Messages:**
- "Android SDK is missing"
- "Build tools version not found"
- "Compilation target not found"

**Fix in Android Studio:**
```
1. Open SDK Manager (Tools → SDK Manager)
2. Install:
   - Android 14.0 (API 34) - SDK Platform
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools
   - Android SDK Tools
3. Apply changes and restart
```

### Issue 3: Gradle Version Conflicts

**Symptoms:**
- "Gradle version incompatible" 
- Build scripts failing

**Solution:**
1. Check gradle-wrapper.properties
2. Update to compatible Gradle version
3. Update Android Gradle Plugin version

### Issue 4: Java/JDK Issues

**Error Messages:**
- "JAVA_HOME not set"
- "Unsupported Java version"

**Fix:**
```
1. File → Project Structure → SDK Location
2. Set JDK location to Android Studio's embedded JDK
3. Or install Java 17 separately
```

### Issue 5: Memory/Performance Issues

**Symptoms:**
- Android Studio crashes
- Very slow builds
- Out of memory errors

**Solution:**
```
1. Help → Edit Custom VM Options
2. Add or modify:
   -Xmx4096m
   -XX:MaxPermSize=1024m
3. Restart Android Studio
```

## Alternative Build Methods

Since Android Studio isn't working, here are other approaches:

### Method 1: Fix Command Line Build

The gradle build failed due to missing Java. Let's fix this:

**Install OpenJDK 17:**
```powershell
# Download and install from: https://adoptium.net/
# Or use Windows Package Manager:
winget install EclipseAdoptium.Temurin.17.JDK
```

**Verify Installation:**
```powershell
java -version
javac -version
```

**Build APK:**
```powershell
.\gradlew clean assembleDebug
```

### Method 2: Use Online Build Service

**GitHub Actions (Free):**
1. Push code to GitHub repository
2. Create `.github/workflows/build.yml`
3. GitHub will build APK automatically
4. Download from Actions tab

**GitLab CI (Free):**
1. Push to GitLab repository  
2. Configure `.gitlab-ci.yml`
3. Download built APK from pipelines

### Method 3: Use Docker

**Build in isolated environment:**
```bash
# Create Dockerfile for Android builds
# Build APK in clean Docker container
# No local Java/Android setup needed
```

### Method 4: Pre-built APK Creation

Let me create a simplified version that builds easily:

## Simplified Build Process

Let's try to identify and fix the specific Android Studio issues you're experiencing.

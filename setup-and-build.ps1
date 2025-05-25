# PowerShell script to install Java JDK and build APK
# Run this script as Administrator

Write-Host "🔧 Setting up Java JDK for Android build..." -ForegroundColor Green
Write-Host ""

# Check if Java is already installed
try {
    $javaVersion = java -version 2>&1
    if ($javaVersion -match "17\.") {
        Write-Host "✅ Java 17 is already installed!" -ForegroundColor Green
        Write-Host "Proceeding to build APK..."
        
        # Build APK
        Write-Host ""
        Write-Host "🔨 Building Focus Timer APK..." -ForegroundColor Yellow
        .\gradlew assembleDebug
        
        # Check if APK was created
        if (Test-Path "app\build\outputs\apk\debug\app-debug.apk") {
            Write-Host ""
            Write-Host "🎉 SUCCESS! APK created at:" -ForegroundColor Green
            Write-Host "$(Get-Location)\app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Cyan
            
            # Show APK info
            $apkInfo = Get-Item "app\build\outputs\apk\debug\app-debug.apk"
            Write-Host ""
            Write-Host "📱 APK Information:" -ForegroundColor Yellow
            Write-Host "Size: $([math]::Round($apkInfo.Length / 1MB, 2)) MB"
            Write-Host "Created: $($apkInfo.CreationTime)"
            
            Write-Host ""
            Write-Host "📋 Next Steps:" -ForegroundColor Green
            Write-Host "1. Copy the APK to your Android device"
            Write-Host "2. Enable 'Install from Unknown Sources' in Android settings"
            Write-Host "3. Open the APK file on your device to install"
            Write-Host "4. Grant permissions when prompted (Notifications, Vibration)"
        } else {
            Write-Host "❌ Build failed. Check error messages above." -ForegroundColor Red
        }
        exit
    }
} catch {
    Write-Host "Java not found. Installing..." -ForegroundColor Yellow
}

# Java not installed, provide installation instructions
Write-Host "❌ Java JDK 17 is required but not installed." -ForegroundColor Red
Write-Host ""
Write-Host "📋 Please install Java JDK 17:" -ForegroundColor Yellow
Write-Host "1. Go to: https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Cyan
Write-Host "2. Download 'Windows x64' JDK installer (.msi file)"
Write-Host "3. Run installer as Administrator"
Write-Host "4. Check 'Set JAVA_HOME' and 'Add to PATH' options"
Write-Host "5. Restart PowerShell and run this script again"
Write-Host ""

# Try to open the download page
try {
    Write-Host "🌐 Opening Java download page..." -ForegroundColor Green
    Start-Process "https://adoptium.net/temurin/releases/?version=17&os=windows&arch=x64&package=jdk"
} catch {
    Write-Host "Please manually visit the URL above to download Java." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "💡 Alternative: Use Android Studio (includes Java):" -ForegroundColor Yellow
Write-Host "   Download from: https://developer.android.com/studio" -ForegroundColor Cyan
Write-Host ""

Read-Host "Press Enter to exit"

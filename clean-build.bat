@echo off
echo Cleaning Gradle cache and stopping daemon...

REM Stop Gradle daemon
call gradlew.bat --stop

REM Clean build
call gradlew.bat clean

REM Clear Gradle cache (optional, uncomment if needed)
REM rmdir /s /q .gradle

echo Done! Now try building again with: gradlew.bat assembleDebug
pause

@echo off
echo Deep cleaning Gradle cache and build files...

REM Stop all Gradle daemons
call gradlew.bat --stop

REM Clean build directories
if exist build rmdir /s /q build
if exist app\build rmdir /s /q app\build
if exist .gradle rmdir /s /q .gradle

REM Clean Gradle cache in user home (optional, use carefully)
REM if exist %USERPROFILE%\.gradle\caches rmdir /s /q %USERPROFILE%\.gradle\caches
REM if exist %USERPROFILE%\.kotlin rmdir /s /q %USERPROFILE%\.kotlin

echo Deep clean complete!
echo Now run: gradlew.bat assembleDebug
pause

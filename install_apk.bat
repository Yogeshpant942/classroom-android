@echo off
echo ===================================================
echo School Management System - Android App Installer
echo ===================================================
set ADB="C:\Users\defaultuser0\AppData\Local\Android\Sdk\platform-tools\adb.exe"

echo Checking connected devices...
%ADB% devices

echo Installing APK: d:\new_project\android\app\build\outputs\apk\debug\app-debug.apk
%ADB% install -r "d:\new_project\android\app\build\outputs\apk\debug\app-debug.apk"
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Installation Successful!
    echo Launching App on device...
    %ADB% shell am start -n com.practicum.schoolmanagement/.ui.MainActivity
) else (
    echo.
    echo Installation failed or no device detected. Make sure USB debugging is enabled on your phone or start an emulator.
)
pause

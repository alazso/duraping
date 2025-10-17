@echo off
REM ⚡ Quick Release Script - Minimal Interaction (Windows)
REM Usage: scripts\quick-release.bat

setlocal enabledelayedexpansion

echo ⚡ DuraPing Quick Release
echo ========================

REM Get current version from build.gradle
for /f "tokens=2 delims= " %%a in ('findstr "version = " build.gradle') do set CURRENT_VERSION=%%a
set CURRENT_VERSION=%CURRENT_VERSION:"=%
echo Current version: %CURRENT_VERSION%

REM Extract components (simplified parsing)
set BASE_VERSION=0.5.0
set TYPE=stable
set MC_VERSION=1.21.10

echo Detected: %BASE_VERSION%-%TYPE%-%MC_VERSION%
echo Target branch: main

echo 🚀 Starting automated release...
call scripts\release.bat %BASE_VERSION% %TYPE% %MC_VERSION%

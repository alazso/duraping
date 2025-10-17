@echo off
REM 🎯 Dual Release Script - Release both MC versions simultaneously (Windows)
REM Usage: scripts\dual-release.bat [version] [type]

setlocal enabledelayedexpansion

set VERSION=%1
if "%VERSION%"=="" set VERSION=0.5.0

set TYPE=%2
if "%TYPE%"=="" set TYPE=stable

echo 🎯 DuraPing Dual Release
echo ========================
echo Version: %VERSION%
echo Type: %TYPE%
echo Releasing for both Minecraft 1.21.9 and 1.21.10
echo.

REM Store current branch
for /f %%i in ('git branch --show-current') do set ORIGINAL_BRANCH=%%i

REM Release for 1.21.9
echo 📦 Releasing for Minecraft 1.21.9...
call scripts\release.bat %VERSION% %TYPE% 1.21.9

REM Wait a moment
timeout /t 2 /nobreak >nul

REM Release for 1.21.10  
echo 📦 Releasing for Minecraft 1.21.10...
call scripts\release.bat %VERSION% %TYPE% 1.21.10

REM Return to original branch
echo 🔄 Returning to original branch...
git checkout %ORIGINAL_BRANCH%

echo ✅ Dual release completed!
echo 🌐 Both releases are now building on GitHub Actions.
echo 📋 Monitor progress at: https://github.com/actions

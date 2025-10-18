@echo off
REM 🚀 DuraPing Automated Release Script (Windows)
REM Usage: scripts\release.bat [version] [type] [mc_version]

setlocal enabledelayedexpansion

REM Default values
set VERSION=%1
if "%VERSION%"=="" set VERSION=0.5.0

set TYPE=%2
if "%TYPE%"=="" set TYPE=stable

set MC_VERSION=%3
if "%MC_VERSION%"=="" set MC_VERSION=1.21.10

REM Determine branch
if "%MC_VERSION%"=="1.21.9" (
    set BRANCH=stable/1.21.9
) else (
    set BRANCH=main
)

set FULL_VERSION=%VERSION%-%TYPE%-%MC_VERSION%
set TAG_NAME=v%FULL_VERSION%

echo 🚀 DuraPing Release Automation
echo ================================
echo Version: %FULL_VERSION%
echo Type: %TYPE%
echo Minecraft: %MC_VERSION%
echo Branch: %BRANCH%
echo Tag: %TAG_NAME%
echo.

REM Check if we're on the right branch
for /f %%i in ('git branch --show-current') do set CURRENT_BRANCH=%%i
if not "%CURRENT_BRANCH%"=="%BRANCH%" (
    echo ⚠️  Switching to %BRANCH% branch...
    git checkout %BRANCH%
)

REM Check for uncommitted changes (warn only)
git diff-index --quiet HEAD --
if errorlevel 1 (
    echo ⚠️  Note: Uncommitted changes detected.
    echo The release will use the current committed state.
)

REM Create and push tag (axion will handle versioning automatically)
echo 🏷️  Creating tag %TAG_NAME%...
git tag -s %TAG_NAME% -m "Release %FULL_VERSION% - DuraPing for Minecraft %MC_VERSION%"

echo 📤 Pushing changes and tag...
git push origin %BRANCH%
git push origin %TAG_NAME%

echo ✅ Release %FULL_VERSION% initiated!
echo 🌐 GitHub Actions will now build and publish the release.
echo 📋 Monitor progress at: https://github.com/actions

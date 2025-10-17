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

REM Check for uncommitted changes
git diff-index --quiet HEAD --
if errorlevel 1 (
    echo ❌ Uncommitted changes detected!
    echo Please commit or stash your changes before releasing.
    exit /b 1
)

REM Update version in build.gradle
echo 📝 Updating version in build.gradle...
powershell -Command "(Get-Content build.gradle) -replace 'version = \".*\"', 'version = \"%FULL_VERSION%\"' | Set-Content build.gradle"

REM Update Minecraft version in gradle.properties
echo 📝 Updating Minecraft version in gradle.properties...
powershell -Command "(Get-Content gradle.properties) -replace 'minecraft_version=.*', 'minecraft_version=%MC_VERSION%' | Set-Content gradle.properties"

REM Commit version changes
echo 💾 Committing version changes...
git add build.gradle gradle.properties
git commit -S -m "chore: bump version to %FULL_VERSION%

- Update version to %FULL_VERSION%
- Update Minecraft version to %MC_VERSION%
- Prepare for %TYPE% release"

REM Create and push tag
echo 🏷️  Creating tag %TAG_NAME%...
git tag -s %TAG_NAME% -m "Release %FULL_VERSION% - DuraPing for Minecraft %MC_VERSION%"

echo 📤 Pushing changes and tag...
git push origin %BRANCH%
git push origin %TAG_NAME%

echo ✅ Release %FULL_VERSION% initiated!
echo 🌐 GitHub Actions will now build and publish the release.
echo 📋 Monitor progress at: https://github.com/actions

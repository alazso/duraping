# Installation Guide

This guide will walk you through installing DuraPing for Minecraft 1.21.9 Fabric.

## Prerequisites

Before installing DuraPing, ensure you have the following:

### 1. Java 21 or Higher

DuraPing requires Java 21. Check your version:

**Windows**: Open Command Prompt and run:
```cmd
java -version
```

**macOS/Linux**: Open Terminal and run:
```bash
java -version
```

If you need to install Java 21:
- Download from [Adoptium](https://adoptium.net/) (recommended)
- Or use [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)

### 2. Minecraft 1.21.9

Launch the official Minecraft launcher and ensure you can run version 1.21.9.

## Step-by-Step Installation

### Step 1: Install Fabric Loader

1. **Download Fabric Installer**
   - Visit [https://fabricmc.net/use/](https://fabricmc.net/use/)
   - Download the installer for your operating system
   - Run the `.jar` or `.exe` file

2. **Run the Installer**
   - Select Minecraft version: `1.21.9`
   - Select Loader version: `0.17.3` or higher
   - Click "Install"
   - Leave "Create profile" checked

3. **Verify Installation**
   - Open Minecraft Launcher
   - You should see a new profile: "fabric-loader-1.21.9"

### Step 2: Install Fabric API

Fabric API is required for DuraPing to function.

1. **Download Fabric API**
   - Visit [Modrinth](https://modrinth.com/mod/fabric-api)
   - Or visit [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
   - Download version `0.134.0+1.21.9` or higher for Minecraft 1.21.9

2. **Install Fabric API**
   - Locate your Minecraft mods folder:
     - **Windows**: `%APPDATA%\.minecraft\mods`
     - **macOS**: `~/Library/Application Support/minecraft/mods`
     - **Linux**: `~/.minecraft/mods`
   - Create the `mods` folder if it doesn't exist
   - Copy the Fabric API `.jar` file into the `mods` folder

### Step 3: Install DuraPing

1. **Download DuraPing**
   - Visit the [Releases page](https://github.com/redlynxlabs/duraping/releases)
   - Download the latest `.jar` file for Minecraft 1.21.9

2. **Install DuraPing**
   - Copy the DuraPing `.jar` file into your `mods` folder
   - It should be in the same folder as Fabric API

### Step 4: Install Optional Dependencies

**Mod Menu** (Highly Recommended)

Mod Menu provides in-game access to DuraPing's configuration screen.

1. Download from [Modrinth](https://modrinth.com/mod/modmenu) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/modmenu)
2. Place in your `mods` folder
3. Cloth Config will be automatically installed

**Cloth Config**

Cloth Config is required for the configuration UI. If you install Mod Menu, this is automatically included. Otherwise:

1. Download from [Modrinth](https://modrinth.com/mod/cloth-config)
2. Place in your `mods` folder

### Step 5: Launch Minecraft

1. **Select Fabric Profile**
   - Open Minecraft Launcher
   - Select the "fabric-loader-1.21.9" profile
   - Click "Play"

2. **Verify Installation**
   - From the main menu, click "Mods" (if Mod Menu is installed)
   - Look for "DuraPing" in the mod list
   - You should see version information and a "Config" button

3. **Check Logs**
   - If the game doesn't launch, check `logs/latest.log` for errors
   - See [Troubleshooting](Troubleshooting) for common issues

## Updating DuraPing

To update to a newer version:

1. **Download the new version** from the [Releases page](https://github.com/redlynxlabs/duraping/releases)
2. **Delete the old version** from your `mods` folder
3. **Copy the new version** into your `mods` folder
4. **Restart Minecraft**

Your configuration file (`config/duraping.json`) will be preserved automatically.

## Uninstalling DuraPing

To completely remove DuraPing:

1. **Delete the mod file** from your `mods` folder
2. **Delete the config file** (optional): `config/duraping.json`
3. **Restart Minecraft**

## Installing on a Server

**DuraPing is client-only and does not need to be installed on servers.**

Players can use DuraPing on:
- Vanilla servers
- Fabric servers (with or without DuraPing installed)
- Forge servers (client-side Fabric mods work)
- Realms

Simply install DuraPing in your client as described above.

## MultiMC / Prism Launcher Installation

### Using MultiMC or Prism Launcher:

1. **Create a new instance**
   - Click "Add Instance"
   - Select Minecraft version `1.21.9`
   - Install Fabric Loader

2. **Add mods**
   - Right-click the instance
   - Click "Edit Instance"
   - Go to the "Loader mods" tab
   - Click "Add" and select:
     - Fabric API
     - DuraPing
     - Mod Menu (optional)

3. **Launch**
   - Click "Launch" on your instance

## Troubleshooting Installation

### Game Won't Launch

**Check Java Version**
```
java -version
```
Must be Java 21 or higher.

**Check Fabric API Version**
Ensure you downloaded the correct version for Minecraft 1.21.9.

**Check Logs**
Open `logs/latest.log` and look for errors mentioning "duraping" or "fabric".

### Mod Not Showing in Mod Menu

**Verify File Placement**
- DuraPing `.jar` should be in the `mods` folder
- Fabric API should also be in the `mods` folder

**Check Mod File Name**
- File should be named like: `duraping-0.3.1.jar`
- Should NOT be in a subfolder

**Verify Minecraft Version**
Ensure you're launching with the Fabric 1.21.9 profile.

### Configuration Button Missing

If you see DuraPing in the mod list but no "Config" button:

1. Install [Mod Menu](https://modrinth.com/mod/modmenu)
2. Cloth Config will be automatically installed
3. Restart Minecraft

Without Mod Menu, you can still edit `config/duraping.json` manually.

## Next Steps

Now that DuraPing is installed:

- **[Configure the mod](Configuration)** - Customize thresholds and alerts
- **[Learn the features](Features-and-Usage)** - Discover what DuraPing can do
- **[Set up keybindings](Keybindings)** - Customize keyboard controls

---

Need more help? Visit the [Troubleshooting](Troubleshooting) page or [open an issue](https://github.com/redlynxlabs/duraping/issues).


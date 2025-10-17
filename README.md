# DuraPing

[![Latest Build](https://img.shields.io/github/actions/workflow/status/redlynxlabs/duraping/release.yml?branch=main&logo=github&style=for-the-badge&label=Build)](https://github.com/redlynxlabs/duraping/actions/workflows/release.yml)
[![Latest Release](https://img.shields.io/github/v/release/redlynxlabs/duraping?logo=github&style=for-the-badge&label=Latest)](https://github.com/redlynxlabs/duraping/releases/latest)
[![Previous Build](https://img.shields.io/github/actions/workflow/status/redlynxlabs/duraping/release.yml?branch=stable/1.21.9&logo=github&style=for-the-badge&label=Build)](https://github.com/redlynxlabs/duraping/actions/workflows/release.yml)
[![License](https://img.shields.io/github/license/redlynxlabs/duraping?logo=github&style=for-the-badge)](https://github.com/redlynxlabs/duraping/blob/main/LICENSE)
[![Discord](https://img.shields.io/discord/1402745018682179624?label=discord&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/hologram)

**Lightweight durability alerts for Minecraft 1.21.9 (Fabric)**

DuraPing is a client-side quality-of-life mod that helps you never break your favorite tools, armor, or elytra again. Get timely alerts through chat, sound, screen flash, or toast (hotbar) notifications when your gear is running low on durability.

## Features

### Core Alerts
- **Multi-tier threshold system**: Warn (25%), Danger (10%), Critical (5%), and 1-durability emergency alerts
- **Chat notifications**: Discrete in-game messages with item name and remaining uses
- **Custom sounds**: Subtle audio cues (warning and critical) with subtitle support
- **Screen flash**: Brief, low-opacity vignette overlay for visual alerts
- **Toast notifications**: Optional advancement-style pop-ups

### Smart Monitoring
- **All equipment slots**: Main hand, offhand, and all armor pieces
- **Spam prevention**: Debounced alerts with configurable cooldowns (default: 10s)
- **One-shot threshold crossing**: Alerts trigger only when durability crosses a threshold downward
- **Per-item overrides**: Customize thresholds for specific items (e.g., elytra at 15%)

### Quality of Life
- **Elytra flight guard**: Extra warning when attempting to fly with critically low elytra
- **Keybinds**: Toggle alerts, snooze for 5 minutes, or check current item durability on demand
- **Mod Menu integration**: Clean Cloth Config GUI for all settings
- **Client-only**: No server-side mod required; works on vanilla servers

## Installation

### Requirements
- **Minecraft**: 1.21.9
- **Fabric Loader**: 0.17.3 or higher
- **Fabric API**: 0.134.0+1.21.9 or higher
- **Java**: 21 or higher

### Optional Dependencies
- **Mod Menu**: For in-game config screen
- **Cloth Config**: For config UI (auto-installed)

### Steps
1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.9
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download DuraPing from [Releases](https://github.com/redlynxlabs/duraping/releases)
4. Place all JARs in `.minecraft/mods/`
5. Launch Minecraft with the Fabric profile

## Configuration

### In-Game (Mod Menu)
Press `Escape` → `Mods` → find "DuraPing" → `Config` button

Configure:
- Enable/disable individual alert types
- Set global thresholds (warn/danger/critical %)
- Adjust cooldown timings
- Toggle elytra guard

### File-Based (`config/duraping.json`)
```json
{
  "enabled": true,
  "chat": true,
  "sound": true,
  "flash": true,
  "toast": false,
  "elytraGuard": true,
  "warn": 25,
  "danger": 10,
  "critical": 5,
  "cooldownMs": 10000,
  "overrides": {
    "minecraft:elytra": { "warn": 15, "danger": 7, "critical": 1 }
  }
}
```

### Per-Item Overrides
Add entries to `overrides` using the namespaced item ID:
```json
"overrides": {
  "minecraft:elytra": { "warn": 15, "danger": 7, "critical": 1 },
  "minecraft:netherite_pickaxe": { "warn": 30, "danger": 15, "critical": 10 }
}
```

## Default Keybinds

| Key | Action |
|-----|--------|
| **Numpad 7** | Toggle DuraPing on/off |
| **Numpad 8** | Snooze alerts for 5 minutes |
| **Numpad 9** | Show main hand durability |

*Rebind in `Options` → `Controls` → `DuraPing`*

## Multiplayer & Server Compatibility

**Fully client-side** — Works on:
- Vanilla servers
- Modded servers (without DuraPing installed)
- Singleplayer

No server-side installation or permissions required.

## Development

### Building from Source
```bash
git clone https://github.com/redlynxlabs/duraping.git
cd duraping
./gradlew build
# Output: build/libs/duraping-v<version>-1.21.9.jar
```

### Branching Strategy
- `main`: Stable releases only
- `dev`: Active development branch
- Open PRs from feature branches to `dev`
- Merge `dev` → `main` for releases

### Versioning & Releases
We use [Axion Release](https://github.com/allegro/axion-release-plugin) to read version from Git tags and follow semantic versioning with Conventional Commits.

**Creating a release:**
```bash
# 1. Merge dev to main
git checkout main
git merge dev
git push origin main

# 2. Create signed version tag
git tag -s -a v0.2.0 -m "Release 0.2.0"  # Minor version
git tag -s -a v0.1.1 -m "Release 0.1.1"  # Patch version
git tag -s -a v1.0.0 -m "Release 1.0.0"  # Major version

# 3. Push the signed tag to trigger release workflow
git push origin v0.2.0
```

### Commit Convention
Follow [Conventional Commits](https://www.conventionalcommits.org/):
```
feat: add elytra flight guard
fix: prevent duplicate alerts on threshold edge
chore: update Fabric API to 0.111.0
docs: clarify per-item override syntax
```

## Roadmap / Future Features

- [ ] **Effective uses left**: Display Unbreaking-adjusted durability estimates
- [ ] **Sound pack support**: Let users provide custom alert sounds
- [ ] **Per-slot thresholds**: Different thresholds for armor vs. tools
- [ ] **Durability HUD**: Optional on-screen durability overlay

## Contributing

Contributions welcome! Please:
1. Fork and create a feature branch from `dev`
2. Follow Conventional Commits
3. Add/update tests as needed
4. Open PR to `dev` (not `main`)

## License

[GNU GPLv3](LICENSE) — Free to use, modify, and distribute.

## Credits

- **Organization**: [RedLynx Labs](https://redlynx.io) • [GitHub](https://github.com/redlynxlabs)
- **Author**: [redkey](https://github.com/redkeysh)
- Built with [Fabric](https://fabricmc.net/), [Cloth Config](https://github.com/shedaniel/cloth-config), and [Mod Menu](https://github.com/TerraformersMC/ModMenu)

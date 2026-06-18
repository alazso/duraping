<div align="center">

# DuraPing

**Never break your favorite gear again.** Timely durability alerts through chat, sound, screen flash, or hotbar toast, the moment a tool, weapon, or armor piece runs low.

[![Build](https://img.shields.io/github/actions/workflow/status/redlynxlabs/duraping/ci.yml?branch=main&style=for-the-badge&label=build)](https://github.com/redlynxlabs/duraping/actions)
[![Downloads](https://img.shields.io/modrinth/dt/duraping?style=for-the-badge&logo=modrinth&label=downloads&color=00AF5C)](https://modrinth.com/mod/duraping)
[![Minecraft](https://img.shields.io/badge/Fabric%20·%20NeoForge-1.21.9%2B-2b2d31?style=for-the-badge)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/license-MIT-2b2d31?style=for-the-badge)](https://github.com/redlynxlabs/duraping/blob/main/LICENSE)

[![Modrinth](https://img.shields.io/badge/Modrinth-00AF5C?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/mod/duraping)
[![CurseForge](https://img.shields.io/badge/CurseForge-F16436?style=for-the-badge&logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/duraping)
[![Source](https://img.shields.io/badge/Source-0f0f0f?style=for-the-badge&logo=github&logoColor=white)](https://github.com/redlynxlabs/duraping)
[![Discord](https://img.shields.io/discord/1402745018682179624?style=for-the-badge&logo=discord&logoColor=white&label=discord&color=5865F2)](https://discord.gg/hologram)

</div>

<br>

DuraPing is a **client-side quality-of-life mod** that watches the durability of everything you have equipped and warns you before it breaks. Pick how you want to be told (chat, sound, a screen flash, or a hotbar toast), set the thresholds that matter to you, and stop losing enchanted gear to one swing too many. It runs on **Fabric and NeoForge**, needs nothing on the server, and stays out of your way until your gear is actually in danger.

<br>

## At a glance

|   |   |
|---|---|
| 🔔 **Multi-tier alerts** | Warn, Danger, and Critical thresholds, plus an emergency alert at **2 durability** that bypasses snooze. |
| 📺 **Four alert channels** | Chat, custom sound, screen flash, and hotbar toast. Each one toggles independently. |
| 🎒 **Every slot watched** | Main hand, offhand, and all four armor pieces, every tick. |
| 🤫 **Smart anti-spam** | Per-tier cooldowns, hysteresis re-arming, and activity-aware quieting while you mine. |
| 🎚️ **Per-item thresholds** | Override warn, danger, and critical percentages for specific items by id. |
| ⏱️ **Snooze and toggle** | Keybinds to silence alerts for a few minutes or switch the mod off entirely. |
| 🧩 **In-game config** | A Cloth Config screen on both loaders (Fabric via Mod Menu, NeoForge via the Mods list), plus a plain JSON file. |
| 🌍 **Client-side only** | Works on vanilla servers, modded servers, and singleplayer. No server install. |

<br>

## Every feature

**Alerts**
- **Multi-tier thresholds**: Warn (25%), Danger (10%), and Critical (5%) by default, each with its own color and cooldown.
- **Emergency alert at 2 durability**: ignores snooze, flashes the screen, repeats the critical sound, and prints a banner so you cannot miss it.
- **Chat messages** with the item name and exact uses remaining.
- **Custom sounds** for warn and critical, with subtitle support.
- **Screen flash**: a brief, low-opacity vignette for a glance-free heads-up.
- **Hotbar toast** messages for a quieter, text-only nudge.

**Smart monitoring**
- Watches the **main hand, offhand, and all armor** continuously.
- Fires **once on a downward threshold crossing**, then re-arms only after you recover, with configurable **hysteresis** for Mending or modded durability regen.
- **Activity-aware mode** stretches warn and danger cooldowns while you are continuously mining, so a long dig does not spam you.
- Optional **quiet-below-warn**: visual-only alerts in the warn tier, no sound or chat.

**Customization**
- **Per-item overrides**: different thresholds for an elytra, a netherite pickaxe, or anything else, keyed by item id.
- Tunable thresholds, per-tier cooldowns, and snooze duration.
- **In-game config screen on both loaders** via Cloth Config: Fabric through Mod Menu, NeoForge through the Mods list (Mods → DuraPing → Config). The JSON config file still works everywhere.

**Controls**
- Keybinds to **toggle**, **snooze or cancel**, and **show main-hand durability** on demand (defaults on the numpad, fully rebindable).

**Auto-swap** (off by default)
- Swaps a worn-down tool or armor piece for a more durable copy from your inventory, through server-synced inventory actions that hold up on real servers.

<br>

## Configuration

The config lives at `config/duraping.json` and is created on first launch.

```json
{
  "enabled": true,
  "chat": true,
  "sound": true,
  "flash": false,
  "toast": false,
  "warn": 25,
  "danger": 10,
  "critical": 5,
  "warnCooldownSec": 30,
  "dangerCooldownSec": 15,
  "criticalCooldownSec": 7,
  "overrides": {
    "minecraft:elytra": { "warn": 15, "danger": 7, "critical": 1 }
  }
}
```

You can also edit everything live from the in-game config screen: **Escape → Mods → DuraPing → Config** on both loaders (Fabric also needs Mod Menu; both need Cloth Config). Per-item overrides use the namespaced item id, for example `minecraft:netherite_pickaxe`.

<br>

## Keybinds

| Key | Action |
|-----|--------|
| **Numpad 7** | Toggle DuraPing on or off |
| **Numpad 8** | Snooze for 5 minutes, or cancel an active snooze |
| **Numpad 9** | Show current main-hand durability |
| **Numpad 0** | Manual auto-swap trigger |

Rebind any of these under **Options, Controls, DuraPing**.

<br>

## Requirements

|   |   |
|---|---|
| **Loader** | Fabric or NeoForge |
| **Minecraft** | 1.21.9, 1.21.10, or 1.21.11 |
| **Java** | 21 |

**Optional:** Cloth Config for the in-game settings screen (plus Mod Menu on Fabric). Everything works without them through the config file.

<br>

## Multiplayer

DuraPing is **fully client-side**. It reads only your own inventory and never talks to the server, so it works on vanilla servers, modded servers that do not have it, and in singleplayer, with no permissions or server-side install required.

<br>

## Found a bug? Have an idea?

Bug reports and feature ideas are welcome. Open a [GitHub issue](https://github.com/redlynxlabs/duraping/issues) with your loader and Minecraft version, your DuraPing version, and any relevant console output. For quick questions or to help test, join the [Discord](https://discord.gg/hologram).

<br>

<div align="center">

Released under the [MIT License](https://github.com/redlynxlabs/duraping/blob/main/LICENSE).

</div>

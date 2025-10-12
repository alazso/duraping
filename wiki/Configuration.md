# Configuration Guide

DuraPing offers extensive configuration options to tailor alerts to your playstyle. This guide covers all available settings.

## Accessing Configuration

### In-Game (Recommended)

With [Mod Menu](https://modrinth.com/mod/modmenu) installed:

1. Press `Escape` to open the game menu
2. Click **"Mods"**
3. Find **"DuraPing"** in the list
4. Click the **"Config"** button

Settings are organized into categories for easy navigation.

### File-Based

You can manually edit the configuration file:

**Location**: `.minecraft/config/duraping.json`

The file is created automatically after first launch. Edit with any text editor and restart Minecraft to apply changes.

## Configuration Categories

### General Settings

These settings control the core behavior of DuraPing.

#### Master Toggle
**Config Key**: `enabled`  
**Type**: Boolean  
**Default**: `true`

Enables or disables all durability monitoring and alerts. When disabled:
- No durability checks are performed
- Keybindings still work (you can toggle back on)
- Configuration remains accessible

**Note**: This acts as a kill switch, not an override. Individual alert types respect their own settings.

#### Chat Alerts
**Config Key**: `chat`  
**Type**: Boolean  
**Default**: `true`

Displays durability warnings in the chat window. Messages include:
- Item name
- Remaining uses
- Color coding based on severity:
  - **Yellow/Gold** - Warn
  - **Orange/Red** - Danger
  - **Dark Red + Bold** - Critical

#### Sound Alerts
**Config Key**: `sound`  
**Type**: Boolean  
**Default**: `true`

Plays audio cues when durability thresholds are crossed.

Two custom sounds:
- **warn.ogg** - Subtle alert for warn/danger
- **critical.ogg** - Urgent alert for critical

Sounds respect your game's master volume and sound settings.

#### Screen Flash
**Config Key**: `flash`  
**Type**: Boolean  
**Default**: `false`

Displays a brief vignette overlay when alerts trigger.

Flash intensity:
- **Warn/Danger**: 30% opacity
- **Critical**: 55% opacity

**Performance Note**: Minimal GPU impact, but disabled by default for those who prefer less visual distraction.

#### Toast Alerts (Hotbar)
**Config Key**: `toast`  
**Type**: Boolean  
**Default**: `false`

Shows advancement-style notifications above the hotbar.

These are less intrusive than chat messages and don't clutter your chat history.

#### Elytra Flight Guard
**Config Key**: `elytraGuard`  
**Type**: Boolean  
**Default**: `true`

Reserved for future functionality: provides extra warnings when attempting to fly with critically low elytra durability.

Currently tracks elytra durability with standard thresholds.

#### Snooze Duration
**Config Key**: `snoozeDurationMinutes`  
**Type**: Integer (1-60)  
**Default**: `5`

How long (in minutes) alerts are snoozed when using the snooze keybind.

Useful when you're aware of low durability and don't want repeated alerts while finishing a task.

---

### Threshold Settings

Control when alerts trigger based on remaining durability percentage.

#### Warn Threshold
**Config Key**: `warn`  
**Type**: Integer (1-99)  
**Default**: `25`

First alert level. Triggers when durability drops to or below this percentage.

**Recommended Values**:
- **Conservative**: 30-40% (more warning time)
- **Balanced**: 20-30% (default range)
- **Aggressive**: 10-20% (fewer alerts)

#### Danger Threshold
**Config Key**: `danger`  
**Type**: Integer (1-99)  
**Default**: `10`

Second alert level for more urgent warnings.

Must be ≤ warn threshold (enforced automatically).

#### Critical Threshold
**Config Key**: `critical`  
**Type**: Integer (1-99)  
**Default**: `5`

Final alert level before breakage.

Must be ≤ danger threshold (enforced automatically).

**Special Behavior**: Critical alerts repeat on cooldown even when disarmed for safety.

#### Hysteresis Percentage
**Config Key**: `hysteresisPct`  
**Type**: Integer (0-20)  
**Default**: `0`

Recovery margin before re-arming alerts.

**How it works**:
- Alert triggers at 25% (warn threshold)
- Alert disarms
- Alert re-arms when durability recovers above 25% + hysteresis
- Example: With 3% hysteresis, re-arms at 28%

**When to enable**:
- Using Mending enchantment with XP farms
- Modded durability regeneration
- Frequently swapping between damaged items

**Default 0 (disabled)**: Vanilla Minecraft doesn't regenerate durability naturally.

---

### Cooldown Settings

Prevent alert spam by setting minimum time between repeated alerts.

#### Warn Cooldown
**Config Key**: `warnCooldownSec`  
**Type**: Integer (0-300 seconds)  
**Default**: `30`

Minimum time between warn-level alerts for the same item/slot.

#### Danger Cooldown
**Config Key**: `dangerCooldownSec`  
**Type**: Integer (0-300 seconds)  
**Default**: `15`

Minimum time between danger-level alerts for the same item/slot.

Shorter than warn because danger is more urgent.

#### Critical Cooldown
**Config Key**: `criticalCooldownSec`  
**Type**: Integer (0-60 seconds)  
**Default**: `7`

Minimum time between critical-level alerts for the same item/slot.

Very short because breakage is imminent. Critical alerts bypass some disarm logic for safety.

---

### Activity-Aware Settings

Intelligent alert suppression during continuous block breaking.

#### Activity-Aware Mode
**Config Key**: `activityAware`  
**Type**: Boolean  
**Default**: `true`

Enables smart suppression during mining sessions.

**Why this matters**: When mining with a pickaxe, constant durability alerts become noise. Activity-aware mode detects continuous block breaking and extends cooldowns during mining.

#### Work Ticks Threshold
**Config Key**: `workTicksThreshold`  
**Type**: Integer (1-100 ticks)  
**Default**: `40`

How many consecutive ticks of block breaking before suppression activates.

**Examples**:
- `20` ticks = ~1 second
- `40` ticks = ~2 seconds (default)
- `60` ticks = ~3 seconds

Lower values activate suppression faster but may trigger during casual block breaking.

#### Work Cooldown
**Config Key**: `workCooldownSec`  
**Type**: Integer (0-600 seconds)  
**Default**: `30`

Extended cooldown applied to warn/danger alerts during continuous mining.

Critical alerts are not affected (always use `criticalCooldownSec`).

**Recommended Values**:
- **Light mining**: 20-40 seconds
- **Heavy mining**: 60-120 seconds
- **Extended sessions**: 120+ seconds

#### Quiet Below Warn
**Config Key**: `quietBelowWarn`  
**Type**: Boolean  
**Default**: `false`

Suppresses repeated warn-level alerts after the first crossing.

When enabled:
- First warn alert: Full alert (sound, chat, flash, toast)
- Subsequent warn alerts: Visual only (flash, toast)

Useful if you acknowledge the warning but want visual reminders without audio/chat spam.

---

## Configuration File Example

```json
{
  "enabled": true,
  
  "chat": true,
  "sound": true,
  "flash": false,
  "toast": false,
  "elytraGuard": true,
  "snoozeDurationMinutes": 5,
  
  "warn": 25,
  "danger": 10,
  "critical": 5,
  "hysteresisPct": 0,
  
  "warnCooldownSec": 30,
  "dangerCooldownSec": 15,
  "criticalCooldownSec": 7,
  
  "activityAware": true,
  "workTicksThreshold": 40,
  "workCooldownSec": 30,
  "quietBelowWarn": false,
  
  "overrides": {}
}
```

## Preset Configurations

### Minimal Alerts (Quiet Mode)
```json
{
  "chat": false,
  "sound": true,
  "flash": false,
  "toast": true,
  "warn": 25,
  "danger": 10,
  "critical": 3,
  "warnCooldownSec": 60,
  "dangerCooldownSec": 30,
  "criticalCooldownSec": 10,
  "quietBelowWarn": true
}
```

### Maximum Alerts (Paranoid Mode)
```json
{
  "chat": true,
  "sound": true,
  "flash": true,
  "toast": true,
  "warn": 40,
  "danger": 20,
  "critical": 10,
  "warnCooldownSec": 15,
  "dangerCooldownSec": 10,
  "criticalCooldownSec": 5,
  "activityAware": false
}
```

### Mending-Friendly (XP Farm)
```json
{
  "warn": 30,
  "danger": 15,
  "critical": 5,
  "hysteresisPct": 5,
  "warnCooldownSec": 20,
  "dangerCooldownSec": 15,
  "criticalCooldownSec": 10,
  "activityAware": true,
  "workCooldownSec": 60
}
```

### Mining Focus
```json
{
  "chat": false,
  "sound": false,
  "flash": false,
  "toast": true,
  "warn": 20,
  "danger": 10,
  "critical": 5,
  "activityAware": true,
  "workTicksThreshold": 20,
  "workCooldownSec": 120,
  "quietBelowWarn": true
}
```

## Best Practices

### General Recommendations

1. **Start with defaults** - They're balanced for most players
2. **Enable activity-aware mode** - Prevents mining spam
3. **Adjust thresholds based on item durability**:
   - High durability tools (1500+): Lower thresholds (15-20%)
   - Low durability tools (<100): Higher thresholds (30-40%)
4. **Use per-item overrides** for special cases (see [Advanced Configuration](Advanced-Configuration))

### Alert Type Selection

**Enable chat** if:
- You want a permanent record of alerts
- You're multitasking and might miss other alerts

**Enable sound** if:
- You play with sound on
- You want non-intrusive alerts

**Enable flash** if:
- You want high-visibility alerts
- You're in intense combat situations

**Enable toast** if:
- You prefer clean chat
- You want subtle reminders

### Cooldown Tuning

**Shorter cooldowns** (5-15 seconds):
- Frequently switch between items
- Want aggressive reminders
- Playing fast-paced content

**Longer cooldowns** (30-60+ seconds):
- Extended mining/farming sessions
- Trust yourself to remember
- Want minimal interruption

## Next Steps

- **[Advanced Configuration](Advanced-Configuration)** - Per-item threshold overrides
- **[Features & Usage](Features-and-Usage)** - Learn how alerts work in practice
- **[Keybindings](Keybindings)** - Configure hotkeys for quick control

---

Need help with configuration? Check the [FAQ](FAQ) or [open an issue](https://github.com/redlynxlabs/duraping/issues).


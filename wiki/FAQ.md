# Frequently Asked Questions

Common questions about DuraPing answered.

## General Questions

### What is DuraPing?

DuraPing is a client-side Fabric mod for Minecraft 1.21.9 that alerts you when your tools, weapons, and armor are running low on durability. It helps prevent accidentally breaking valuable items.

### Do I need to install DuraPing on my server?

**No**. DuraPing is entirely client-side. You can use it on:
- Vanilla servers
- Modded servers
- Realms
- Singleplayer

The server doesn't need DuraPing installed, and it won't interfere with server gameplay.

### Does DuraPing work in multiplayer?

Yes, DuraPing works perfectly in multiplayer. It tracks your client-side equipment durability and alerts you locally. Other players won't see your alerts.

### Can I use DuraPing in modpacks?

Yes! DuraPing is free to use in modpacks. Attribution is appreciated but not required. See the [GNU GPLv3 license](https://github.com/redlynxlabs/duraping/blob/main/LICENSE) for full terms.

### Is DuraPing compatible with Forge?

No, DuraPing is a Fabric mod only. There are no plans for a Forge port at this time.

---

## Installation & Configuration

### Where is the config file located?

`.minecraft/config/duraping.json`

For MultiMC/Prism Launcher:  
`instances/[instance name]/minecraft/config/duraping.json`

### How do I access the config menu in-game?

1. Install [Mod Menu](https://modrinth.com/mod/modmenu)
2. Press `Escape` → `Mods` → Find "DuraPing" → Click "Config"

Without Mod Menu, you must edit the JSON file manually.

### What happens if I edit the config while Minecraft is running?

Changes won't take effect until you:
- Restart Minecraft, **or**
- Press `F3 + T` to reload resources

We recommend editing while Minecraft is closed.

### Can I have different configs for different worlds?

Not natively. DuraPing uses a single config file for all worlds. You would need to manually swap config files between sessions.

---

## Alert Behavior

### Why am I not getting alerts?

Check these common issues:
1. **Master toggle disabled** - Press Numpad 7 or check config: `"enabled": true`
2. **Snoozed** - Press Numpad 8 to cancel snooze
3. **No alert types enabled** - Enable at least one: chat, sound, flash, or toast
4. **Thresholds too low** - Verify thresholds: Default 25/10/5
5. **On cooldown** - Wait for cooldown period to elapse

See [Troubleshooting](Troubleshooting#not-receiving-alerts) for detailed solutions.

### Why am I getting constant alerts?

This usually happens during mining. Solutions:
1. **Enable activity-aware mode**: `"activityAware": true`
2. **Increase cooldowns**: `"warnCooldownSec": 60`
3. **Enable quiet mode**: `"quietBelowWarn": true`

See [Configuration](Configuration#cooldown-settings) for more options.

### Do alerts trigger at exact durability values or percentages?

**Percentages**. DuraPing calculates:  
`(remaining_uses / max_durability) * 100`

This means:
- Diamond Pickaxe (1561 max): Alerts at different remaining uses than...
- Iron Pickaxe (250 max): ...but same percentage thresholds

### What's the difference between warn, danger, and critical?

| Level | Default % | Purpose | Alert Style |
|-------|-----------|---------|-------------|
| **Warn** | 25% | First notice, time to prepare backup | Yellow/Gold |
| **Danger** | 10% | Urgent, switch items soon | Orange/Red |
| **Critical** | 5% | Immediate action required | Dark Red + Bold |

Critical alerts repeat more frequently and can't be fully disarmed.

### Can I disable alerts for specific items?

Not directly, but you can set very low thresholds:
```json
"overrides": {
  "minecraft:wooden_pickaxe": {
    "warn": 1,
    "danger": 1,
    "critical": 1
  }
}
```

See [Advanced Configuration](Advanced-Configuration#disabling-specific-items) for details.

### Why do I get alerts even though my item looks fine?

The durability bar is not perfectly granular. An item might visually look 30% full but actually be at 24%, triggering the 25% warn threshold.

Press Numpad 9 (Show Hand Durability) to see exact percentage.

---

## Thresholds & Overrides

### What are good threshold values?

**Default (25/10/5)** works for most players.

**Adjust based on item durability**:
- **High durability** (1500+ uses): Lower thresholds (20/10/5)
- **Low durability** (<100 uses): Higher thresholds (40/20/10)
- **Critical items** (elytra, netherite): Earlier warnings (30/15/8)

See [Advanced Configuration](Advanced-Configuration) for examples.

### How do I set different thresholds for different items?

Use per-item overrides in `config/duraping.json`:
```json
"overrides": {
  "minecraft:elytra": {
    "warn": 20,
    "danger": 10,
    "critical": 5
  }
}
```

See [Advanced Configuration](Advanced-Configuration#per-item-threshold-overrides) for full guide.

### Do overrides work with modded items?

Yes! Use the modded item's ID:
```json
"overrides": {
  "modname:custom_pickaxe": {
    "warn": 30,
    "danger": 15,
    "critical": 7
  }
}
```

Find the ID with F3 + H (advanced tooltips) and hover over the item.

### What is hysteresis and when should I use it?

**Hysteresis** prevents alert bouncing when durability fluctuates near thresholds.

**Formula**: Alert re-arms when durability recovers above `threshold + hysteresis`

**Enable if**:
- Using Mending with XP farms
- Using mods with durability regeneration
- Frequently swapping damaged items

**Default 0 (disabled)**: Vanilla Minecraft doesn't regenerate durability naturally.

See [Configuration](Configuration#hysteresis-percentage) for details.

---

## Keybindings

### What are the default keybindings?

| Key | Action |
|-----|--------|
| **Numpad 7** | Toggle DuraPing on/off |
| **Numpad 8** | Snooze/Cancel alerts |
| **Numpad 9** | Show hand durability |

### How do I change keybindings?

Options → Controls → Scroll to "DuraPing" category → Click keybind → Press new key → Done

### My laptop doesn't have a Numpad. What should I use?

Recommended alternatives:
- **F6, F7, F8** (function keys)
- **J, K, L** (right-hand keys)
- **[, ], \** (bracket keys)

See [Keybindings](Keybindings#customizing-keybindings) for more options.

### Do keybindings work when DuraPing is disabled?

**Yes**. All three keybindings work regardless of DuraPing's enabled/disabled state. This ensures you can always toggle it back on.

### How long does snooze last?

Default: 5 minutes

Change in config:
```json
"snoozeDurationMinutes": 10
```

Range: 1-60 minutes.

---

## Features

### Does DuraPing track armor?

Yes. DuraPing monitors:
- Main hand
- Off hand
- Helmet
- Chestplate
- Leggings
- Boots

Each slot is tracked independently.

### Does DuraPing consider Unbreaking enchantments?

**No**. DuraPing reads the durability bar, which doesn't reflect Unbreaking's probabilistic protection. 

Unbreaking III makes items last ~4x longer on average, but the durability bar still shows max durability as the base value.

**Recommendation**: Lower thresholds for Unbreaking items since they'll last longer than the bar suggests.

### Does DuraPing work with Mending?

Yes. DuraPing monitors durability regardless of how it changes.

**With Mending**, consider enabling hysteresis:
```json
"hysteresisPct": 3
```

This prevents re-alerting when durability fluctuates during XP collection.

### Can DuraPing show durability on the HUD?

Not currently. DuraPing focuses on alerts. Press Numpad 9 for on-demand durability display.

Future versions may include an optional HUD overlay.

### Does DuraPing prevent items from breaking?

**No**. DuraPing only alerts you. It's still possible to break items if you ignore the alerts.

The "Elytra Flight Guard" feature (planned) may add preventive measures in the future.

---

## Performance & Compatibility

### Does DuraPing impact performance?

Minimal impact:
- **CPU**: Simple calculations 20 times per second
- **GPU**: Flash overlay is lightweight
- **Memory**: <1 KB per tracked item
- **Network**: Zero network traffic

See [Features & Usage](Features-and-Usage#performance) for details.

### Is DuraPing compatible with Sodium/Iris/OptiFine?

**Yes**:
- Sodium ✓
- Lithium ✓
- Phosphor ✓
- Iris Shaders ✓

**No**:
- OptiFine (Fabric incompatible, use alternatives)

### Is DuraPing compatible with inventory mods?

Yes. DuraPing tracks durability independently of inventory rendering. Compatible with:
- Inventory Tweaks
- REI (Roughly Enough Items)
- JEI (Just Enough Items)
- Crafting Tweaks

### Can I use DuraPing with other durability mods?

Generally yes, but behavior may overlap. Test compatibility and adjust settings as needed.

If you encounter conflicts, [open an issue](https://github.com/redlynxlabs/duraping/issues).

---

## Troubleshooting

### Alerts stopped working after update

Try these steps:
1. Delete `config/duraping.json`
2. Restart Minecraft (config regenerates)
3. Reconfigure your settings

See [Troubleshooting](Troubleshooting) for detailed solutions.

### I'm getting error messages in logs

Common errors and fixes:
- `java.lang.UnsupportedClassVersionError` → Update to Java 21+
- `Mod resolution encountered an incompatible mod set` → Update Fabric API
- `Could not find required mod: cloth-config2` → Install Mod Menu

See [Troubleshooting](Troubleshooting#installation-issues) for more.

### Config changes aren't saving

Check:
1. File permissions on `.minecraft/config/` folder
2. JSON syntax (use [jsonlint.com](https://jsonlint.com/))
3. File isn't read-only

See [Troubleshooting](Troubleshooting#config-file-not-saving) for detailed steps.

---

## Development & Contributing

### Is DuraPing open source?

Yes! DuraPing is licensed under GNU General Public License v3.0.

Source code: [github.com/redlynxlabs/duraping](https://github.com/redlynxlabs/duraping)

### Can I contribute to DuraPing?

Absolutely! Contributions are welcome:
1. Fork the repository
2. Create a feature branch from `dev`
3. Make your changes
4. Open a pull request to `dev` (not `main`)

See [Development](Development) for build instructions and contribution guidelines.

### How do I report bugs or request features?

Use GitHub issue templates:
- **Bug Report**: [Create bug report](https://github.com/redlynxlabs/duraping/issues/new?template=bug_report.yml)
- **Feature Request**: [Request feature](https://github.com/redlynxlabs/duraping/issues/new?template=feature_request.yml)
- **Question**: [Ask question](https://github.com/redlynxlabs/duraping/issues/new?template=question.yml)

### Where can I find changelogs?

Changelogs are included with each [release](https://github.com/redlynxlabs/duraping/releases).

### Will DuraPing support older Minecraft versions?

No plans for backports to older versions. Development focuses on the latest Minecraft release.

### Will there be a Forge version?

No plans at this time. DuraPing is designed specifically for Fabric's architecture.

---

## Miscellaneous

### What does "DuraPing" mean?

**Dura**bility + **Ping** (alert/notification)

### Who made DuraPing?

DuraPing is developed by [RedLynx Labs](https://redlynx.io) and authored by [redkey](https://github.com/redkeysh).

### How can I support DuraPing development?

- Star the [GitHub repository](https://github.com/redlynxlabs/duraping)
- Report bugs and suggest features
- Contribute code or documentation
- Share with others who might find it useful

### Can I use DuraPing in YouTube videos/streams?

Yes! DuraPing can be used in content creation without restriction. Attribution is appreciated but not required.

### Does DuraPing collect any data?

**No**. DuraPing:
- Does not collect telemetry
- Does not send network requests
- Does not track usage
- Is 100% local and private

---

## Still Have Questions?

- Check the [full documentation](Home)
- Search [existing issues](https://github.com/redlynxlabs/duraping/issues)
- [Open a question issue](https://github.com/redlynxlabs/duraping/issues/new?template=question.yml)
- Visit [RedLynx Labs](https://redlynx.io)

---

**Last Updated**: December 2024


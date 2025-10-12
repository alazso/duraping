# Advanced Configuration

This guide covers advanced customization options, including per-item threshold overrides and edge cases.

## Per-Item Threshold Overrides

DuraPing allows you to set custom thresholds for specific items using the `overrides` configuration.

### Why Use Overrides?

Different items have different durability ranges and importance levels:

**High Durability Items** (1500+ uses):
- Netherite tools and armor
- Diamond tools and armor
- Can use lower thresholds (more uses remaining)

**Low Durability Items** (<100 uses):
- Shears (238 uses)
- Flint and Steel (64 uses)
- Fishing Rod (64 uses)
- Need higher thresholds (percentage matters more)

**Critical Items**:
- Elytra (432 uses, hard to replace)
- Netherite armor (high investment)
- Enchanted gear
- Want earlier warnings

### Configuration Format

Overrides are added to `config/duraping.json` under the `overrides` key:

```json
{
  "warn": 25,
  "danger": 10,
  "critical": 5,
  "overrides": {
    "minecraft:item_id": {
      "warn": 30,
      "danger": 15,
      "critical": 7
    }
  }
}
```

### Finding Item IDs

**Method 1: F3 + H (Advanced Tooltips)**
1. Press `F3 + H` in-game
2. Hover over the item in your inventory
3. Look for the ID at the bottom of the tooltip
4. Format: `minecraft:diamond_pickaxe`

**Method 2: Minecraft Wiki**
- Visit the [Minecraft Wiki](https://minecraft.wiki/)
- Search for the item
- ID is listed in the infobox

**Method 3: `/give` Command**
- Most items can be spawned with `/give @s minecraft:item_name`
- The `item_name` is the ID

### Common Item IDs

**Tools**:
```
minecraft:diamond_pickaxe
minecraft:diamond_axe
minecraft:diamond_shovel
minecraft:diamond_sword
minecraft:diamond_hoe
```

**Netherite Tools** (replace `diamond` with `netherite`):
```
minecraft:netherite_pickaxe
minecraft:netherite_sword
```

**Armor**:
```
minecraft:diamond_helmet
minecraft:diamond_chestplate
minecraft:diamond_leggings
minecraft:diamond_boots
```

**Special Items**:
```
minecraft:elytra
minecraft:shield
minecraft:bow
minecraft:crossbow
minecraft:trident
minecraft:fishing_rod
minecraft:shears
minecraft:flint_and_steel
```

---

## Example Override Configurations

### Elytra Protection

Elytra are irreplaceable in many playstyles. Get early warnings:

```json
"overrides": {
  "minecraft:elytra": {
    "warn": 20,
    "danger": 10,
    "critical": 5
  }
}
```

**Reasoning**:
- 432 max durability
- 20% = 86 uses left
- Plenty of time to find repair station
- Critical at 5% = 21 uses (multiple flights remaining)

### Low-Durability Items

Items like shears and fishing rods have very low durability. Higher percentages give better warning:

```json
"overrides": {
  "minecraft:shears": {
    "warn": 40,
    "danger": 20,
    "critical": 10
  },
  "minecraft:fishing_rod": {
    "warn": 40,
    "danger": 20,
    "critical": 10
  },
  "minecraft:flint_and_steel": {
    "warn": 50,
    "danger": 25,
    "critical": 10
  }
}
```

**Reasoning**:
- Shears: 238 max, 40% = 95 uses
- Fishing Rod: 64 max, 40% = 25 uses
- Flint and Steel: 64 max, 50% = 32 uses
- Low durability means percentages drop fast

### Netherite Gear (Conservative)

Netherite is expensive. Get very early warnings:

```json
"overrides": {
  "minecraft:netherite_pickaxe": {
    "warn": 35,
    "danger": 20,
    "critical": 10
  },
  "minecraft:netherite_sword": {
    "warn": 35,
    "danger": 20,
    "critical": 10
  },
  "minecraft:netherite_chestplate": {
    "warn": 30,
    "danger": 15,
    "critical": 8
  }
}
```

**Reasoning**:
- Netherite pickaxe: 2031 max, 35% = 710 uses
- Plenty of warning time
- High investment requires early notification

### Diamond Gear (Aggressive)

Diamond tools are relatively common. Later warnings reduce alert fatigue:

```json
"overrides": {
  "minecraft:diamond_pickaxe": {
    "warn": 20,
    "danger": 10,
    "critical": 5
  },
  "minecraft:diamond_sword": {
    "warn": 15,
    "danger": 8,
    "critical": 3
  }
}
```

**Reasoning**:
- Easier to replace than netherite
- 20% still provides ample warning
- Reduces alert frequency

### Combat Equipment

Shields and weapons need immediate warnings in combat:

```json
"overrides": {
  "minecraft:shield": {
    "warn": 30,
    "danger": 15,
    "critical": 8
  },
  "minecraft:bow": {
    "warn": 25,
    "danger": 12,
    "critical": 5
  },
  "minecraft:crossbow": {
    "warn": 25,
    "danger": 12,
    "critical": 5
  }
}
```

**Reasoning**:
- Breaking mid-combat is dangerous
- Early warnings allow weapon switching
- Critical threshold higher for safety margin

---

## Complete Override Examples

### Hardcore Mode Configuration

Every item matters. Early warnings across the board:

```json
{
  "warn": 35,
  "danger": 20,
  "critical": 10,
  "overrides": {
    "minecraft:elytra": {
      "warn": 25,
      "danger": 15,
      "critical": 8
    },
    "minecraft:netherite_pickaxe": {
      "warn": 40,
      "danger": 25,
      "critical": 12
    },
    "minecraft:netherite_sword": {
      "warn": 40,
      "danger": 25,
      "critical": 12
    },
    "minecraft:shield": {
      "warn": 35,
      "danger": 20,
      "critical": 10
    }
  }
}
```

### Speedrun Configuration

Minimize alerts, only critical equipment:

```json
{
  "warn": 15,
  "danger": 8,
  "critical": 3,
  "overrides": {
    "minecraft:diamond_pickaxe": {
      "warn": 10,
      "danger": 5,
      "critical": 2
    },
    "minecraft:iron_pickaxe": {
      "warn": 15,
      "danger": 8,
      "critical": 3
    }
  }
}
```

### Balanced Survival Configuration

Reasonable defaults with elytra protection:

```json
{
  "warn": 25,
  "danger": 10,
  "critical": 5,
  "overrides": {
    "minecraft:elytra": {
      "warn": 20,
      "danger": 10,
      "critical": 5
    },
    "minecraft:netherite_pickaxe": {
      "warn": 30,
      "danger": 15,
      "critical": 8
    },
    "minecraft:shears": {
      "warn": 40,
      "danger": 20,
      "critical": 10
    },
    "minecraft:fishing_rod": {
      "warn": 40,
      "danger": 20,
      "critical": 10
    }
  }
}
```

---

## Advanced Use Cases

### Temporary Overrides

You can edit `config/duraping.json` without restarting Minecraft:

1. Edit the config file
2. Press `F3 + T` to reload resources (or restart)
3. New overrides take effect immediately

**Useful for**: Testing different thresholds without restarting.

### Modded Item Support

DuraPing works with modded items automatically. Add overrides using the mod's item ID:

```json
"overrides": {
  "modname:custom_pickaxe": {
    "warn": 30,
    "danger": 15,
    "critical": 7
  }
}
```

**Finding modded item IDs**: Use F3 + H and hover over the item.

### Disabling Specific Items

To effectively disable alerts for a specific item, set very low thresholds:

```json
"overrides": {
  "minecraft:wooden_pickaxe": {
    "warn": 1,
    "danger": 1,
    "critical": 1
  }
}
```

**Why not 0?**: Thresholds are clamped to minimum 1%.

**Use case**: You don't care about disposable early-game tools.

### Unbreaking Enchantments

Unbreaking enchantments make items last longer but don't change durability values. Thresholds still apply:

**Example**: Diamond Pickaxe with Unbreaking III
- Max durability: Still 1561
- Effective durability: ~6244 uses (4x multiplier)
- Thresholds: Based on 1561, not 6244

**Recommendation**: Lower thresholds for Unbreaking items:

```json
"overrides": {
  "minecraft:diamond_pickaxe": {
    "warn": 20,
    "danger": 10,
    "critical": 5
  }
}
```

You'll get alerts when the visible durability bar shows 20%, even though the item has many uses remaining.

---

## Override Priority

If multiple configurations could apply, priority order:

1. **Item-specific override** (most specific)
2. **Global threshold** (fallback)

**Example**:
```json
{
  "warn": 25,          // Global default
  "overrides": {
    "minecraft:elytra": {
      "warn": 20      // Used for elytra
    }
  }
}
```

Elytra uses 20%, all other items use 25%.

---

## Configuration Validation

DuraPing automatically validates and normalizes your config:

**Rules**:
- `warn >= danger >= critical`
- All thresholds >= 1
- All thresholds <= 99
- Invalid values are clamped to valid range

**Example**:
```json
{
  "warn": 10,
  "danger": 20,  // Invalid: Higher than warn
  "critical": 5
}
```

**Normalized to**:
```json
{
  "warn": 10,
  "danger": 10,  // Clamped to warn value
  "critical": 5
}
```

---

## Troubleshooting Overrides

### Override Not Working

**Check Item ID Format**:
- Must include namespace: `minecraft:diamond_pickaxe`
- NOT just: `diamond_pickaxe`

**Check JSON Syntax**:
- Use a JSON validator: [jsonlint.com](https://jsonlint.com/)
- Common errors: Missing commas, trailing commas, mismatched quotes

**Restart Minecraft**:
- Config is loaded on startup
- Or press `F3 + T` to reload

### Testing Overrides

1. Edit config file
2. Add override for test item
3. Use creative mode to get item at specific durability
4. `/give @s minecraft:diamond_pickaxe{Damage:1400}`
5. Verify alert fires at correct threshold

---

## Next Steps

- **[Configuration](Configuration)** - Learn about global settings
- **[Features & Usage](Features-and-Usage)** - Understand alert behavior
- **[FAQ](FAQ)** - Common questions about overrides

---

Need help with overrides? [Open an issue](https://github.com/redlynxlabs/duraping/issues) with your config file.


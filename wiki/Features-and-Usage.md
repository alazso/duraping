# Features and Usage

This guide explains all of DuraPing's features and how to use them effectively.

## Core Alert System

### How Durability Monitoring Works

DuraPing continuously monitors the durability of items in:
- **Main hand** (right hand by default)
- **Off hand** (left hand)
- **Armor slots** (helmet, chestplate, leggings, boots)

Checks occur every game tick (20 times per second) when you have damageable items equipped.

### Alert Triggering

Alerts trigger based on **percentage of remaining durability**, not absolute values.

**Formula**: `(Max Durability - Current Damage) / Max Durability * 100`

**Example**: Diamond Pickaxe (1561 max durability)
- 1561 uses left = 100%
- 390 uses left = 25% (warn threshold)
- 156 uses left = 10% (danger threshold)
- 78 uses left = 5% (critical threshold)
- 1 use left = Emergency alert

### Alert Buckets

DuraPing uses a tiered "bucket" system:

**Bucket 0: Healthy** (Above warn threshold)
- No alerts
- Full durability or minor wear

**Bucket 1: Warn** (≤ Warn threshold)
- First notification
- Time to prepare a backup
- Default: 25% remaining

**Bucket 2: Danger** (≤ Danger threshold)
- More urgent notification
- Consider switching items soon
- Default: 10% remaining

**Bucket 3: Critical** (≤ Critical threshold or 1 durability)
- Immediate action required
- Item will break in 1-2 hits
- Default: 5% remaining or 1 use left

### Crossing Detection

Alerts trigger when you **cross down** into a lower bucket:

**Example progression**:
1. Healthy (50%) → Warn (23%) = **Alert fires**
2. Warn (23%) → Warn (20%) = No alert (same bucket)
3. Warn (20%) → Danger (9%) = **Alert fires**
4. Danger (9%) → Critical (4%) = **Alert fires**
5. Critical (4%) → Break (0%) = Item destroyed

**Key Point**: You get one alert per threshold crossing, preventing spam while ensuring you're notified.

### Re-Arming Logic

After an alert fires, it becomes "disarmed" until conditions reset.

**Re-arm conditions**:
1. **Return to healthy**: Durability recovers above threshold + hysteresis
2. **Cross to next bucket**: Move from warn → danger → critical
3. **Cooldown elapsed**: Time-based re-arming for same bucket (configurable)

**Critical bucket exception**: Critical alerts repeat on cooldown regardless of disarm state for safety.

---

## Alert Types

### Chat Notifications

**Location**: Game chat window  
**Config Key**: `chat`  
**Default**: Enabled

**Format**:
```
[Item Name] is getting low ([X] uses left)
[Item Name] is in DANGER ([X] uses left)
[Item Name] is CRITICAL ([X] uses left)
```

**Color Coding**:
- **Yellow/Gold** (`0xFFAA00`) - Warn
- **Orange/Red** (`0xFF5555`) - Danger
- **Dark Red + Bold** (`0xAA0000`) - Critical

**Advantages**:
- Permanent record (can scroll back)
- Visible even during inventory management
- Includes exact remaining uses

**Disadvantages**:
- Can clutter chat during multiplayer
- Easy to miss during combat

**Best for**: Players who want detailed information and chat history.

### Sound Effects

**Config Key**: `sound`  
**Default**: Enabled

DuraPing includes two custom audio cues:

**warn.ogg** - Used for:
- Warn threshold crossing
- Danger threshold crossing

**critical.ogg** - Used for:
- Critical threshold crossing
- 1-durability emergency

**Volume**: Respects game master volume and sound category settings.

**Advantages**:
- Non-intrusive
- Works even when not looking at screen
- Instant recognition after familiarity

**Disadvantages**:
- Requires sound enabled
- May be masked by other game sounds

**Best for**: Players who play with sound and want ambient awareness.

### Screen Flash

**Config Key**: `flash`  
**Default**: Disabled

A brief vignette overlay that flashes when alerts trigger.

**Intensity**:
- Warn/Danger: 30% opacity, 0.25 second duration
- Critical: 55% opacity, 0.3 second duration

**Appearance**: Red-tinted vignette from screen edges

**Advantages**:
- High visibility
- Impossible to miss during gameplay
- No chat clutter

**Disadvantages**:
- Can be startling
- May obscure combat visuals briefly
- Some players find it distracting

**Best for**: PvP players, hardcore mode, or anyone who needs guaranteed visibility.

### Toast Notifications (Hotbar)

**Config Key**: `toast`  
**Default**: Disabled

Advancement-style notifications that appear above the hotbar.

**Format**: Brief message with item name

**Duration**: Approximately 3 seconds

**Advantages**:
- Clean and unobtrusive
- Doesn't clutter chat
- Positioned in natural eye-line

**Disadvantages**:
- Temporary (can't review later)
- Small text size
- May be missed during inventory management

**Best for**: Players who want minimal distraction and clean chat.

---

## Smart Features

### Activity-Aware Suppression

**Config Key**: `activityAware`  
**Default**: Enabled

Detects when you're continuously breaking blocks and extends cooldowns to prevent spam.

**How it works**:
1. Tracks consecutive ticks of block breaking
2. After threshold reached (default 40 ticks / 2 seconds), enters "work mode"
3. Extends warn/danger cooldowns (default 30 seconds)
4. Critical alerts unaffected (always important)
5. Exits work mode when you stop breaking blocks

**Example Scenario**:
```
Mining session starts
Break 1 block → tick counter: 1
Break 2 blocks → tick counter: 15
...continuous mining...
Break 20 blocks → tick counter: 40
→ Work mode activated
→ Cooldowns extended to 30 seconds

Stop mining for 1 tick
→ Work mode deactivated
→ Cooldowns return to normal
```

**Use Cases**:
- Mining operations
- Excavation projects
- Tree farming
- Any repetitive block breaking

**Why it matters**: Without this, you'd get constant alerts while strip mining with a pickaxe, which becomes noise rather than useful information.

### Hysteresis Support

**Config Key**: `hysteresisPct`  
**Default**: 0 (disabled)

Prevents alert bouncing when durability fluctuates near thresholds.

**How it works**:
```
Warn threshold: 25%
Hysteresis: 3%

Durability drops to 24% → Alert fires → Disarmed
Durability recovers to 26% → Still disarmed (below 25% + 3%)
Durability recovers to 29% → Re-armed
Durability drops to 24% again → Alert fires
```

**When to enable**:

**Mending + XP**: 
- Killing mobs near XP farm
- Durability fluctuates as you fight and gain XP
- Prevents alert spam during repair cycles

**Modded Durability Regen**:
- Some mods provide passive durability regeneration
- Hysteresis prevents re-alerting during slow regen

**Rapid Item Swapping**:
- Switching between damaged items of same type
- Each swap might trigger alerts without hysteresis

**Default 0**: Vanilla Minecraft has no durability recovery, so hysteresis is unnecessary for most players.

### Per-Slot Tracking

DuraPing tracks each equipment slot independently.

**Tracked Slots**:
- Main Hand
- Off Hand
- Head (helmet)
- Chest (chestplate)
- Legs (leggings)
- Feet (boots)

**Why it matters**: 
- Wearing diamond chestplate at 20% and diamond leggings at 15% tracks separately
- Alerts fire independently for each item
- Prevents confusion about which item needs attention

**Implementation Detail**: Uses `item_id + slot_name` as unique key to distinguish:
- Diamond Pickaxe in main hand
- vs. Diamond Pickaxe in off hand
- vs. Different diamond pickaxe

### Emergency 1-Durability Alert

**Always enabled** - Cannot be disabled

When any item reaches exactly 1 durability remaining:
- **Immediate critical alert** regardless of cooldown
- **Bypasses all suppression** (even snooze)
- **Maximum intensity** (all enabled alert types fire)

**Why this is special**: One more use will **destroy the item permanently**. This is your last chance to act.

---

## Keybind Features

See [Keybindings](Keybindings) for detailed control information.

### Toggle DuraPing

**Default**: Numpad 7

Enables or disables all durability monitoring.

**Visual Feedback**:
- Chat message: "DuraPing: ENABLED" (green, bold)
- Chat message: "DuraPing: DISABLED" (red, bold)
- Hotbar overlay with same message

**Use Cases**:
- Temporary disable during creative mode
- Disable during screenshot/video recording
- Quick mute without entering menu

**Note**: Keybindings work even when DuraPing is disabled, so you can always toggle back on.

### Snooze Alerts

**Default**: Numpad 8

Toggles snooze mode for configured duration (default 5 minutes).

**Behavior**:
- First press: Snoozes for X minutes
- Second press: Cancels snooze early
- Visual feedback shows remaining time or cancellation

**Visual Feedback**:
- "DuraPing: Snoozed for X minutes" (gold, bold)
- "DuraPing: Snooze CANCELLED" (green, bold)

**Use Cases**:
- Aware of low durability, finishing current task
- Extended mining session with backup tools ready
- Boss fight where alerts would be distracting

### Show Hand Durability

**Default**: Numpad 9

Displays on-demand durability info for main hand item.

**Output** (hotbar toast):
```
[Item Name]: [Uses Left] ([Percentage]%)
```

**Example**:
```
Diamond Pickaxe: 1250 (80%)
```

**If no damageable item**:
```
No durable item in hand
```

**Use Cases**:
- Quick check without hovering
- Verify exact remaining uses
- Check durability during combat without inventory

---

## Elytra Flight Guard

**Config Key**: `elytraGuard`  
**Default**: Enabled

Special handling for elytra durability.

**Current Behavior**:
- Tracks elytra like any armor piece
- Critical alerts when low
- Reserved for future flight detection

**Future Plans** (not yet implemented):
- Detect elytra flight activation
- Extra warning if attempting to fly with critical durability
- Prevent accidental mid-flight breakage

**Recommended Settings for Elytra**:
```json
"overrides": {
  "minecraft:elytra": {
    "warn": 15,
    "danger": 7,
    "critical": 3
  }
}
```

See [Advanced Configuration](Advanced-Configuration) for per-item overrides.

---

## Advanced Usage Patterns

### Hardcore Mode

Recommended configuration for hardcore:
- Enable all alert types (chat, sound, flash, toast)
- Higher thresholds (warn: 40%, danger: 20%, critical: 10%)
- Shorter cooldowns (10-15 seconds)
- Disable activity-aware (no suppression)

### Speedrunning

Recommended configuration:
- Sound only (no visual distraction)
- Critical threshold only (5%)
- Aggressive cooldowns
- Activity-aware enabled

### Casual Survival

Default configuration works well:
- Chat + sound
- Standard thresholds (25/10/5)
- Activity-aware enabled
- Moderate cooldowns

### Multiplayer PvP

Recommended configuration:
- Sound + flash (highly visible)
- Disable chat (prevents info leaking)
- Higher thresholds (30/15/8)
- Short cooldowns

---

## Performance

DuraPing is designed to have minimal performance impact:

**CPU Usage**:
- Tick-based checking (only when items equipped)
- Simple percentage calculations
- No complex pathfinding or AI

**Memory Usage**:
- Lightweight state tracking (<1 KB per item)
- No texture loading (uses vanilla resources)

**Network**:
- Zero network traffic (client-only)
- No packets sent to server
- No server-side requirements

**Compatibility**:
- Works with Sodium, Lithium, Phosphor
- No known conflicts with performance mods

---

## Next Steps

- **[Configuration](Configuration)** - Customize alert behavior
- **[Advanced Configuration](Advanced-Configuration)** - Per-item threshold overrides
- **[Keybindings](Keybindings)** - Configure hotkeys
- **[Troubleshooting](Troubleshooting)** - Solve common issues

---

Have questions about features? Check the [FAQ](FAQ) or [open an issue](https://github.com/redlynxlabs/duraping/issues).


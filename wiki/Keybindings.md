# Keybindings

DuraPing provides three customizable keybindings for quick control without opening menus.

## Default Keybindings

| Key | Action | Description |
|-----|--------|-------------|
| **Numpad 7** | Toggle DuraPing | Enable/disable all durability monitoring |
| **Numpad 8** | Snooze/Cancel Alerts | Temporarily mute alerts for configured duration |
| **Numpad 9** | Show Hand Durability | Display durability info for main hand item |

## Customizing Keybindings

### In-Game Configuration

1. Press `Escape` to open the game menu
2. Click **"Options"**
3. Click **"Controls"**
4. Scroll to find the **"DuraPing"** category
5. Click on the keybind you want to change
6. Press the new key
7. Click **"Done"** to save

**Note**: The keybindings category appears alphabetically, usually between "Gameplay" and "Inventory".

### Conflicting Keybindings

If you assign a key that's already in use:
- Minecraft will highlight the conflict in red
- Both actions will trigger simultaneously
- Reassign one of the conflicting keys

**Common Conflicts with Numpad**:
- Some mods use Numpad keys for their own functions
- Consider rebinding to unused keys like:
  - `F6`, `F7`, `F8` (if not used by other mods)
  - `K`, `L`, `;` (right-hand keys)
  - `[`, `]`, `\` (bracket keys)

### Recommended Alternative Bindings

**For laptops without Numpad**:
```
Toggle:  F6
Snooze:  F7
Show:    F8
```

**For left-hand placement**:
```
Toggle:  Z
Snooze:  X
Show:    C
```

**For minimal conflicts**:
```
Toggle:  J
Snooze:  K
Show:    L
```

## Keybinding Details

### Toggle DuraPing (Default: Numpad 7)

**Function**: Enables or disables all durability monitoring and alerts.

**When pressed**:
1. Flips the `enabled` config value
2. Saves configuration to disk
3. Displays feedback message

**Visual Feedback**:
- **Chat**: "DuraPing: ENABLED" or "DuraPing: DISABLED"
- **Hotbar**: Same message displayed above hotbar
- **Color**: Green (bold) when enabled, Red (bold) when disabled

**Behavior When Disabled**:
- No durability checks performed
- No alerts fire
- Keybindings still work (so you can re-enable)
- Mod Menu config still accessible

**Use Cases**:
```
Scenario: Creative mode building
Action: Press Numpad 7 to disable
Result: No alerts while in creative

Scenario: Recording gameplay
Action: Press Numpad 7 to disable temporarily
Result: Clean recording without alerts

Scenario: Testing without alerts
Action: Press Numpad 7 to toggle off
Result: Can still use Show Hand Durability feature
```

**Important**: This is a master toggle, not an override. Individual alert types still respect their own enabled/disabled state.

---

### Snooze/Cancel Alerts (Default: Numpad 8)

**Function**: Toggles snooze mode for configured duration (default 5 minutes).

**When pressed (not snoozed)**:
1. Activates snooze mode
2. Records snooze end time
3. Displays feedback with duration

**When pressed (already snoozed)**:
1. Cancels snooze mode
2. Clears snooze end time
3. Displays cancellation feedback

**Visual Feedback**:
- **Snooze activated**: "DuraPing: Snoozed for X minutes" (gold, bold)
- **Snooze cancelled**: "DuraPing: Snooze CANCELLED" (green, bold)
- Both messages appear in chat and hotbar

**During Snooze**:
- All durability checks still occur
- No alerts fire (chat, sound, flash, toast)
- State tracking continues (for smooth resume)
- Emergency 1-durability alerts **still fire** (cannot be snoozed)

**Configuration**:
Change snooze duration in config:
```json
"snoozeDurationMinutes": 10
```

**Use Cases**:
```
Scenario: Mining with backup pickaxes ready
Action: Press Numpad 8 to snooze for 5 minutes
Result: Continue mining without constant alerts

Scenario: Boss fight with low-durability armor
Action: Press Numpad 8 to snooze
Result: Focus on fight, will repair after

Scenario: Snoozed but situation changed
Action: Press Numpad 8 again to cancel early
Result: Alerts resume immediately
```

**Best Practices**:
- Use when **aware** of low durability
- Have backup items ready
- Cancel snooze when switching activities
- Remember: 1-durability alerts bypass snooze

---

### Show Hand Durability (Default: Numpad 9)

**Function**: Displays on-demand durability information for the item in your main hand.

**When pressed (with damageable item)**:
1. Calculates remaining uses
2. Calculates percentage
3. Displays formatted message

**Output Format**:
```
[Item Name]: [Uses Left] ([Percentage]%)
```

**Examples**:
```
Diamond Pickaxe: 1250 (80%)
Iron Sword: 78 (31%)
Elytra: 5 (1%)
Netherite Chestplate: 2968 (50%)
```

**When pressed (without damageable item)**:
```
No durable item in hand
```

**Display Location**: Hotbar overlay (toast notification area)

**Use Cases**:
```
Scenario: Quick durability check
Action: Press Numpad 9
Result: See exact remaining uses without hovering

Scenario: Deciding whether to repair
Action: Press Numpad 9
Result: Get percentage to inform decision

Scenario: Combat without breaking flow
Action: Press Numpad 9 during fight
Result: Check sword durability without opening inventory

Scenario: Precision monitoring
Action: Press Numpad 9 repeatedly
Result: Watch durability decrease in real-time
```

**Best Practices**:
- Use during inventory management
- Check before committing to large projects
- Verify after repairing/mending
- Quick confirmation that item is safe to use

**Technical Details**:
- Only checks main hand (right hand by default)
- Does not check off-hand or armor
- Works even when DuraPing is disabled
- Works even when snoozed
- No cooldown (can spam if desired)

---

## Keybinding Behavior

### Always Active

**All three keybindings work regardless of**:
- DuraPing enabled/disabled state
- Snooze active/inactive
- Current alert state
- Configuration values

**Why**: Allows you to always control the mod, even when it's temporarily disabled.

### No Cooldown

Keybindings have no cooldown and can be pressed repeatedly.

**Example**:
```
Press Numpad 7: Disable
Press Numpad 7: Enable
Press Numpad 7: Disable
Press Numpad 7: Enable
```

All presses register immediately.

### Feedback Every Press

Every keybind press provides visual feedback:
- Chat message
- Hotbar overlay
- Both use colored, bold text for visibility

### Persistence

**Toggle state**: Saved to config file immediately
**Snooze state**: Not saved (resets on game restart)
**Show durability**: Instant, no state to save

---

## Troubleshooting Keybindings

### Keybind Not Working

**Check Conflicts**:
1. Open Controls menu
2. Look for red highlighted keys
3. Reassign conflicting keys

**Verify Keybind Assignment**:
1. Controls → DuraPing category
2. Confirm keys are correctly assigned
3. Try pressing in-game

**Test in Creative**:
Sometimes keybinds work but feedback is missed. Test in creative with clear chat.

### Keybind Category Not Showing

**Missing Translation**:
- Should say "DuraPing" in Controls menu
- If shows "key.category.duraping.keybinds", reinstall mod

**Mod Not Loaded**:
- Check Mods menu to confirm DuraPing is loaded
- Verify `.jar` file is in `mods` folder

### Feedback Not Appearing

**Chat May Be Full**:
- Try in singleplayer with empty chat
- Hotbar overlay should still appear

**Hotbar Overlay Short Duration**:
- Message only shows ~3 seconds
- Try pressing again to confirm it's working

### Numpad Keys Not Registering

**Num Lock State**:
- Ensure Num Lock is ON
- Numpad keys don't work with Num Lock OFF

**Laptop Keyboards**:
- Some laptops don't have dedicated Numpad
- Rebind to function keys (F6-F8) or letter keys

---

## Advanced Keybinding Tips

### Macro Integration

DuraPing keybindings can be integrated with macro mods:

**Example with a macro mod**:
```
Macro: When inventory opens, press Numpad 9
Result: Auto-check hand durability on inventory open
```

**Example with gaming mouse**:
```
Mouse button 4: Numpad 8 (snooze)
Mouse button 5: Numpad 9 (show durability)
```

### Stream Deck / External Devices

Assign DuraPing keybinds to external devices:
1. Bind DuraPing functions to unused keys (e.g., F13-F15)
2. Program external device to send those key codes
3. One-button control without in-game key conflicts

### Accessibility

For players with limited keyboard access:
1. Rebind to easily reachable keys
2. Use larger keys (Spacebar, Enter) with modifiers
3. Consider voice control software mapping to keybinds

---

## Next Steps

- **[Configuration](Configuration)** - Adjust snooze duration and alert behavior
- **[Features & Usage](Features-and-Usage)** - Learn what each keybind controls
- **[Troubleshooting](Troubleshooting)** - Solve keybind issues

---

Have keybinding questions? Check the [FAQ](FAQ) or [open an issue](https://github.com/redlynxlabs/duraping/issues).


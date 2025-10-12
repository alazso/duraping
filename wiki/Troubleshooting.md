# Troubleshooting

This guide covers common issues and their solutions.

## Installation Issues

### Game Won't Launch After Installing DuraPing

**Symptoms**: Minecraft crashes or shows error screen immediately after clicking "Play".

**Cause**: Usually a dependency or version mismatch.

**Solutions**:

**1. Check Java Version**
```bash
java -version
```
Must be Java 21 or higher. [Download Java 21](https://adoptium.net/) if needed.

**2. Verify Fabric Loader**
- Required: 0.17.3 or higher
- In Minecraft Launcher, check profile name: Should include "fabric-loader-0.17.3" or higher
- Reinstall Fabric Loader if needed: [fabricmc.net/use](https://fabricmc.net/use/)

**3. Check Fabric API Version**
- Required: 0.134.0+1.21.9 or higher for Minecraft 1.21.9
- Download correct version: [Modrinth](https://modrinth.com/mod/fabric-api)
- Place in `mods` folder

**4. Check Logs**
Open `.minecraft/logs/latest.log` and search for:
- `duraping` - Look for error messages
- `fabric` - Check for Fabric-related errors
- `java.lang` - Check for Java errors

**Common Error Messages**:

| Error | Solution |
|-------|----------|
| `java.lang.UnsupportedClassVersionError` | Update to Java 21+ |
| `net.fabricmc.loader.impl.FormattedException: Mod resolution encountered an incompatible mod set!` | Check Fabric API version |
| `Could not find required mod: cloth-config2` | Install Cloth Config or Mod Menu |

---

### DuraPing Not Showing in Mod Menu

**Symptoms**: Mod Menu opens but DuraPing isn't listed.

**Solutions**:

**1. Verify File Location**
- DuraPing `.jar` should be in `.minecraft/mods/` folder
- NOT in a subfolder
- File name should be like: `duraping-0.3.1.jar`

**2. Check Minecraft Version**
- DuraPing for 1.21.9 only works with Minecraft 1.21.9
- Download correct version from [Releases](https://github.com/redlynxlabs/duraping/releases)

**3. Verify Mod Loaded**
Check `logs/latest.log` for:
```
[FabricLoader] Loading ... mods:
	...
	- duraping 0.3.1
	...
```

If missing, DuraPing isn't loading. Check file name and location.

---

### Config Button Missing in Mod Menu

**Symptoms**: DuraPing appears in mod list but no "Config" button.

**Cause**: Cloth Config not installed.

**Solution**:
1. Install [Mod Menu](https://modrinth.com/mod/modmenu) (if not already installed)
2. Cloth Config will be automatically included
3. Restart Minecraft

**Alternative**: Edit config file manually at `.minecraft/config/duraping.json`

---

## Alert Issues

### Not Receiving Alerts

**Symptoms**: Item durability gets low but no alerts appear.

**Check These Settings**:

**1. Master Toggle**
- Press Numpad 7 (or your toggle keybind)
- Look for "DuraPing: ENABLED" message
- Or check config: `"enabled": true`

**2. Snooze Mode**
- Press Numpad 8 (or your snooze keybind)
- Look for "Snooze CANCELLED" message
- Verify you're not in snooze period

**3. Alert Types Enabled**
Open Mod Menu → DuraPing → Config:
- At least one alert type should be enabled (chat, sound, flash, toast)
- Or check config: At least one of `chat`, `sound`, `flash`, `toast` should be `true`

**4. Thresholds**
- Verify your thresholds are reasonable: Default 25/10/5
- Check if item has per-item override that might be too low
- Formula: Alert fires when `(remaining_uses / max_durability) * 100 <= threshold`

**5. Cooldowns**
- Alerts may be on cooldown
- Wait for cooldown period (default 30s for warn, 15s for danger, 7s for critical)
- Or lower cooldowns in config

**6. Item Type**
- Only damageable items trigger alerts
- Check if item has durability bar
- Non-damageable items (stone, dirt, etc.) never alert

---

### Alerts Spamming

**Symptoms**: Constant alerts for the same item.

**Solutions**:

**1. Enable Activity-Aware Mode**
```json
"activityAware": true,
"workTicksThreshold": 40,
"workCooldownSec": 30
```
This detects continuous mining and extends cooldowns.

**2. Increase Cooldowns**
```json
"warnCooldownSec": 60,
"dangerCooldownSec": 30,
"criticalCooldownSec": 15
```

**3. Enable Quiet Mode**
```json
"quietBelowWarn": true
```
Reduces repeat warn-level alerts to visual-only.

**4. Check Hysteresis**
If using Mending or modded durability regen:
```json
"hysteresisPct": 3
```
Prevents re-alerting when durability fluctuates.

---

### Missing Critical Alerts

**Symptoms**: Never getting critical alerts, only warn/danger.

**Possible Causes**:

**1. Items Breaking Before Critical**
- Your usage pattern may skip critical threshold
- Solution: Raise critical threshold to 8-10%

**2. Config Issue**
- Verify `critical` threshold is set correctly
- Check `config/duraping.json`: `"critical": 5`

**3. Threshold Order**
- Thresholds must be: `warn >= danger >= critical`
- If critical > danger, it's automatically clamped
- Fix by ensuring proper order

---

## Keybinding Issues

### Keybind Not Responding

**Solutions**:

**1. Check Conflicts**
- Options → Controls → Look for red highlighted keys
- Reassign conflicting keybinds

**2. Verify Assignment**
- Options → Controls → DuraPing category
- Confirm keys are correctly assigned
- Try rebinding to a different key

**3. Num Lock (for Numpad)**
- Ensure Num Lock is ON
- Numpad keys require Num Lock enabled

**4. Laptop Keyboards**
- Some laptops lack dedicated Numpad
- Rebind to function keys: F6, F7, F8
- Or use letter keys: J, K, L

---

### Keybind Feedback Not Showing

**Symptoms**: Pressing keybind seems to work but no message appears.

**Solutions**:

**1. Check Chat**
- Messages appear in chat
- Chat may be full or scrolled
- Try in singleplayer with empty chat

**2. Check Hotbar**
- Messages also appear above hotbar
- Duration is only ~3 seconds
- Try pressing again and watch hotbar carefully

**3. Test Each Keybind**
```
Numpad 7: Should see "ENABLED" or "DISABLED"
Numpad 8: Should see "Snoozed" or "CANCELLED"
Numpad 9: Should see durability info or "No durable item"
```

---

## Performance Issues

### FPS Drops After Installing DuraPing

**Rare but possible solutions**:

**1. Disable Screen Flash**
```json
"flash": false
```
Flash overlay has minimal GPU impact but can be disabled.

**2. Reduce Alert Types**
Multiple alert types firing simultaneously might cause micro-stutters:
```json
"chat": true,
"sound": true,
"flash": false,
"toast": false
```

**3. Check for Mod Conflicts**
Test with only DuraPing + Fabric API + Mod Menu:
1. Create new Minecraft profile
2. Install only required mods
3. Test performance
4. Add other mods back one at a time

**4. Update Fabric API**
Older Fabric API versions may have performance issues. Update to latest.

---

## Configuration Issues

### Config File Not Saving

**Symptoms**: Changes in Mod Menu don't persist after restart.

**Solutions**:

**1. File Permissions**
Verify `.minecraft/config/` folder has write permissions:
- **Windows**: Right-click folder → Properties → Security
- **Linux/Mac**: `chmod 755 ~/.minecraft/config`

**2. File Location**
Verify config is in correct location:
- `.minecraft/config/duraping.json` (default)
- MultiMC/Prism: `instances/[instance]/minecraft/config/duraping.json`

**3. JSON Syntax Errors**
If manually editing, validate JSON:
- Use [jsonlint.com](https://jsonlint.com/)
- Check for missing commas, quotes, brackets

**4. Read-Only File**
Check if file is marked read-only:
- **Windows**: Right-click → Properties → Uncheck "Read-only"
- **Linux/Mac**: `chmod 644 ~/.minecraft/config/duraping.json`

---

### Config Resets to Default

**Symptoms**: Config reverts to defaults on game start.

**Causes**:

**1. Invalid JSON**
Syntax errors cause config to be regenerated:
- Validate with JSON linter
- Check logs for parse errors

**2. Version Update**
Major version updates may reset config:
- Backup config before updating
- Manually re-apply your settings

**3. File Deletion**
Something is deleting the config file:
- Check antivirus software
- Check backup software

---

## Multiplayer Issues

### Alerts Not Working on Server

**Note**: DuraPing is client-only and should work on all servers.

**If alerts aren't working**:

**1. Server Doesn't Need DuraPing**
- DuraPing works 100% client-side
- Server doesn't need to have DuraPing installed
- Works on vanilla, Fabric, and Forge servers

**2. Check Client Configuration**
- Settings apply client-side only
- Verify config on your client
- Server can't control DuraPing settings

**3. Packet Loss**
High latency might delay durability updates:
- Durability is tracked client-side but updated by server
- High ping may cause delayed alerts
- This is rare and usually not noticeable

---

### Different Behavior on Server vs Singleplayer

**This is normal**: Server may send durability updates at different rates.

**If alerts are significantly different**:
- Check if server has custom plugins affecting durability
- Verify client config is same for both environments
- Test on vanilla server vs modded server

---

## Compatibility Issues

### Conflict with Other Mods

**Symptoms**: Crash or errors when using DuraPing with other mods.

**Diagnosis**:

**1. Isolate the Conflict**
- Remove all mods except DuraPing
- Add mods back one at a time
- Identify which mod causes the issue

**2. Check Logs**
Look for error messages mentioning both mods:
```
[ERROR] Mixin apply failed duraping.mixins.json ...
```

**3. Report the Issue**
[Open an issue](https://github.com/redlynxlabs/duraping/issues) with:
- Both mod names and versions
- Full logs
- Steps to reproduce

**Known Compatible Mods**:
- Sodium, Lithium, Phosphor
- Iris Shaders
- REI, JEI
- Inventory Tweaks
- All client-side optimization mods

**No Known Conflicts**: DuraPing has no known mod conflicts as of current version.

---

## Advanced Troubleshooting

### Debug Mode

Enable detailed logging (if implemented in future versions):
```json
"debug": true
```
This would log all durability checks to console.

### Manual Config Reset

To completely reset configuration:
1. Close Minecraft
2. Delete `.minecraft/config/duraping.json`
3. Restart Minecraft
4. Config regenerates with defaults

### Clean Reinstall

If all else fails:
1. Close Minecraft
2. Delete `mods/duraping-*.jar`
3. Delete `config/duraping.json`
4. Restart Minecraft (verify mod is gone)
5. Download latest DuraPing version
6. Place in `mods` folder
7. Restart Minecraft

---

## Reporting Bugs

If you've tried everything and still have issues:

**1. Gather Information**
- Minecraft version
- DuraPing version
- Fabric Loader version
- Fabric API version
- List of other mods installed

**2. Collect Logs**
- `.minecraft/logs/latest.log`
- `.minecraft/crash-reports/` (if crashed)

**3. Reproduce the Issue**
- Document exact steps to reproduce
- Test with minimal mods (DuraPing + Fabric API only)

**4. Open an Issue**
[Create a bug report](https://github.com/redlynxlabs/duraping/issues/new/choose) using the bug report template.

Include:
- All information from step 1
- Logs from step 2
- Reproduction steps from step 3
- Your config file (if relevant)

---

## Next Steps

- **[Installation](Installation)** - Verify correct installation
- **[Configuration](Configuration)** - Review settings
- **[FAQ](FAQ)** - Check frequently asked questions

Still stuck? [Open an issue](https://github.com/redlynxlabs/duraping/issues) or check existing issues for similar problems.


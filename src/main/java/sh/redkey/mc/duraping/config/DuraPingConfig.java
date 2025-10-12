package sh.redkey.mc.duraping.config;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DuraPingConfig {
    public boolean enabled = true;

    public boolean chat = true;
    public boolean sound = true;
    public boolean flash = false;
    public boolean toast = false;

    public boolean elytraGuard = true;

    public int warn = 25;
    public int danger = 10;
    public int critical = 5;

    // How many percentage points above a threshold you must recover before re-arming alerts.
    // Example: warn at 25%, re-arm when back above 28% (25 + 3).
    // Default 0: disabled (vanilla durability never recovers naturally).
    // Set > 0 for: Mending with XP orbs, modded durability regen, or rapid item swapping.
    public int hysteresisPct = 0;

    // Cooldowns per severity (seconds) for same item/slot while in the same bucket.
    public int warnCooldownSec = 30;
    public int dangerCooldownSec = 15;
    public int criticalCooldownSec = 7;

    // When the player is holding left click and continuously breaking blocks,
    // suppress repeat warn/danger pings for longer to avoid spam.
    public boolean activityAware = true;
    public int workTicksThreshold = 40;       // ~2s of continuous mining
    public int workCooldownSec = 30;

    // Optional: suppress repeated alerts in warn bucket except for first crossing
    public boolean quietBelowWarn = false;

    // Snooze duration in minutes
    public int snoozeDurationMinutes = 5;

    public Map<String, Thresholds> overrides = new HashMap<>();

    public Thresholds thresholdsFor(Identifier id) {
        Thresholds ov = overrides.get(id.toString());
        if (ov != null) return ov.normalize();
        return new Thresholds(warn, danger, critical).normalize();
    }

    public record Thresholds(int warn, int danger, int critical) {
        public Thresholds normalize() {
            int w = Math.max(1, warn);
            int d = Math.max(1, Math.min(danger, w));
            int c = Math.max(1, Math.min(critical, d));
            return new Thresholds(w, d, c);
        }
    }

    private static DuraPingConfig INSTANCE = new DuraPingConfig();
    public static DuraPingConfig get() { return INSTANCE; }
    public static void set(DuraPingConfig v) { INSTANCE = v; }
}


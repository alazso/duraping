package sh.redkey.mc.duraping.config;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DuraPingConfig {
    public boolean enabled = true;

    public boolean chat = true;
    public boolean sound = true;
    public boolean flash = true;
    public boolean toast = false;

    public boolean elytraGuard = true;

    public int warn = 25;
    public int danger = 10;
    public int critical = 5;

    public long cooldownMs = 10_000L;

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


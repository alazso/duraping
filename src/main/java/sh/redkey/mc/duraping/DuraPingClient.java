package sh.redkey.mc.duraping;

import sh.redkey.mc.duraping.config.ConfigManager;
import sh.redkey.mc.duraping.config.DuraPingConfig;
import sh.redkey.mc.duraping.hud.HudFlashOverlay;
import sh.redkey.mc.duraping.keybind.Keybinds;
import sh.redkey.mc.duraping.sound.ModSounds;
import sh.redkey.mc.duraping.util.AlertState;
import sh.redkey.mc.duraping.util.DurabilityCalc;
import sh.redkey.mc.duraping.util.ItemKey;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DuraPingClient implements ClientModInitializer {

    public static final String MOD_ID = "duraping";
    private static final MinecraftClient MC = MinecraftClient.getInstance();

    private static final Map<String, AlertState> stateByKey = new HashMap<>();
    private static int breakingTicks = 0;
    private static long snoozeUntil = 0;

    private static String keyFor(ItemKey key, EquipmentSlot slot) {
        // Distinguish armor slots vs. hand stacks of the same item ID.
        var base = key.id().toString();
        var slotTag = (slot == null) ? "HAND" : slot.getName();
        return base + "#" + slotTag;
    }

    @Override public void onInitializeClient() {
        ConfigManager.load();
        ModSounds.register();
        Keybinds.register();
        HudRenderCallback.EVENT.register(new HudFlashOverlay());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (!DuraPingConfig.get().enabled) return;
            if (System.currentTimeMillis() < snoozeUntil) return;

            // Track "work mode" (continuous block breaking)
            if (client.interactionManager != null && client.interactionManager.isBreakingBlock()) {
                breakingTicks++;
            } else {
                breakingTicks = 0;
            }

            // Hands
            checkStack(ItemKey.of(client.player.getMainHandStack()), null);
            checkStack(ItemKey.of(client.player.getOffHandStack()), null);

            // Armor
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
                ItemStack st = client.player.getEquippedStack(slot);
                checkStack(ItemKey.of(st), slot);
            }

            // Keybinds
            Keybinds.tick(() -> {
                DuraPingConfig cfg = DuraPingConfig.get();
                cfg.enabled = !cfg.enabled;
                ConfigManager.save();
                toast("DuraPing " + (cfg.enabled ? "Enabled" : "Disabled"));
            }, () -> {
                snoozeUntil = System.currentTimeMillis() + (5 * 60_000L);
                toast("DuraPing snoozed for 5m");
            }, () -> {
                ItemStack h = client.player.getMainHandStack();
                if (h.isEmpty() || !h.isDamageable()) {
                    toast("No durable item in hand");
                } else {
                    int left = h.getMaxDamage() - h.getDamage();
                    int pct = (int)Math.floor(100.0 * left / h.getMaxDamage());
                    toast(h.getName().getString() + ": " + left + " (" + pct + "%)");
                }
            });
        });
    }

    private void checkStack(ItemKey key, EquipmentSlot slot) {
        if (key.isEmpty() || !key.stack().isDamageable()) return;

        DuraPingConfig cfg = DuraPingConfig.get();

        int max = Math.max(1, key.stack().getMaxDamage());
        int left = Math.max(0, max - key.stack().getDamage());
        int pct = (int)Math.floor((left * 100.0) / max);

        var th = cfg.thresholdsFor(key.id());
        int bucket =
                (left <= 1)               ? 3 :
                (pct <= th.critical())    ? 3 :
                (pct <= th.danger())      ? 2 :
                (pct <= th.warn())        ? 1 : 0;

        String k = keyFor(key, slot);
        var st = stateByKey.computeIfAbsent(k, _k -> new AlertState());

        // --- Re-arm logic with hysteresis ---
        // If we went ABOVE the boundary + hysteresis, re-arm.
        if (st.lastBucket > 0) {
            int armAt = switch (st.lastBucket) {
                case 3 -> th.critical() + cfg.hysteresisPct;
                case 2 -> th.danger()   + cfg.hysteresisPct;
                case 1 -> th.warn()     + cfg.hysteresisPct;
                default -> 101;
            };
            if (pct > armAt) st.armed = true;
        } else {
            // bucket 0 means we're healthy; always armed when we're out of danger
            st.armed = true;
        }

        // --- Determine if we should alert now ---
        boolean crossedDown = (bucket > 0 && bucket > st.lastBucket); // e.g., 0->1, 1->2, 2->3
        boolean sameBucket = (bucket == st.lastBucket && bucket > 0);

        long now = System.currentTimeMillis();
        long bucketCooldown = switch (bucket) {
            case 3 -> cfg.criticalCooldownMs;
            case 2 -> cfg.dangerCooldownMs;
            case 1 -> cfg.warnCooldownMs;
            default -> 0L;
        };

        // Activity-aware suppression for warn/danger (not for critical)
        if (cfg.activityAware && breakingTicks >= cfg.workTicksThreshold && (bucket == 1 || bucket == 2)) {
            bucketCooldown = Math.max(bucketCooldown, cfg.workCooldownMs);
        }

        boolean cooldownOk = (now - st.lastAlertAt) >= bucketCooldown;

        // Alert only on downward crossing when armed, OR (optionally) rate-limited repeats in same bucket.
        boolean shouldAlert = (crossedDown && st.armed) || (sameBucket && st.armed && cooldownOk && bucket > 1);
        // For warn bucket repeats, make it very conservative:
        if (sameBucket && bucket == 1) shouldAlert = st.armed && cooldownOk && breakingTicks < cfg.workTicksThreshold;

        if (bucket == 0) {
            // fully healthy; reset soft state but keep armed=true
            st.lastBucket = 0;
            st.lastPct = pct;
            return;
        }

        // Optional quiet mode for warn bucket
        if (cfg.quietBelowWarn && bucket == 1 && !crossedDown && shouldAlert) {
            // Visual-only alerts, no sound/chat
            if (cfg.toast) toast("Low " + key.displayName());
            if (cfg.flash) HudFlashOverlay.flash(0.25f);
            st.lastAlertAt = now;
            st.lastBucket = bucket;
            st.lastPct = pct;
            return;
        }

        if (shouldAlert && (System.currentTimeMillis() >= snoozeUntil)) {
            // fire full alert
            boolean critical = (bucket == 3);
            if (cfg.chat)   MC.player.sendMessage(critical
                    ? Text.translatable("text.duraping.critical", key.displayName(), left)
                    : Text.translatable("text.duraping.warn",     key.displayName(), left), false);
            if (cfg.sound)  MC.player.playSound(critical ? ModSounds.CRITICAL : ModSounds.WARN, 1.0F, 1.0F);
            if (cfg.flash)  HudFlashOverlay.flash(critical ? 0.55f : 0.30f);
            if (cfg.toast)  toast((critical ? "CRITICAL " : "Low ") + key.displayName());

            st.lastAlertAt = now;

            // After a downward crossing, disarm until we recover above threshold + hysteresis
            if (crossedDown) st.armed = false;

            // Optional: Elytra guard
            if (cfg.elytraGuard && key.id().equals(Identifier.of("minecraft", "elytra")) && critical) {
                // reserved for glide start hook if later added
            }
        }

        // Update last seen
        st.lastBucket = bucket;
        st.lastPct = pct;
    }

    public static void toast(String msg) {
        if (MC == null || MC.getToastManager() == null) return;
        MC.inGameHud.setOverlayMessage(Text.literal(msg), false);
    }

    public static Identifier id(String path) { return Identifier.of(MOD_ID, path); }
}


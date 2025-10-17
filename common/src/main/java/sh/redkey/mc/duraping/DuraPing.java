package sh.redkey.mc.duraping;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import sh.redkey.mc.duraping.config.ConfigManager;
import sh.redkey.mc.duraping.config.DuraPingConfig;
import sh.redkey.mc.duraping.sound.SoundEvents;
import sh.redkey.mc.duraping.util.AlertState;
import sh.redkey.mc.duraping.util.AutoSwapUtil;
import sh.redkey.mc.duraping.util.ItemKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Core DuraPing client logic - platform-independent
 * This class handles all the durability monitoring and alert logic
 */
public class DuraPing {
    private static final Minecraft MC = Minecraft.getInstance();
    private static final Map<String, AlertState> stateByKey = new HashMap<>();
    private static int breakingTicks = 0;
    private static long snoozeUntil = 0;
    private static float flashAlpha = 0f;
    private static long flashUntil = 0;

    /**
     * Initialize the mod - called by platform-specific entrypoints
     */
    public static void init() {
        Constants.LOG.info("Initializing DuraPing");
        ConfigManager.load();
        // Sound events are registered automatically by NeoForge
        // ModSounds.register();
    }

    /**
     * Main client tick handler - should be called every tick by platform implementations
     */
    public static void onClientTick() {
        var client = MC;
        if (client.player == null || client.level == null) return;

        // Master toggle and snooze checks - return early if alerts disabled
        if (!DuraPingConfig.get().enabled) return;
        if (System.currentTimeMillis() < snoozeUntil) return;

        // Track "work mode" (continuous block breaking)
        if (client.gameMode != null && client.gameMode.isDestroying()) {
            breakingTicks++;
        } else {
            breakingTicks = 0;
        }

        // Hands
        checkStack(ItemKey.of(client.player.getMainHandItem()), null);
        checkStack(ItemKey.of(client.player.getOffhandItem()), null);

        // Armor - check durability alerts
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
            ItemStack st = client.player.getItemBySlot(slot);
            checkStack(ItemKey.of(st), slot);
        }
        
        // Auto-swap armor on tick (armor doesn't trigger attack/use events like tools)
        // Check every 10 ticks (~0.5 seconds) to reduce overhead
        if (client.player.tickCount % 10 == 0 && DuraPingConfig.get().autoSwapEnabled && DuraPingConfig.get().autoSwapArmor) {
            AutoSwapUtil.checkAndSwapArmor(client.player);
        }
    }

    /**
     * Called when player attacks a block - triggers auto-swap check for tools
     */
    public static void onAttackBlock() {
        var client = MC;
        if (client.player != null) {
            AutoSwapUtil.checkAndSwapTool(client.player);
        }
    }

    /**
     * Called when player uses a block - triggers auto-swap check for tools
     */
    public static void onUseBlock() {
        var client = MC;
        if (client.player != null) {
            AutoSwapUtil.checkAndSwapTool(client.player);
        }
    }

    /**
     * Toggle keybind handler
     */
    public static void onTogglePressed() {
        DuraPingConfig cfg = DuraPingConfig.get();
        cfg.enabled = !cfg.enabled;
        ConfigManager.save();
        // More visible feedback with color
        var msg = Component.literal("DuraPing: " + (cfg.enabled ? "ENABLED" : "DISABLED"))
                .withStyle(style -> style.withColor(cfg.enabled ? 0x55FF55 : 0xFF5555).withBold(true));
        MC.gui.setOverlayMessage(msg, false);
        if (MC.player != null) MC.player.displayClientMessage(msg, false);
    }

    /**
     * Snooze keybind handler
     */
    public static void onSnoozePressed() {
        DuraPingConfig cfg = DuraPingConfig.get();
        long now = System.currentTimeMillis();
        if (now < snoozeUntil) {
            // Already snoozed - cancel it
            snoozeUntil = 0;
            var msg = Component.literal("DuraPing: Snooze CANCELLED")
                    .withStyle(style -> style.withColor(0x55FF55).withBold(true));
            MC.gui.setOverlayMessage(msg, false);
            if (MC.player != null) MC.player.displayClientMessage(msg, false);
        } else {
            // Not snoozed - activate snooze
            int minutes = Math.max(1, cfg.snoozeDurationMinutes); // Minimum 1 minute
            snoozeUntil = now + (minutes * 60_000L);
            var msg = Component.literal("DuraPing: Snoozed for " + minutes + " minute" + (minutes == 1 ? "" : "s"))
                    .withStyle(style -> style.withColor(0xFFAA00).withBold(true));
            MC.gui.setOverlayMessage(msg, false);
            if (MC.player != null) MC.player.displayClientMessage(msg, false);
        }
    }

    /**
     * Show durability keybind handler
     */
    public static void onShowPressed() {
        var client = MC;
        if (client.player == null) return;
        
        ItemStack h = client.player.getMainHandItem();
        if (h.isEmpty() || !h.isDamageableItem()) {
            toast("No durable item in hand");
        } else {
            int left = h.getMaxDamage() - h.getDamageValue();
            int pct = (int)Math.floor(100.0 * left / h.getMaxDamage());
            toast(h.getHoverName().getString() + ": " + left + " (" + pct + "%)");
        }
    }

    /**
     * Auto-swap keybind handlers
     */
    public static void onAutoSwapPressed() {
        var client = MC;
        if (client.player != null) {
            AutoSwapUtil.manualAutoSwap(client.player);
        }
    }

    public static void onAutoSwapMainHandPressed() {
        var client = MC;
        if (client.player != null) {
            AutoSwapUtil.manualAutoSwapMainHand(client.player);
        }
    }

    public static void onAutoSwapArmorPressed() {
        var client = MC;
        if (client.player != null) {
            AutoSwapUtil.manualAutoSwapArmor(client.player);
        }
    }

    /**
     * Core durability checking logic
     */
    private static void checkStack(ItemKey key, EquipmentSlot slot) {
        if (key.isEmpty() || !key.stack().isDamageableItem()) return;

        DuraPingConfig cfg = DuraPingConfig.get();

        int max = Math.max(1, key.stack().getMaxDamage());
        int left = Math.max(0, max - key.stack().getDamageValue());
        int pct = (int)Math.floor((left * 100.0) / max);

        // === EMERGENCY 2-DURABILITY ALERT (bypasses snooze) ===
        if (left == 2) {
            String k = keyFor(key, slot);
            var st = stateByKey.computeIfAbsent(k, _k -> new AlertState());
            
            // Only fire once per item (when crossing into 2-durability, or if not yet tracked)
            // Check if we haven't alerted for 2-durability yet (lastPct will be different)
            boolean shouldEmergencyAlert = (st.lastBucket == 0 || st.lastPct > pct);
            
            if (shouldEmergencyAlert) {
                // Emergency alert format
                MutableComponent emergencyMsg = Component.literal("---------------------------------------------\n")
                        .append(Component.literal("EMERGENCY: CRITICAL 2 DURABILITY\n").withStyle(style -> style.withColor(0xAA0000).withBold(true)))
                        .append(Component.literal(key.displayName() + " - " + left + " uses left\n").withStyle(style -> style.withColor(0xFF5555)))
                        .append(Component.literal("---------------------------------------------"));
                
                if (cfg.chat && MC.player != null) MC.player.displayClientMessage(emergencyMsg, false);
                if (cfg.toast) {
                    toast("⚠ EMERGENCY: " + key.displayName() + " - 2 DURABILITY");
                }
                if (cfg.sound && MC.player != null) {
                    // Play critical sound 3 times in succession with varying pitch
                    MC.player.playSound(SoundEvents.CRITICAL, 1.0F, 1.0F);
                    MC.player.playSound(SoundEvents.CRITICAL, 1.0F, 1.2F);
                    MC.player.playSound(SoundEvents.CRITICAL, 1.0F, 0.8F);
                }
                if (cfg.flash) flash(0.75f); // Max intensity flash
            }
        }

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
            case 3 -> cfg.criticalCooldownSec * 1000L;
            case 2 -> cfg.dangerCooldownSec * 1000L;
            case 1 -> cfg.warnCooldownSec * 1000L;
            default -> 0L;
        };

        // Activity-aware suppression for warn/danger (not for critical)
        if (cfg.activityAware && breakingTicks >= cfg.workTicksThreshold && (bucket == 1 || bucket == 2)) {
            bucketCooldown = Math.max(bucketCooldown, cfg.workCooldownSec * 1000L);
        }

        boolean cooldownOk = (now - st.lastAlertAt) >= bucketCooldown;

        // Alert only on downward crossing when armed, OR (optionally) rate-limited repeats in same bucket.
        // Critical bucket (3) ALWAYS repeats on cooldown for safety, regardless of armed status
        boolean shouldAlert = (crossedDown && st.armed) || (sameBucket && st.armed && cooldownOk && bucket > 1);
        // Critical bucket override: always repeat on cooldown even if disarmed (safety critical)
        if (sameBucket && bucket == 3 && cooldownOk) shouldAlert = true;
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
            if (cfg.flash) flash(0.25f);
            st.lastAlertAt = now;
            st.lastBucket = bucket;
            st.lastPct = pct;
            return;
        }

        if (shouldAlert && (System.currentTimeMillis() >= snoozeUntil)) {
            // fire full alert
            boolean critical = (bucket == 3);
            
            if (cfg.chat) {
                var msg = switch (bucket) {
                    case 3 -> Component.translatable("text.duraping.critical", key.displayName(), left)
                            .withStyle(style -> style.withColor(0xAA0000).withBold(true)); // Dark red + bold
                    case 2 -> Component.translatable("text.duraping.danger", key.displayName(), left)
                            .withStyle(style -> style.withColor(0xFF5555)); // Red/orange
                    default -> Component.translatable("text.duraping.warn", key.displayName(), left)
                            .withStyle(style -> style.withColor(0xFFAA00)); // Gold/yellow
                };
                MC.player.displayClientMessage(msg, false);
            }
            if (cfg.sound)  MC.player.playSound(critical ? SoundEvents.CRITICAL : SoundEvents.WARN, 1.0F, 1.0F);
            if (cfg.flash)  flash(critical ? 0.55f : 0.30f);
            if (cfg.toast)  toast((critical ? "CRITICAL " : "Low ") + key.displayName());

            st.lastAlertAt = now;

            // After a downward crossing, disarm until we recover above threshold + hysteresis
            if (crossedDown) st.armed = false;

            // Optional: Elytra guard
            if (cfg.elytraGuard && key.id().equals(ResourceLocation.withDefaultNamespace("elytra")) && critical) {
                // reserved for glide start hook if later added
            }
        }

        // Update last seen
        st.lastBucket = bucket;
        st.lastPct = pct;
    }

    private static String keyFor(ItemKey key, EquipmentSlot slot) {
        // Distinguish armor slots vs. hand stacks of the same item ID.
        var base = key.id().toString();
        var slotTag = (slot == null) ? "HAND" : slot.getName();
        return base + "#" + slotTag;
    }

    /**
     * Flash effect for HUD
     */
    public static void flash(float intensity) {
        flashAlpha = intensity;
        flashUntil = System.currentTimeMillis() + 250;
    }

    /**
     * Get current flash alpha for rendering
     */
    public static float getFlashAlpha() {
        if (System.currentTimeMillis() > flashUntil) return 0f;
        return flashAlpha;
    }

    /**
     * Show toast/overlay message
     */
    public static void toast(String msg) {
        if (MC == null || MC.gui == null) return;
        MC.gui.setOverlayMessage(Component.literal(msg), false);
    }

    /**
     * Get resource location for mod assets
     */
    public static ResourceLocation id(String path) { 
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path); 
    }
}


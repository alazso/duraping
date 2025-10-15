package sh.redkey.mc.duraping;

import sh.redkey.mc.duraping.config.ConfigManager;
import sh.redkey.mc.duraping.config.DuraPingConfig;
import sh.redkey.mc.duraping.hud.HudFlashOverlay;
import sh.redkey.mc.duraping.keybind.Keybinds;
import sh.redkey.mc.duraping.sound.ModSounds;
import sh.redkey.mc.duraping.util.AlertState;
import sh.redkey.mc.duraping.util.AutoSwapUtil;
import sh.redkey.mc.duraping.util.ItemKey;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
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
        
        // Register attack/use callbacks for auto-swap (simpler than tick-based tracking)
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            AutoSwapUtil.checkAndSwapTool(player);
            return net.minecraft.util.ActionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            AutoSwapUtil.checkAndSwapTool(player);
            return net.minecraft.util.ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Keybinds - handle BEFORE enabled check so they always work
            Keybinds.tick(() -> {
                DuraPingConfig cfg = DuraPingConfig.get();
                cfg.enabled = !cfg.enabled;
                ConfigManager.save();
                // More visible feedback with color
                var msg = Text.literal("DuraPing: " + (cfg.enabled ? "ENABLED" : "DISABLED"))
                        .styled(style -> style.withColor(cfg.enabled ? 0x55FF55 : 0xFF5555).withBold(true));
                MC.inGameHud.setOverlayMessage(msg, false);
                if (MC.player != null) MC.player.sendMessage(msg, false);
            }, () -> {
                DuraPingConfig cfg = DuraPingConfig.get();
                long now = System.currentTimeMillis();
                if (now < snoozeUntil) {
                    // Already snoozed - cancel it
                    snoozeUntil = 0;
                    var msg = Text.literal("DuraPing: Snooze CANCELLED")
                            .styled(style -> style.withColor(0x55FF55).withBold(true));
                    MC.inGameHud.setOverlayMessage(msg, false);
                    if (MC.player != null) MC.player.sendMessage(msg, false);
                } else {
                    // Not snoozed - activate snooze
                    int minutes = Math.max(1, cfg.snoozeDurationMinutes); // Minimum 1 minute
                    snoozeUntil = now + (minutes * 60_000L);
                    var msg = Text.literal("DuraPing: Snoozed for " + minutes + " minute" + (minutes == 1 ? "" : "s"))
                            .styled(style -> style.withColor(0xFFAA00).withBold(true));
                    MC.inGameHud.setOverlayMessage(msg, false);
                    if (MC.player != null) MC.player.sendMessage(msg, false);
                }
            }, () -> {
                ItemStack h = client.player.getMainHandStack();
                if (h.isEmpty() || !h.isDamageable()) {
                    toast("No durable item in hand");
                } else {
                    int left = h.getMaxDamage() - h.getDamage();
                    int pct = (int)Math.floor(100.0 * left / h.getMaxDamage());
                    toast(h.getName().getString() + ": " + left + " (" + pct + "%)");
                }
            }, () -> {
                // Manual auto-swap trigger
                AutoSwapUtil.manualAutoSwap(client.player);
            }, () -> {
                // Manual main hand auto-swap trigger
                AutoSwapUtil.manualAutoSwapMainHand(client.player);
            }, () -> {
                // Manual armor auto-swap trigger
                AutoSwapUtil.manualAutoSwapArmor(client.player);
            });

            // Master toggle and snooze checks - return early if alerts disabled
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
            
            // Auto-swap is now handled by damage events, not tick checks
        });
    }

    private void checkStack(ItemKey key, EquipmentSlot slot) {
        if (key.isEmpty() || !key.stack().isDamageable()) return;

        DuraPingConfig cfg = DuraPingConfig.get();

        int max = Math.max(1, key.stack().getMaxDamage());
        int left = Math.max(0, max - key.stack().getDamage());
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
                var emergencyMsg = Text.literal("---------------------------------------------\n")
                        .append(Text.literal("EMERGENCY: CRITICAL 2 DURABILITY\n").styled(style -> style.withColor(0xAA0000).withBold(true)))
                        .append(Text.literal(key.displayName() + " - " + left + " uses left\n").styled(style -> style.withColor(0xFF5555)))
                        .append(Text.literal("---------------------------------------------"));
                
                if (cfg.chat && MC.player != null) MC.player.sendMessage(emergencyMsg, false);
                if (cfg.toast) {
                    toast("⚠ EMERGENCY: " + key.displayName() + " - 2 DURABILITY");
                }
                if (cfg.sound && MC.player != null) {
                    // Play critical sound 3 times in succession with varying pitch
                    MC.player.playSound(ModSounds.CRITICAL, 1.0F, 1.0F);
                    MC.player.playSound(ModSounds.CRITICAL, 1.0F, 1.2F);
                    MC.player.playSound(ModSounds.CRITICAL, 1.0F, 0.8F);
                }
                if (cfg.flash) HudFlashOverlay.flash(0.75f); // Max intensity flash
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
            if (cfg.flash) HudFlashOverlay.flash(0.25f);
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
                    case 3 -> Text.translatable("text.duraping.critical", key.displayName(), left)
                            .styled(style -> style.withColor(0xAA0000).withBold(true)); // Dark red + bold
                    case 2 -> Text.translatable("text.duraping.danger", key.displayName(), left)
                            .styled(style -> style.withColor(0xFF5555)); // Red/orange
                    default -> Text.translatable("text.duraping.warn", key.displayName(), left)
                            .styled(style -> style.withColor(0xFFAA00)); // Gold/yellow
                };
                MC.player.sendMessage(msg, false);
            }
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


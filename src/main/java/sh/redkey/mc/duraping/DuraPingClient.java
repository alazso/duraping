package sh.redkey.mc.duraping;

import sh.redkey.mc.duraping.config.ConfigManager;
import sh.redkey.mc.duraping.config.DuraPingConfig;
import sh.redkey.mc.duraping.hud.HudFlashOverlay;
import sh.redkey.mc.duraping.keybind.Keybinds;
import sh.redkey.mc.duraping.sound.ModSounds;
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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class DuraPingClient implements ClientModInitializer {

    public static final String MOD_ID = "duraping";
    private static final MinecraftClient MC = MinecraftClient.getInstance();

    private static final Map<EquipmentSlot, Integer> lastBucket = new EnumMap<>(EquipmentSlot.class);
    private static final Map<EquipmentSlot, Long> lastAlertAt = new EnumMap<>(EquipmentSlot.class);
    private static final Map<ItemKey, Integer> lastHandBucket = new HashMap<>();
    private static long snoozeUntil = 0;

    @Override public void onInitializeClient() {
        ConfigManager.load();
        ModSounds.register();
        Keybinds.register();
        HudRenderCallback.EVENT.register(new HudFlashOverlay());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (!DuraPingConfig.get().enabled) return;
            if (System.currentTimeMillis() < snoozeUntil) return;

            // Hands
            checkStack(ItemKey.of(client.player.getMainHandStack()), null);
            checkStack(ItemKey.of(client.player.getOffHandStack()), null);

            // Armor
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.getType().equals(EquipmentSlot.Type.ARMOR)) continue;
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
        int max = key.stack().getMaxDamage();
        int left = max - key.stack().getDamage();
        if (left < 0) left = 0;
        int pct = (int)Math.floor(100.0 * left / Math.max(1, max));

        var thresholds = cfg.thresholdsFor(key.id());
        int bucket =
                (left <= 1) ? 3 :
                (pct <= thresholds.critical()) ? 3 :
                (pct <= thresholds.danger())   ? 2 :
                (pct <= thresholds.warn())     ? 1 : 0;

        if (bucket == 0) {
            remember(slot, key, 0);
            return;
        }

        int prev = recall(slot, key);
        if (prev == bucket) return; // no crossing

        long now = System.currentTimeMillis();
        if (!cooldownOk(slot, now, cfg.cooldownMs)) return;
        remember(slot, key, bucket);
        stamp(slot, now);

        // Emit alert
        boolean critical = (bucket == 3);
        if (cfg.chat) {
            var msg = critical
                    ? Text.translatable("text.duraping.critical",
                    key.displayName(), left)
                    : Text.translatable("text.duraping.warn",
                    key.displayName(), left);
            MC.player.sendMessage(msg, false);
        }
        if (cfg.sound) {
            MC.player.playSound(
                    critical ? ModSounds.CRITICAL : ModSounds.WARN,
                    1.0F, 1.0F
            );
        }
        if (cfg.flash) HudFlashOverlay.flash(critical ? 0.55f : 0.35f);
        if (cfg.toast) toast((critical ? "CRITICAL " : "Low ") + key.displayName());

        // Optional: Elytra guard
        if (cfg.elytraGuard && key.id().equals(Identifier.of("minecraft", "elytra")) && critical) {
            // reserved for glide start hook if later added
        }
    }

    private static boolean cooldownOk(EquipmentSlot slot, long now, long cooldownMs) {
        Long last = lastAlertAt.get(slot);
        return last == null || now - last >= cooldownMs;
    }
    private static void stamp(EquipmentSlot slot, long now) { if (slot != null) lastAlertAt.put(slot, now); }
    private static int recall(EquipmentSlot slot, ItemKey key) {
        return (slot != null) ? lastBucket.getOrDefault(slot, 0) : lastHandBucket.getOrDefault(key, 0);
    }
    private static void remember(EquipmentSlot slot, ItemKey key, int bucket) {
        if (slot != null) lastBucket.put(slot, bucket); else lastHandBucket.put(key, bucket);
    }

    public static void toast(String msg) {
        if (MC == null || MC.getToastManager() == null) return;
        MC.inGameHud.setOverlayMessage(Text.literal(msg), false);
    }

    public static Identifier id(String path) { return Identifier.of(MOD_ID, path); }
}


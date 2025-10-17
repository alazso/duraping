package sh.redkey.mc.duraping.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var cfg = DuraPingConfig.get();
            var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.literal("DuraPing Config"));
            var entry = builder.entryBuilder();

            // General Category
            var general = builder.getOrCreateCategory(Component.literal("General"));
            general.addEntry(entry.startBooleanToggle(Component.literal("Enabled"), cfg.enabled)
                    .setTooltip(Component.literal("Master toggle for all durability alerts"))
                    .setSaveConsumer(v -> cfg.enabled = v).build());
            general.addEntry(entry.startBooleanToggle(Component.literal("Chat Alerts"), cfg.chat)
                    .setSaveConsumer(v -> cfg.chat = v).build());
            general.addEntry(entry.startBooleanToggle(Component.literal("Sound Alerts"), cfg.sound)
                    .setSaveConsumer(v -> cfg.sound = v).build());
            general.addEntry(entry.startBooleanToggle(Component.literal("Screen Flash"), cfg.flash)
                    .setSaveConsumer(v -> cfg.flash = v).build());
            general.addEntry(entry.startBooleanToggle(Component.literal("Toast Alerts (Hotbar)"), cfg.toast)
                    .setSaveConsumer(v -> cfg.toast = v).build());
            general.addEntry(entry.startBooleanToggle(Component.literal("Elytra Flight Guard"), cfg.elytraGuard)
                    .setTooltip(Component.literal("Extra warning when attempting to fly with critically low elytra"))
                    .setSaveConsumer(v -> cfg.elytraGuard = v).build());
            general.addEntry(entry.startIntField(Component.literal("Snooze Duration (minutes)"), cfg.snoozeDurationMinutes)
                    .setTooltip(Component.literal("How long to snooze alerts when using the snooze keybind"))
                    .setMin(1).setMax(60).setSaveConsumer(v -> cfg.snoozeDurationMinutes = v).build());

            // Thresholds Category
            var thresholds = builder.getOrCreateCategory(Component.literal("Thresholds"));
            thresholds.addEntry(entry.startIntField(Component.literal("Warn %"), cfg.warn)
                    .setTooltip(Component.literal("Percentage at which warn alerts trigger"))
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.warn = v).build());
            thresholds.addEntry(entry.startIntField(Component.literal("Danger %"), cfg.danger)
                    .setTooltip(Component.literal("Percentage at which danger alerts trigger"))
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.danger = v).build());
            thresholds.addEntry(entry.startIntField(Component.literal("Critical %"), cfg.critical)
                    .setTooltip(Component.literal("Percentage at which critical alerts trigger"))
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.critical = v).build());
            thresholds.addEntry(entry.startIntField(Component.literal("Hysteresis %"), cfg.hysteresisPct)
                    .setTooltip(Component.literal("Recovery margin before re-arming. 0=disabled (vanilla). Enable for Mending/modded regen."))
                    .setMin(0).setMax(20).setSaveConsumer(v -> cfg.hysteresisPct = v).build());

            // Cooldowns Category
            var cooldowns = builder.getOrCreateCategory(Component.literal("Cooldowns"));
            cooldowns.addEntry(entry.startIntField(Component.literal("Warn Cooldown (seconds)"), cfg.warnCooldownSec)
                    .setTooltip(Component.literal("Minimum time between warn alerts for the same item"))
                    .setMin(0).setMax(300).setSaveConsumer(v -> cfg.warnCooldownSec = v).build());
            cooldowns.addEntry(entry.startIntField(Component.literal("Danger Cooldown (seconds)"), cfg.dangerCooldownSec)
                    .setTooltip(Component.literal("Minimum time between danger alerts for the same item"))
                    .setMin(0).setMax(300).setSaveConsumer(v -> cfg.dangerCooldownSec = v).build());
            cooldowns.addEntry(entry.startIntField(Component.literal("Critical Cooldown (seconds)"), cfg.criticalCooldownSec)
                    .setTooltip(Component.literal("Minimum time between critical alerts for the same item"))
                    .setMin(0).setMax(60).setSaveConsumer(v -> cfg.criticalCooldownSec = v).build());

            // Activity-Aware Category
            var activity = builder.getOrCreateCategory(Component.literal("Activity-Aware"));
            activity.addEntry(entry.startBooleanToggle(Component.literal("Activity-Aware Mode"), cfg.activityAware)
                    .setTooltip(Component.literal("Suppress alerts during continuous mining to reduce spam"))
                    .setSaveConsumer(v -> cfg.activityAware = v).build());
            activity.addEntry(entry.startIntField(Component.literal("Work Ticks Threshold"), cfg.workTicksThreshold)
                    .setTooltip(Component.literal("How many ticks of continuous mining before suppression kicks in (20 = 1s)"))
                    .setMin(1).setMax(100).setSaveConsumer(v -> cfg.workTicksThreshold = v).build());
            activity.addEntry(entry.startIntField(Component.literal("Work Cooldown (seconds)"), cfg.workCooldownSec)
                    .setTooltip(Component.literal("Extended cooldown during continuous mining"))
                    .setMin(0).setMax(600).setSaveConsumer(v -> cfg.workCooldownSec = v).build());
            activity.addEntry(entry.startBooleanToggle(Component.literal("Quiet Below Warn"), cfg.quietBelowWarn)
                    .setTooltip(Component.literal("In warn bucket, only show visual alerts after first crossing (no sound/chat)"))
                    .setSaveConsumer(v -> cfg.quietBelowWarn = v).build());

            // Auto-Swap Category
            var autoSwap = builder.getOrCreateCategory(Component.literal("Auto-Swap"));
            autoSwap.addEntry(entry.startBooleanToggle(Component.literal("Auto-Swap Enabled"), cfg.autoSwapEnabled)
                    .setTooltip(Component.literal("Automatically swap to more durable items when current item falls below threshold"))
                    .setSaveConsumer(v -> cfg.autoSwapEnabled = v).build());
            autoSwap.addEntry(entry.startIntField(Component.literal("Auto-Swap Threshold (%)"), cfg.autoSwapThreshold)
                    .setTooltip(Component.literal("Durability percentage at which auto-swap triggers"))
                    .setMin(1).setMax(50).setSaveConsumer(v -> cfg.autoSwapThreshold = v).build());
            autoSwap.addEntry(entry.startBooleanToggle(Component.literal("Auto-Swap Tools"), cfg.autoSwapTools)
                    .setTooltip(Component.literal("Enable auto-swap for main hand tools"))
                    .setSaveConsumer(v -> cfg.autoSwapTools = v).build());
            autoSwap.addEntry(entry.startBooleanToggle(Component.literal("Auto-Swap Armor"), cfg.autoSwapArmor)
                    .setTooltip(Component.literal("Enable auto-swap for armor pieces"))
                    .setSaveConsumer(v -> cfg.autoSwapArmor = v).build());
            autoSwap.addEntry(entry.startBooleanToggle(Component.literal("Auto-Swap Elytra"), cfg.autoSwapElytra)
                    .setTooltip(Component.literal("Enable auto-swap for elytra"))
                    .setSaveConsumer(v -> cfg.autoSwapElytra = v).build());
            autoSwap.addEntry(entry.startBooleanToggle(Component.literal("Allow Lower Quality"), cfg.autoSwapAllowLowerQuality)
                    .setTooltip(Component.literal("Allow swapping to lower quality items if they have more durability"))
                    .setSaveConsumer(v -> cfg.autoSwapAllowLowerQuality = v).build());

            // Keybinds Info Category
            var keybinds = builder.getOrCreateCategory(Component.literal("Keybinds"));
            keybinds.addEntry(entry.startTextDescription(
                    Component.literal("§6DuraPing Keybinds:§r\n\n" +
                            "§e• Numpad 7§r - Toggle DuraPing on/off\n" +
                            "§e• Numpad 8§r - Snooze for 5 min / Cancel snooze\n" +
                            "§e• Numpad 9§r - Show current hand durability\n" +
                            "§e• Numpad 0§r - Manual auto-swap trigger\n\n" +
                            "§7Optional keybinds (no defaults assigned):§r\n" +
                            "§7• Auto-Swap Main Hand§r - Bind manually if desired\n" +
                            "§7• Auto-Swap Armor§r - Bind manually if desired\n\n" +
                            "§7To rebind these keys, go to:\n" +
                            "Options → Controls → DuraPing§r")
            ).build());

            builder.setSavingRunnable(ConfigManager::save);
            return builder.build();
        };
    }
}

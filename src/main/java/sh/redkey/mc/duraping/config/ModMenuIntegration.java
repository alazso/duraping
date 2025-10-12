package sh.redkey.mc.duraping.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            var cfg = DuraPingConfig.get();
            var builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("DuraPing Config"));
            var entry = builder.entryBuilder();

            // General Category
            var general = builder.getOrCreateCategory(Text.literal("General"));
            general.addEntry(entry.startBooleanToggle(Text.literal("Enabled"), cfg.enabled)
                    .setTooltip(Text.literal("Master toggle for all durability alerts"))
                    .setSaveConsumer(v -> cfg.enabled = v).build());
            general.addEntry(entry.startBooleanToggle(Text.literal("Chat Alerts"), cfg.chat)
                    .setSaveConsumer(v -> cfg.chat = v).build());
            general.addEntry(entry.startBooleanToggle(Text.literal("Sound Alerts"), cfg.sound)
                    .setSaveConsumer(v -> cfg.sound = v).build());
            general.addEntry(entry.startBooleanToggle(Text.literal("Screen Flash"), cfg.flash)
                    .setSaveConsumer(v -> cfg.flash = v).build());
            general.addEntry(entry.startBooleanToggle(Text.literal("Toast Alerts (Hotbar)"), cfg.toast)
                    .setSaveConsumer(v -> cfg.toast = v).build());
            general.addEntry(entry.startBooleanToggle(Text.literal("Elytra Flight Guard"), cfg.elytraGuard)
                    .setTooltip(Text.literal("Extra warning when attempting to fly with critically low elytra"))
                    .setSaveConsumer(v -> cfg.elytraGuard = v).build());

            // Thresholds Category
            var thresholds = builder.getOrCreateCategory(Text.literal("Thresholds"));
            thresholds.addEntry(entry.startIntField(Text.literal("Warn %"), cfg.warn)
                    .setTooltip(Text.literal("Percentage at which warn alerts trigger"))
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.warn = v).build());
            thresholds.addEntry(entry.startIntField(Text.literal("Danger %"), cfg.danger)
                    .setTooltip(Text.literal("Percentage at which danger alerts trigger"))
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.danger = v).build());
            thresholds.addEntry(entry.startIntField(Text.literal("Critical %"), cfg.critical)
                    .setTooltip(Text.literal("Percentage at which critical alerts trigger"))
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.critical = v).build());
            thresholds.addEntry(entry.startIntField(Text.literal("Hysteresis %"), cfg.hysteresisPct)
                    .setTooltip(Text.literal("Recovery margin before re-arming. 0=disabled (vanilla). Enable for Mending/modded regen."))
                    .setMin(0).setMax(20).setSaveConsumer(v -> cfg.hysteresisPct = v).build());

            // Cooldowns Category
            var cooldowns = builder.getOrCreateCategory(Text.literal("Cooldowns"));
            cooldowns.addEntry(entry.startLongField(Text.literal("Warn Cooldown (ms)"), cfg.warnCooldownMs)
                    .setTooltip(Text.literal("Minimum time between warn alerts for the same item (30000 = 30s)"))
                    .setMin(0L).setSaveConsumer(v -> cfg.warnCooldownMs = v).build());
            cooldowns.addEntry(entry.startLongField(Text.literal("Danger Cooldown (ms)"), cfg.dangerCooldownMs)
                    .setTooltip(Text.literal("Minimum time between danger alerts for the same item (15000 = 15s)"))
                    .setMin(0L).setSaveConsumer(v -> cfg.dangerCooldownMs = v).build());
            cooldowns.addEntry(entry.startLongField(Text.literal("Critical Cooldown (ms)"), cfg.criticalCooldownMs)
                    .setTooltip(Text.literal("Minimum time between critical alerts for the same item (7000 = 7s)"))
                    .setMin(0L).setSaveConsumer(v -> cfg.criticalCooldownMs = v).build());

            // Activity-Aware Category
            var activity = builder.getOrCreateCategory(Text.literal("Activity-Aware"));
            activity.addEntry(entry.startBooleanToggle(Text.literal("Activity-Aware Mode"), cfg.activityAware)
                    .setTooltip(Text.literal("Suppress alerts during continuous mining to reduce spam"))
                    .setSaveConsumer(v -> cfg.activityAware = v).build());
            activity.addEntry(entry.startIntField(Text.literal("Work Ticks Threshold"), cfg.workTicksThreshold)
                    .setTooltip(Text.literal("How many ticks of continuous mining before suppression kicks in (20 = 1s)"))
                    .setMin(1).setMax(100).setSaveConsumer(v -> cfg.workTicksThreshold = v).build());
            activity.addEntry(entry.startLongField(Text.literal("Work Cooldown (ms)"), cfg.workCooldownMs)
                    .setTooltip(Text.literal("Extended cooldown during mining (120000 = 2 minutes)"))
                    .setMin(0L).setSaveConsumer(v -> cfg.workCooldownMs = v).build());
            activity.addEntry(entry.startBooleanToggle(Text.literal("Quiet Below Warn"), cfg.quietBelowWarn)
                    .setTooltip(Text.literal("In warn bucket, only show visual alerts after first crossing (no sound/chat)"))
                    .setSaveConsumer(v -> cfg.quietBelowWarn = v).build());

            // Keybinds Info Category
            var keybinds = builder.getOrCreateCategory(Text.literal("Keybinds"));
            keybinds.addEntry(entry.startTextDescription(
                    Text.literal("§6DuraPing Keybinds (defaults):§r\n\n" +
                            "§e• Numpad 7§r - Toggle DuraPing on/off\n" +
                            "§e• Numpad 8§r - Snooze alerts for 5 minutes\n" +
                            "§e• Numpad 9§r - Show current hand durability\n\n" +
                            "§7To rebind these keys, go to:\n" +
                            "Options → Controls → DuraPing§r")
            ).build());

            builder.setSavingRunnable(ConfigManager::save);
            return builder.build();
        };
    }
}


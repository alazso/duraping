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
            var cat = builder.getOrCreateCategory(Text.literal("General"));
            var entry = builder.entryBuilder();

            cat.addEntry(entry.startBooleanToggle(Text.literal("Enabled"), cfg.enabled)
                    .setSaveConsumer(v -> cfg.enabled = v)
                    .build());
            cat.addEntry(entry.startBooleanToggle(Text.literal("Chat Alerts"), cfg.chat)
                    .setSaveConsumer(v -> cfg.chat = v).build());
            cat.addEntry(entry.startBooleanToggle(Text.literal("Sound Alerts"), cfg.sound)
                    .setSaveConsumer(v -> cfg.sound = v).build());
            cat.addEntry(entry.startBooleanToggle(Text.literal("Screen Flash"), cfg.flash)
                    .setSaveConsumer(v -> cfg.flash = v).build());
            cat.addEntry(entry.startBooleanToggle(Text.literal("Toast Alerts"), cfg.toast)
                    .setSaveConsumer(v -> cfg.toast = v).build());
            cat.addEntry(entry.startIntField(Text.literal("Warn %"), cfg.warn)
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.warn = v).build());
            cat.addEntry(entry.startIntField(Text.literal("Danger %"), cfg.danger)
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.danger = v).build());
            cat.addEntry(entry.startIntField(Text.literal("Critical %"), cfg.critical)
                    .setMin(1).setMax(99).setSaveConsumer(v -> cfg.critical = v).build());
            cat.addEntry(entry.startLongField(Text.literal("Cooldown (ms)"), cfg.cooldownMs)
                    .setMin(0L).setSaveConsumer(v -> cfg.cooldownMs = v).build());

            builder.setSavingRunnable(ConfigManager::save);
            return builder.build();
        };
    }
}


package sh.redkey.mc.duraping.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds implements SoundManager {
    public static final SoundEvent WARN = SoundEvent.of(id("warn"));
    public static final SoundEvent CRITICAL = SoundEvent.of(id("critical"));

    private static Identifier id(String p) { return Identifier.of("duraping", p); }

    @Override
    public void register() {
        Registry.register(Registries.SOUND_EVENT, id("warn"), WARN);
        Registry.register(Registries.SOUND_EVENT, id("critical"), CRITICAL);
    }
    
    @Override
    public void playWarning() {
        var mc = MinecraftClient.getInstance();
        if (mc != null && mc.player != null) {
            mc.player.playSound(WARN, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void playCritical() {
        var mc = MinecraftClient.getInstance();
        if (mc != null && mc.player != null) {
            mc.player.playSound(CRITICAL, 1.0f, 1.0f);
        }
    }
}


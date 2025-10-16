package sh.redkey.mc.duraping.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

public class ModSounds implements SoundManager {
    public static final SoundEvent WARN = SoundEvent.createVariableRangeEvent(new ResourceLocation("duraping", "warn"));
    public static final SoundEvent CRITICAL = SoundEvent.createVariableRangeEvent(new ResourceLocation("duraping", "critical"));

    @Override
    public void register() {
        // NeoForge sound registration will be handled by the platform-specific entry point
        // This is a placeholder implementation
    }
    
    @Override
    public void playWarning() {
        var mc = Minecraft.getInstance();
        if (mc != null && mc.player != null) {
            mc.player.playSound(WARN, 1.0f, 1.0f);
        }
    }
    
    @Override
    public void playCritical() {
        var mc = Minecraft.getInstance();
        if (mc != null && mc.player != null) {
            mc.player.playSound(CRITICAL, 1.0f, 1.0f);
        }
    }
}

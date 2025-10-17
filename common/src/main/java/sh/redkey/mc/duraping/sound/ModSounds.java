package sh.redkey.mc.duraping.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import sh.redkey.mc.duraping.Constants;

public class ModSounds {
    public static final SoundEvent WARN = SoundEvent.createVariableRangeEvent(id("warn"));
    public static final SoundEvent CRITICAL = SoundEvent.createVariableRangeEvent(id("critical"));

    private static ResourceLocation id(String p) { 
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, p); 
    }

    public static void register() {
        // Sound events are now registered automatically by NeoForge
        // No manual registration needed
    }
}


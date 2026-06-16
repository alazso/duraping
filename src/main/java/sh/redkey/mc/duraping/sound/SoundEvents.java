package sh.redkey.mc.duraping.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Platform-agnostic sound events for DuraPing.
 * These are used by the common DuraPing class.
 */
public class SoundEvents {
    public static final SoundEvent WARN = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("duraping", "warn"));
    public static final SoundEvent CRITICAL = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("duraping", "critical"));
}
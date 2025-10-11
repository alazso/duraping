package com.redkey.duraping.sound;

import com.redkey.duraping.DuraPingClient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent WARN = SoundEvent.of(id("warn"));
    public static final SoundEvent CRITICAL = SoundEvent.of(id("critical"));

    private static Identifier id(String p) { return DuraPingClient.id(p); }

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, id("warn"), WARN);
        Registry.register(Registries.SOUND_EVENT, id("critical"), CRITICAL);
    }
}


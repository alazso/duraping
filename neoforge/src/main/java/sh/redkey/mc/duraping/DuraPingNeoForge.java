package sh.redkey.mc.duraping;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.TickEvent;
import sh.redkey.mc.duraping.hud.HudFlashOverlay;
import sh.redkey.mc.duraping.keybind.Keybinds;
import sh.redkey.mc.duraping.sound.ModSounds;

@Mod("duraping")
@EventBusSubscriber(modid = "duraping", bus = EventBusSubscriber.Bus.MOD)
public class DuraPingNeoForge {
    private DuraPingCore core;
    
    public DuraPingNeoForge() {
        // Constructor
    }
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Create platform-specific implementations
        Keybinds keybinds = new Keybinds();
        HudFlashOverlay hudRenderer = new HudFlashOverlay();
        ModSounds soundManager = new ModSounds();
        
        // Initialize the core with platform-specific implementations
        DuraPingNeoForge instance = new DuraPingNeoForge();
        instance.core = new DuraPingCore(keybinds, hudRenderer, soundManager);
        instance.core.init();
    }
}

@EventBusSubscriber(modid = "duraping", bus = EventBusSubscriber.Bus.GAME)
class DuraPingNeoForgeEvents {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Get the core instance and call onClientTick
            // This would need to be properly managed in a real implementation
        }
    }
}

package sh.redkey.mc.duraping;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

@Mod("duraping")
@EventBusSubscriber(modid = "duraping", bus = EventBusSubscriber.Bus.MOD)
public class DuraPingNeoForge {
    
    public DuraPingNeoForge() {
        // Constructor
    }
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Initialize the main client
        DuraPingClient.init();
        
        // Register keybinds
        Keybinds.register();
    }
    
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        // Register key mappings
        Keybinds.registerKeyMappings(event);
    }
}

@EventBusSubscriber(modid = "duraping", bus = EventBusSubscriber.Bus.GAME)
class DuraPingNeoForgeEvents {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            DuraPingClient.onClientTick();
        }
    }
}

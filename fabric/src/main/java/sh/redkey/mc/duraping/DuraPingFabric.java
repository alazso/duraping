package sh.redkey.mc.duraping;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import sh.redkey.mc.duraping.hud.HudFlashOverlay;
import sh.redkey.mc.duraping.keybind.Keybinds;
import sh.redkey.mc.duraping.sound.ModSounds;

public class DuraPingFabric implements ClientModInitializer {
    private DuraPingCore core;
    
    @Override
    public void onInitializeClient() {
        // Create platform-specific implementations
        Keybinds keybinds = new Keybinds();
        HudFlashOverlay hudRenderer = new HudFlashOverlay();
        ModSounds soundManager = new ModSounds();
        
        // Initialize the core with platform-specific implementations
        this.core = new DuraPingCore(keybinds, hudRenderer, soundManager);
        this.core.init();
        
        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                this.core.onClientTick();
            }
        });
    }
    
    public DuraPingCore getCore() {
        return core;
    }
}

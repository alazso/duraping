package sh.redkey.mc.duraping;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;

public class DuraPingFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Initialize the main client
        DuraPingClient.init();
        
        // Register keybinds
        Keybinds.register();
        
        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                DuraPingClient.onClientTick();
            }
        });
    }
}

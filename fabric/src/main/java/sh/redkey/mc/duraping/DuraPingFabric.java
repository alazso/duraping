package sh.redkey.mc.duraping;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DuraPingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("[DuraPing] Fabric mod initialized!");
        
        // Register HUD rendering callback
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            // Simple HUD rendering for now
            if (System.currentTimeMillis() % 2000 < 100) { // Flash every 2 seconds
                var mc = net.minecraft.client.Minecraft.getInstance();
                if (mc != null) {
                    System.out.println("[DuraPing] HUD render - Minecraft instance: " + mc);
                }
            }
        });

        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Simple tick handling for now
            if (client.player != null && System.currentTimeMillis() % 5000 < 50) {
                System.out.println("[DuraPing] Player tick: " + client.player.getName().getString());
            }
        });
    }
}
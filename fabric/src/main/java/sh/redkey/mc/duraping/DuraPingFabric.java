package sh.redkey.mc.duraping;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class DuraPingFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[DuraPing] Fabric mod initialized!");
        
        // Register HUD rendering callback
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            // Simple HUD rendering for now
            if (System.currentTimeMillis() % 2000 < 100) { // Flash every 2 seconds
                var mc = net.minecraft.client.MinecraftClient.getInstance();
                if (mc != null && mc.getWindow() != null) {
                    int w = mc.getWindow().getScaledWidth();
                    int h = mc.getWindow().getScaledHeight();
                    drawContext.fill(0, 0, w, h, 0x20FF0000); // Red tint
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
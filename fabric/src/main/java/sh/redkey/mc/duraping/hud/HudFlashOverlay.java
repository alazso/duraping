package sh.redkey.mc.duraping.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudFlashOverlay implements HudRenderer, HudRenderCallback {
    private static float alpha = 0f;
    private static long until = 0;

    public static void flash(float intensity) {
        alpha = intensity;
        until = System.currentTimeMillis() + 250;
    }

    @Override
    public void render(float partialTicks) {
        // This method is called by the common interface
        // The actual rendering is handled by the Fabric callback
    }

    @Override
    public boolean shouldRender() {
        return System.currentTimeMillis() <= until;
    }

    @Override
    public void onHudRender(DrawContext ctx, RenderTickCounter tickCounter) {
        if (!shouldRender()) return;
        var mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        int a = (int)(alpha * 120) << 24;
        ctx.fill(0, 0, w, h, 0x00FFFFFF | a);
    }
}


package sh.redkey.mc.duraping.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class HudFlashOverlay implements HudRenderer {
    private static float alpha = 0f;
    private static long until = 0;

    public static void flash(float intensity) {
        alpha = intensity;
        until = System.currentTimeMillis() + 250;
    }

    @Override
    public void render(float partialTicks) {
        if (!shouldRender()) return;
        var mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int a = (int)(alpha * 120) << 24;
        // Forge HUD rendering implementation
        // This would need to be integrated with Forge's HUD system
    }

    @Override
    public boolean shouldRender() {
        return System.currentTimeMillis() <= until;
    }
}

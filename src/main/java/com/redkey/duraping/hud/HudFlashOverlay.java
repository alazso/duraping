package com.redkey.duraping.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class HudFlashOverlay implements HudRenderCallback {
    private static float alpha = 0f;
    private static long until = 0;

    public static void flash(float intensity) {
        alpha = intensity;
        until = System.currentTimeMillis() + 250;
    }

    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        if (System.currentTimeMillis() > until) return;
        var mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        int a = (int)(alpha * 120) << 24;
        ctx.fill(0, 0, w, h, 0x00FFFFFF | a);
    }
}


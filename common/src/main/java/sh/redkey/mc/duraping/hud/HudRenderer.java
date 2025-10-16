package sh.redkey.mc.duraping.hud;

/**
 * Common interface for HUD rendering across all platforms.
 * Platform-specific implementations should implement this interface.
 */
public interface HudRenderer {
    /**
     * Render the HUD overlay.
     * @param partialTicks Partial tick time for smooth rendering
     */
    void render(float partialTicks);
    
    /**
     * Check if the HUD should be rendered.
     * @return true if the HUD should be rendered
     */
    boolean shouldRender();
}

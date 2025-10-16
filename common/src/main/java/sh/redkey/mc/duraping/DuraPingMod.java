package sh.redkey.mc.duraping;

/**
 * Common interface for DuraPing mod initialization across all platforms.
 * Platform-specific implementations should implement this interface.
 */
public interface DuraPingMod {
    /**
     * Initialize the mod. Called when the mod is loaded.
     */
    void init();
    
    /**
     * Called every client tick.
     */
    void onClientTick();
}

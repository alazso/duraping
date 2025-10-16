package sh.redkey.mc.duraping.keybind;

/**
 * Common interface for keybind management across all platforms.
 * Platform-specific implementations should implement this interface.
 */
public interface KeybindManager {
    /**
     * Register all keybinds for the platform.
     */
    void register();
    
    /**
     * Check if a keybind was pressed this tick.
     * @param keybind The keybind to check
     * @return true if the keybind was pressed
     */
    boolean wasPressed(KeybindType keybind);
    
    /**
     * Enum for different keybind types.
     */
    enum KeybindType {
        TOGGLE,
        SNOOZE,
        SHOW,
        AUTO_SWAP,
        AUTO_SWAP_MAIN_HAND,
        AUTO_SWAP_ARMOR
    }
}

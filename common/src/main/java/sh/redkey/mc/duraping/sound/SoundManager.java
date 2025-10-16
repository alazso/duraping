package sh.redkey.mc.duraping.sound;

/**
 * Common interface for sound management across all platforms.
 * Platform-specific implementations should implement this interface.
 */
public interface SoundManager {
    /**
     * Play a warning sound.
     */
    void playWarning();
    
    /**
     * Play a critical sound.
     */
    void playCritical();
    
    /**
     * Register all sounds for the platform.
     */
    void register();
}

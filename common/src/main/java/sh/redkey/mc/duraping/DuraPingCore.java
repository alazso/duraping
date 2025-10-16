package sh.redkey.mc.duraping;

import sh.redkey.mc.duraping.config.DuraPingConfig;
import sh.redkey.mc.duraping.hud.HudRenderer;
import sh.redkey.mc.duraping.keybind.KeybindManager;
import sh.redkey.mc.duraping.sound.SoundManager;
import sh.redkey.mc.duraping.util.AutoSwapUtil;

/**
 * Core DuraPing functionality that is shared across all platforms.
 * This class contains the main mod logic and coordinates between platform-specific implementations.
 */
public class DuraPingCore {
    private static DuraPingCore instance;
    private final DuraPingConfig config;
    private final KeybindManager keybindManager;
    private final HudRenderer hudRenderer;
    private final SoundManager soundManager;
    
    public DuraPingCore(KeybindManager keybindManager, HudRenderer hudRenderer, SoundManager soundManager) {
        this.keybindManager = keybindManager;
        this.hudRenderer = hudRenderer;
        this.soundManager = soundManager;
        this.config = DuraPingConfig.getInstance();
        instance = this;
    }
    
    public static DuraPingCore getInstance() {
        return instance;
    }
    
    /**
     * Initialize the core mod functionality.
     */
    public void init() {
        // Register keybinds
        keybindManager.register();
        
        // Register sounds
        soundManager.register();
        
        // Initialize configuration
        config.load();
    }
    
    /**
     * Called every client tick.
     */
    public void onClientTick() {
        // Check keybinds
        if (keybindManager.wasPressed(KeybindManager.KeybindType.TOGGLE)) {
            config.toggleEnabled();
        }
        
        if (keybindManager.wasPressed(KeybindManager.KeybindType.SNOOZE)) {
            config.snooze();
        }
        
        if (keybindManager.wasPressed(KeybindManager.KeybindType.SHOW)) {
            config.showConfig();
        }
        
        if (keybindManager.wasPressed(KeybindManager.KeybindType.AUTO_SWAP)) {
            // Platform-specific player will be passed by the platform implementation
            // AutoSwapUtil.manualAutoSwap(player);
        }
        
        if (keybindManager.wasPressed(KeybindManager.KeybindType.AUTO_SWAP_MAIN_HAND)) {
            // Platform-specific player will be passed by the platform implementation
            // AutoSwapUtil.manualAutoSwapMainHand(player);
        }
        
        if (keybindManager.wasPressed(KeybindManager.KeybindType.AUTO_SWAP_ARMOR)) {
            // Platform-specific player will be passed by the platform implementation
            // AutoSwapUtil.manualAutoSwapArmor(player);
        }
        
        // Update HUD rendering
        if (hudRenderer.shouldRender()) {
            hudRenderer.render(1.0f); // Platform-specific partial ticks will be passed
        }
    }
    
    public DuraPingConfig getConfig() {
        return config;
    }
    
    public KeybindManager getKeybindManager() {
        return keybindManager;
    }
    
    public HudRenderer getHudRenderer() {
        return hudRenderer;
    }
    
    public SoundManager getSoundManager() {
        return soundManager;
    }
}

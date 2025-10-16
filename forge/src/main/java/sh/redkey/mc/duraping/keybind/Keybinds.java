package sh.redkey.mc.duraping.keybind;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Keybinds implements KeybindManager {
    private KeyMapping toggle;
    private KeyMapping snooze;
    private KeyMapping show;
    private KeyMapping autoSwap;
    private KeyMapping autoSwapMainHand;
    private KeyMapping autoSwapArmor;

    @Override
    public void register() {
        // Forge keybind registration will be handled by the platform-specific entry point
        // This is a placeholder implementation
    }
    
    @Override
    public boolean wasPressed(KeybindType keybind) {
        return switch (keybind) {
            case TOGGLE -> toggle != null && toggle.consumeClick();
            case SNOOZE -> snooze != null && snooze.consumeClick();
            case SHOW -> show != null && show.consumeClick();
            case AUTO_SWAP -> autoSwap != null && autoSwap.consumeClick();
            case AUTO_SWAP_MAIN_HAND -> autoSwapMainHand != null && autoSwapMainHand.consumeClick();
            case AUTO_SWAP_ARMOR -> autoSwapArmor != null && autoSwapArmor.consumeClick();
        };
    }
}

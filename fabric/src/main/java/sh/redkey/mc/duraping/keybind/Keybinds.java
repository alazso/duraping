package sh.redkey.mc.duraping.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Keybinds implements KeybindManager {
    private static final KeyBinding.Category CATEGORY = new KeyBinding.Category(Identifier.of("duraping", "keybinds"));
    
    private KeyBinding toggle;
    private KeyBinding snooze;
    private KeyBinding show;
    private KeyBinding autoSwap;
    private KeyBinding autoSwapMainHand;
    private KeyBinding autoSwapArmor;

    @Override
    public void register() {
        toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.duraping.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_7,
            CATEGORY
        ));
        snooze = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.duraping.snooze",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_8,
            CATEGORY
        ));
        show = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.duraping.show",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_9,
            CATEGORY
        ));
        autoSwap = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.duraping.autoswap",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_0,
            CATEGORY
        ));
        autoSwapMainHand = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.duraping.autoswap_mainhand",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), // No default key
            CATEGORY
        ));
        autoSwapArmor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.duraping.autoswap_armor",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), // No default key
            CATEGORY
        ));
    }
    
    @Override
    public boolean wasPressed(KeybindType keybind) {
        return switch (keybind) {
            case TOGGLE -> toggle.wasPressed();
            case SNOOZE -> snooze.wasPressed();
            case SHOW -> show.wasPressed();
            case AUTO_SWAP -> autoSwap.wasPressed();
            case AUTO_SWAP_MAIN_HAND -> autoSwapMainHand.wasPressed();
            case AUTO_SWAP_ARMOR -> autoSwapArmor.wasPressed();
        };
    }
}


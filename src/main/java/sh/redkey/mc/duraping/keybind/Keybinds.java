package sh.redkey.mc.duraping.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class Keybinds {
    private static final KeyBinding.Category CATEGORY = new KeyBinding.Category(Identifier.of("duraping", "keybinds"));
    
    private static KeyBinding toggle;
    private static KeyBinding snooze;
    private static KeyBinding show;

    public static void register() {
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
    }
    public static void tick(Runnable onToggle, Runnable onSnooze, Runnable onShow) {
        while (toggle.wasPressed()) onToggle.run();
        while (snooze.wasPressed()) onSnooze.run();
        while (show.wasPressed())   onShow.run();
    }
}


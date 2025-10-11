package sh.redkey.mc.duraping.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class Keybinds {
    private static KeyBinding toggle;
    private static KeyBinding snooze;
    private static KeyBinding show;

    public static void register() {
        toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.duraping.toggle", GLFW.GLFW_KEY_KP_7, "category.duraping"));
        snooze = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.duraping.snooze", GLFW.GLFW_KEY_KP_8, "category.duraping"));
        show   = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.duraping.show", GLFW.GLFW_KEY_KP_9, "category.duraping"));
    }
    public static void tick(Runnable onToggle, Runnable onSnooze, Runnable onShow) {
        while (toggle.wasPressed()) onToggle.run();
        while (snooze.wasPressed()) onSnooze.run();
        while (show.wasPressed())   onShow.run();
    }
}


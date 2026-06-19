package sh.redkey.mc.duraping;

//? fabric {
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import org.lwjgl.glfw.GLFW;

public class DuraPingFabric implements ClientModInitializer {
    private static KeyMapping toggleKey;
    private static KeyMapping snoozeKey;
    private static KeyMapping showKey;
    private static KeyMapping autoSwapKey;
    private static KeyMapping autoSwapMainHandKey;
    private static KeyMapping autoSwapArmorKey;

    // FastStats usage metrics (requires Java 25, so 26.x only). Held to keep the reporter alive.
    //? if >=26.1.2 {
    /*@SuppressWarnings("unused")
    private final dev.faststats.fabric.FabricContext fastStats =
            new dev.faststats.fabric.FabricContext.Factory("duraping", "6fc822d7506cfb8bc39e1f0f83a4c854")
                    .metrics(dev.faststats.Metrics.Factory::create)
                    .create();
    *///?}

    @Override
    public void onInitializeClient() {
        DuraPing.init();

        // Register keybindings
        registerKeybinds();

        // Register HUD flash overlay
        //? if >=26.1.2 {
        /*HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("duraping", "flash"), (extractor, deltaTracker) -> {
            float alpha = DuraPing.getFlashAlpha();
            if (alpha <= 0f) return;
            int w = extractor.guiWidth();
            int h = extractor.guiHeight();
            int a = (int)(alpha * 120) << 24;
            extractor.fill(0, 0, w, h, 0x00FFFFFF | a);
        });
        *///?} else {
        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            float alpha = DuraPing.getFlashAlpha();
            if (alpha <= 0f) return;
            
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc == null || mc.getWindow() == null) return;
            int w = mc.getWindow().getGuiScaledWidth();
            int h = mc.getWindow().getGuiScaledHeight();
            int a = (int)(alpha * 120) << 24;
            guiGraphics.fill(0, 0, w, h, 0x00FFFFFF | a);
        });
        //?}

        // Register attack/use callbacks for auto-swap
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            DuraPing.onAttackBlock();
            return InteractionResult.PASS;
        });
        
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            DuraPing.onUseBlock();
            return InteractionResult.PASS;
        });

        // Register client tick handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Handle keybinds first (they should work even when disabled)
            handleKeybinds();
            
            // Then run main tick logic
            DuraPing.onClientTick();
        });
    }

    private void registerKeybinds() {
        // Use the MISC category for now - we can't easily create custom categories in 1.21.9
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.duraping.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_7,
            KeyMapping.Category.MISC
        ));
        snoozeKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.duraping.snooze",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_8,
            KeyMapping.Category.MISC
        ));
        showKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.duraping.show",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_9,
            KeyMapping.Category.MISC
        ));
        autoSwapKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.duraping.autoswap",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_0,
            KeyMapping.Category.MISC
        ));
        autoSwapMainHandKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.duraping.autoswap_mainhand",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KeyMapping.Category.MISC
        ));
        autoSwapArmorKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.duraping.autoswap_armor",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KeyMapping.Category.MISC
        ));
    }

    private void handleKeybinds() {
        while (toggleKey.consumeClick()) {
            DuraPing.onTogglePressed();
        }
        while (snoozeKey.consumeClick()) {
            DuraPing.onSnoozePressed();
        }
        while (showKey.consumeClick()) {
            DuraPing.onShowPressed();
        }
        while (autoSwapKey.consumeClick()) {
            DuraPing.onAutoSwapPressed();
        }
        while (autoSwapMainHandKey.consumeClick()) {
            DuraPing.onAutoSwapMainHandPressed();
        }
        while (autoSwapArmorKey.consumeClick()) {
            DuraPing.onAutoSwapArmorPressed();
        }
    }
}
//?}

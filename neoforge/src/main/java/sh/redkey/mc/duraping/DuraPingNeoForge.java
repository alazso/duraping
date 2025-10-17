package sh.redkey.mc.duraping;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.glfw.GLFW;

@Mod(Constants.MOD_ID)
public class DuraPingNeoForge {
    private static KeyMapping toggleKey;
    private static KeyMapping snoozeKey;
    private static KeyMapping showKey;
    private static KeyMapping autoSwapKey;
    private static KeyMapping autoSwapMainHandKey;
    private static KeyMapping autoSwapArmorKey;

    public DuraPingNeoForge(IEventBus modBus) {
        Constants.LOG.info("Hello NeoForge world!");
        
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::registerKeyMappings);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Sound events are registered automatically by NeoForge
        // No manual registration needed
    }

    private void clientSetup(FMLClientSetupEvent event) {
        DuraPing.init();
        
        // Register game events
        NeoForge.EVENT_BUS.addListener(DuraPingNeoForge::onClientTick);
        NeoForge.EVENT_BUS.addListener(DuraPingNeoForge::onLeftClickBlock);
        NeoForge.EVENT_BUS.addListener(DuraPingNeoForge::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(DuraPingNeoForge::onRenderGuiLayer);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(
            "key.duraping.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_7,
            KeyMapping.Category.MISC
        );
        snoozeKey = new KeyMapping(
            "key.duraping.snooze",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_8,
            KeyMapping.Category.MISC
        );
        showKey = new KeyMapping(
            "key.duraping.show",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_9,
            KeyMapping.Category.MISC
        );
        autoSwapKey = new KeyMapping(
            "key.duraping.autoswap",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_0,
            KeyMapping.Category.MISC
        );
        autoSwapMainHandKey = new KeyMapping(
            "key.duraping.autoswap_mainhand",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KeyMapping.Category.MISC
        );
        autoSwapArmorKey = new KeyMapping(
            "key.duraping.autoswap_armor",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KeyMapping.Category.MISC
        );

        event.register(toggleKey);
        event.register(snoozeKey);
        event.register(showKey);
        event.register(autoSwapKey);
        event.register(autoSwapMainHandKey);
        event.register(autoSwapArmorKey);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        handleKeybinds();
        DuraPing.onClientTick();
    }

    private static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        DuraPing.onAttackBlock();
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        DuraPing.onUseBlock();
    }

    private static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        renderFlashOverlay(event.getGuiGraphics());
    }

    private static void handleKeybinds() {
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

    private static void renderFlashOverlay(GuiGraphics graphics) {
        float alpha = DuraPing.getFlashAlpha();
        if (alpha <= 0) return;
        
        var mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int a = (int)(alpha * 120) << 24;
        
        graphics.fill(0, 0, w, h, 0x00FFFFFF | a);
    }
}

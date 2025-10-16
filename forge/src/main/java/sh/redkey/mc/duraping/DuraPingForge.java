package sh.redkey.mc.duraping;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sh.redkey.mc.duraping.hud.HudFlashOverlay;
import sh.redkey.mc.duraping.keybind.Keybinds;
import sh.redkey.mc.duraping.sound.ModSounds;

@Mod("duraping")
public class DuraPingForge {
    private DuraPingCore core;
    
    public DuraPingForge() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        // Create platform-specific implementations
        Keybinds keybinds = new Keybinds();
        HudFlashOverlay hudRenderer = new HudFlashOverlay();
        ModSounds soundManager = new ModSounds();
        
        // Initialize the core with platform-specific implementations
        this.core = new DuraPingCore(keybinds, hudRenderer, soundManager);
        this.core.init();
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && core != null) {
            this.core.onClientTick();
        }
    }
}

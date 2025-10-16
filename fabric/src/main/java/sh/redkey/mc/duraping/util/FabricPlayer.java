package sh.redkey.mc.duraping.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Fabric implementation of the platform-agnostic Player interface.
 */
public class FabricPlayer implements sh.redkey.mc.duraping.util.Player {
    private final PlayerEntity player;
    
    public FabricPlayer(PlayerEntity player) {
        this.player = player;
    }
    
    @Override
    public String getName() {
        return player.getName().getString();
    }
    
    @Override
    public ItemStack getMainHandItem() {
        return new FabricItemStack(player.getMainHandStack());
    }
    
    @Override
    public ItemStack getOffHandItem() {
        return new FabricItemStack(player.getOffHandStack());
    }
    
    @Override
    public ItemStack getItemInSlot(EquipmentSlot slot) {
        return new FabricItemStack(player.getEquippedStack(slot));
    }
    
    @Override
    public void setItemInSlot(EquipmentSlot slot, ItemStack item) {
        if (item instanceof FabricItemStack fabricItem) {
            player.equipStack(slot, fabricItem.getFabricStack());
        }
    }
    
    @Override
    public int getInventorySize() {
        return player.getInventory().size();
    }
    
    @Override
    public ItemStack getInventoryItem(int slot) {
        return new FabricItemStack(player.getInventory().getStack(slot));
    }
    
    @Override
    public void setInventoryItem(int slot, ItemStack item) {
        if (item instanceof FabricItemStack fabricItem) {
            player.getInventory().setStack(slot, fabricItem.getFabricStack());
        }
    }
    
    @Override
    public boolean isCreative() {
        return player.isCreative();
    }
    
    @Override
    public boolean isSurvival() {
        return !player.isCreative() && !player.isSpectator();
    }
    
    /**
     * Get the underlying Fabric PlayerEntity.
     * @return the Fabric PlayerEntity
     */
    public PlayerEntity getFabricPlayer() {
        return player;
    }
    
    /**
     * Convert Fabric EquipmentSlot to common EquipmentSlot.
     */
    public static EquipmentSlot convertSlot(EquipmentSlot fabricSlot) {
        return switch (fabricSlot) {
            case MAINHAND -> EquipmentSlot.MAINHAND;
            case OFFHAND -> EquipmentSlot.OFFHAND;
            case HEAD -> EquipmentSlot.HEAD;
            case CHEST -> EquipmentSlot.CHEST;
            case LEGS -> EquipmentSlot.LEGS;
            case FEET -> EquipmentSlot.FEET;
        };
    }
    
    /**
     * Convert common EquipmentSlot to Fabric EquipmentSlot.
     */
    public static EquipmentSlot convertToFabricSlot(EquipmentSlot commonSlot) {
        return switch (commonSlot) {
            case MAINHAND -> EquipmentSlot.MAINHAND;
            case OFFHAND -> EquipmentSlot.OFFHAND;
            case HEAD -> EquipmentSlot.HEAD;
            case CHEST -> EquipmentSlot.CHEST;
            case LEGS -> EquipmentSlot.LEGS;
            case FEET -> EquipmentSlot.FEET;
        };
    }
}

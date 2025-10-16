package sh.redkey.mc.duraping.util;

/**
 * Platform-agnostic representation of a player.
 * Platform-specific implementations should implement this interface.
 */
public interface Player {
    /**
     * Get the player's name.
     * @return player name
     */
    String getName();
    
    /**
     * Get the item in the player's main hand.
     * @return main hand item
     */
    ItemStack getMainHandItem();
    
    /**
     * Get the item in the player's off hand.
     * @return off hand item
     */
    ItemStack getOffHandItem();
    
    /**
     * Get the item in the specified equipment slot.
     * @param slot equipment slot
     * @return item in slot
     */
    ItemStack getItemInSlot(EquipmentSlot slot);
    
    /**
     * Set the item in the specified equipment slot.
     * @param slot equipment slot
     * @param item item to set
     */
    void setItemInSlot(EquipmentSlot slot, ItemStack item);
    
    /**
     * Get the player's inventory size.
     * @return inventory size
     */
    int getInventorySize();
    
    /**
     * Get an item from the player's inventory.
     * @param slot inventory slot
     * @return item in slot
     */
    ItemStack getInventoryItem(int slot);
    
    /**
     * Set an item in the player's inventory.
     * @param slot inventory slot
     * @param item item to set
     */
    void setInventoryItem(int slot, ItemStack item);
    
    /**
     * Check if the player is in creative mode.
     * @return true if creative
     */
    boolean isCreative();
    
    /**
     * Check if the player is in survival mode.
     * @return true if survival
     */
    boolean isSurvival();
    
    /**
     * Equipment slot enumeration.
     */
    enum EquipmentSlot {
        MAINHAND,
        OFFHAND,
        HEAD,
        CHEST,
        LEGS,
        FEET
    }
}

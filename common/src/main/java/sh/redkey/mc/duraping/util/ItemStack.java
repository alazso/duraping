package sh.redkey.mc.duraping.util;

/**
 * Platform-agnostic representation of an item stack.
 * Platform-specific implementations should implement this interface.
 */
public interface ItemStack {
    /**
     * Get the durability of this item stack.
     * @return current durability
     */
    int getDurability();
    
    /**
     * Get the maximum durability of this item stack.
     * @return maximum durability
     */
    int getMaxDurability();
    
    /**
     * Get the durability percentage (0.0 to 1.0).
     * @return durability percentage
     */
    default double getDurabilityPercentage() {
        int max = getMaxDurability();
        if (max <= 0) return 1.0;
        return (double) getDurability() / max;
    }
    
    /**
     * Check if this item stack is damaged.
     * @return true if damaged
     */
    default boolean isDamaged() {
        return getDurability() < getMaxDurability();
    }
    
    /**
     * Get the item identifier for this stack.
     * @return item identifier
     */
    String getItemId();
    
    /**
     * Get the display name of this item stack.
     * @return display name
     */
    String getDisplayName();
    
    /**
     * Check if this item stack is empty.
     * @return true if empty
     */
    boolean isEmpty();
    
    /**
     * Get the stack size.
     * @return stack size
     */
    int getCount();
}

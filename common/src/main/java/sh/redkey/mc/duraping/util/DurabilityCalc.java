package sh.redkey.mc.duraping.util;

/**
 * Platform-agnostic durability calculation utilities.
 */
public class DurabilityCalc {
    /**
     * Calculate the effective uses left for an item stack.
     * This is a simplified calculation that doesn't factor in enchantments.
     * Platform-specific implementations should override this for more accurate calculations.
     * 
     * @param stack the item stack
     * @return effective uses left
     */
    public static int effectiveUsesLeft(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        
        // Expected value factoring Unbreaking: EV ≈ remaining / (1 - p(no-damage))
        // For Unbreaking L on tools (not armor): p(no-damage)=L/(L+1)
        // Keep simple MVP; implement later.
        return Math.max(0, stack.getMaxDurability() - stack.getDurability());
    }
    
    /**
     * Calculate the durability percentage of an item stack.
     * 
     * @param stack the item stack
     * @return durability percentage (0.0 to 1.0)
     */
    public static double getDurabilityPercentage(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 1.0;
        }
        return stack.getDurabilityPercentage();
    }
}


package sh.redkey.mc.duraping.util;

import sh.redkey.mc.duraping.config.DuraPingConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Platform-agnostic auto-swap utilities.
 * Platform-specific implementations should extend this class.
 */
public class AutoSwapUtil {
    // Cooldown to prevent rapid swapping
    private static final Map<Integer, Long> lastSwapTime = new HashMap<>();
    private static final Map<String, Long> lastArmorSwapTime = new HashMap<>();
    private static final long SWAP_COOLDOWN_MS = 3000; // 3 second cooldown
    
    /**
     * Check and auto-swap armor pieces if below threshold
     * Called on tick (every 0.5 seconds) since armor doesn't trigger attack/use events
     */
    public static void checkAndSwapArmor(Player player) {
        if (player == null) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapArmor) return;
        
        // Check each armor slot
        for (Player.EquipmentSlot slot : new Player.EquipmentSlot[]{
            Player.EquipmentSlot.HEAD, 
            Player.EquipmentSlot.CHEST, 
            Player.EquipmentSlot.LEGS, 
            Player.EquipmentSlot.FEET
        }) {
            ItemStack currentArmor = player.getItemInSlot(slot);
            if (currentArmor == null || currentArmor.isEmpty()) continue;
            
            double durabilityPercent = currentArmor.getDurabilityPercentage() * 100.0;
            
            // Check threshold
            if (durabilityPercent <= cfg.autoSwapThreshold) {
                System.out.println("[DuraPing] Armor " + slot + " below threshold: " + 
                                  currentArmor.getDisplayName() + " at " + (int)durabilityPercent + "%");
                attemptAutoSwap(player, slot);
            }
        }
    }
    
    /**
     * Check and auto-swap tools if below threshold
     * Called when player uses a tool (mining, attacking, etc.)
     */
    public static void checkAndSwapTool(Player player) {
        if (player == null) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapTools) return;
        
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand == null || mainHand.isEmpty()) return;
        
        double durabilityPercent = mainHand.getDurabilityPercentage() * 100.0;
        
        // Check threshold
        if (durabilityPercent <= cfg.autoSwapThreshold) {
            System.out.println("[DuraPing] Tool below threshold: " + 
                              mainHand.getDisplayName() + " at " + (int)durabilityPercent + "%");
            attemptAutoSwap(player, Player.EquipmentSlot.MAINHAND);
        }
    }
    
    /**
     * Attempt to auto-swap an item in the specified slot
     */
    public static boolean attemptAutoSwap(Player player, Player.EquipmentSlot slot) {
        if (player == null) return false;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) return false;
        
        // Check cooldown
        String slotKey = slot.toString();
        long currentTime = System.currentTimeMillis();
        
        if (slot == Player.EquipmentSlot.MAINHAND) {
            if (lastSwapTime.containsKey(0) && currentTime - lastSwapTime.get(0) < SWAP_COOLDOWN_MS) {
                return false;
            }
        } else {
            if (lastArmorSwapTime.containsKey(slotKey) && 
                currentTime - lastArmorSwapTime.get(slotKey) < SWAP_COOLDOWN_MS) {
                return false;
            }
        }
        
        ItemStack currentItem = player.getItemInSlot(slot);
        if (currentItem == null || currentItem.isEmpty()) return false;
        
        // Find best replacement
        ItemStack replacement = findBestReplacement(player, currentItem, slot, cfg);
        if (replacement == null || replacement.isEmpty()) return false;
        
        // Perform the swap
        boolean success = performSwap(player, slot, currentItem, replacement);
        
        if (success) {
            // Update cooldown
            if (slot == Player.EquipmentSlot.MAINHAND) {
                lastSwapTime.put(0, currentTime);
            } else {
                lastArmorSwapTime.put(slotKey, currentTime);
            }
        }
        
        return success;
    }
    
    /**
     * Find the best replacement item for the given slot
     */
    private static ItemStack findBestReplacement(Player player, ItemStack currentItem, 
                                               Player.EquipmentSlot slot, DuraPingConfig cfg) {
        ItemStack bestReplacement = null;
        double bestDurability = 0.0;
        
        // Search through inventory
        for (int i = 0; i < player.getInventorySize(); i++) {
            ItemStack candidate = player.getInventoryItem(i);
            if (candidate == null || candidate.isEmpty()) continue;
            
            // Check if item is suitable for this slot
            if (!isItemSuitableForSlot(candidate, slot)) continue;
            
            // Check if it's better than current
            if (candidate.getDurabilityPercentage() > bestDurability) {
                // If we don't allow lower quality, skip if it's worse than current
                if (!cfg.autoSwapAllowLowerQuality && 
                    candidate.getDurabilityPercentage() < currentItem.getDurabilityPercentage()) {
                    continue;
                }
                
                bestReplacement = candidate;
                bestDurability = candidate.getDurabilityPercentage();
            }
        }
        
        return bestReplacement;
    }
    
    /**
     * Check if an item is suitable for the given equipment slot
     */
    private static boolean isItemSuitableForSlot(ItemStack stack, Player.EquipmentSlot slot) {
        // This is a simplified check - platform-specific implementations should override
        // For now, just check if the item has durability
        return stack.getMaxDurability() > 0;
    }
    
    /**
     * Perform the actual item swap
     */
    private static boolean performSwap(Player player, Player.EquipmentSlot slot, 
                                     ItemStack currentItem, ItemStack replacement) {
        // This is a simplified implementation - platform-specific implementations should override
        // For now, just swap the items
        player.setItemInSlot(slot, replacement);
        
        // Find the replacement in inventory and replace it with current item
        for (int i = 0; i < player.getInventorySize(); i++) {
            ItemStack invItem = player.getInventoryItem(i);
            if (invItem != null && invItem.equals(replacement)) {
                player.setInventoryItem(i, currentItem);
                break;
            }
        }
        
        return true;
    }
    
    /**
     * Manual auto-swap for all equipment
     */
    public static void manualAutoSwap(Player player) {
        if (player == null) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) return;
        
        // Check main hand
        if (cfg.autoSwapTools) {
            attemptAutoSwap(player, Player.EquipmentSlot.MAINHAND);
        }
        
        // Check armor
        if (cfg.autoSwapArmor) {
            for (Player.EquipmentSlot slot : new Player.EquipmentSlot[]{
                Player.EquipmentSlot.HEAD, 
                Player.EquipmentSlot.CHEST, 
                Player.EquipmentSlot.LEGS, 
                Player.EquipmentSlot.FEET
            }) {
                attemptAutoSwap(player, slot);
            }
        }
    }
    
    /**
     * Manual auto-swap for main hand only
     */
    public static void manualAutoSwapMainHand(Player player) {
        if (player == null) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapTools) return;
        
        attemptAutoSwap(player, Player.EquipmentSlot.MAINHAND);
    }
    
    /**
     * Manual auto-swap for armor only
     */
    public static void manualAutoSwapArmor(Player player) {
        if (player == null) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapArmor) return;
        
        for (Player.EquipmentSlot slot : new Player.EquipmentSlot[]{
            Player.EquipmentSlot.HEAD, 
            Player.EquipmentSlot.CHEST, 
            Player.EquipmentSlot.LEGS, 
            Player.EquipmentSlot.FEET
        }) {
            attemptAutoSwap(player, slot);
        }
    }
}
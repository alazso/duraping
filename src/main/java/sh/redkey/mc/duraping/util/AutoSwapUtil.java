package sh.redkey.mc.duraping.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sh.redkey.mc.duraping.config.DuraPingConfig;
import sh.redkey.mc.duraping.DuraPingClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoSwapUtil {
    // Cooldown to prevent rapid swapping
    private static final Map<Integer, Long> lastSwapTime = new HashMap<>();
    private static final Map<String, Long> lastArmorSwapTime = new HashMap<>();
    private static final long SWAP_COOLDOWN_MS = 3000; // 3 second cooldown
    
    /**
     * Check and auto-swap armor pieces if below threshold
     * Called on tick (every 0.5 seconds) since armor doesn't trigger attack/use events
     */
    public static void checkAndSwapArmor(PlayerEntity player) {
        if (player == null || player.isSpectator()) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapArmor) return;
        
        // Check each armor slot
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack currentArmor = player.getEquippedStack(slot);
            if (currentArmor.isEmpty() || !currentArmor.isDamageable()) continue;
            
            int currentDurability = currentArmor.getMaxDamage() - currentArmor.getDamage();
            int durabilityPercent = (int) Math.floor((currentDurability * 100.0) / currentArmor.getMaxDamage());
            
            // Check threshold
            if (durabilityPercent <= cfg.autoSwapThreshold) {
                System.out.println("[DuraPing] Armor " + slot.getName() + " below threshold: " + 
                                  currentArmor.getName().getString() + " at " + durabilityPercent + "%");
                attemptAutoSwap(player, slot);
            }
        }
    }
    
    /**
     * Simple slot-based auto-swap (inspired by Low-Durability-Switcher)
     * Just changes the selected hotbar slot instead of complex inventory manipulation
     */
    public static void checkAndSwapTool(PlayerEntity player) {
        if (player == null || player.isSpectator()) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapTools) return;
        
        ItemStack currentItem = player.getInventory().getSelectedStack();
        if (currentItem.isEmpty() || !currentItem.isDamageable()) return;
        
        int currentDurability = currentItem.getMaxDamage() - currentItem.getDamage();
        int durabilityPercent = (int) Math.floor((currentDurability * 100.0) / currentItem.getMaxDamage());
        
        // Check threshold
        if (durabilityPercent > cfg.autoSwapThreshold) return;
        
        // Check cooldown
        int currentSlot = player.getInventory().getSelectedSlot();
        long currentTime = System.currentTimeMillis();
        if (lastSwapTime.containsKey(currentSlot)) {
            if (currentTime - lastSwapTime.get(currentSlot) < SWAP_COOLDOWN_MS) {
                return; // Still on cooldown
            }
        }
        
        // Find next suitable slot
        int newSlot = findNextSuitableSlot(player, currentSlot, currentItem);
        if (newSlot != -1) {
            // Only change selected slot if it's different (Phase 1: hotbar switch)
            // Phase 2 returns currentSlot, so item is already in hand
            if (newSlot != currentSlot) {
                player.getInventory().setSelectedSlot(newSlot);
            }
            lastSwapTime.put(currentSlot, currentTime);
            
            player.sendMessage(
                Text.literal("§6[DuraPing] Auto-swapped to slot " + (newSlot + 1))
                    .formatted(Formatting.GOLD), 
                true
            );
            player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            
            System.out.println("[DuraPing] Auto-swapped from slot " + (currentSlot + 1) + " to slot " + (newSlot + 1));
        }
    }
    
    /**
     * Find the next slot with a similar item that has more durability
     * First checks hotbar, then main inventory
     */
    private static int findNextSuitableSlot(PlayerEntity player, int currentSlot, ItemStack currentItem) {
        int bestSlot = -1;
        int bestDurability = currentItem.getMaxDamage() - currentItem.getDamage();
        
        // PHASE 1: Search through hotbar for better items of the same type (simple slot switch)
        for (int i = 0; i < 9; i++) {
            if (i == currentSlot) continue; // Skip current slot
            
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isDamageable()) continue;
            
            // Check if same item type
            if (stack.getItem() != currentItem.getItem()) continue;
            
            int slotDurability = stack.getMaxDamage() - stack.getDamage();
            
            // Find item with significantly more durability
            int minRequiredDurability = bestDurability + (currentItem.getMaxDamage() / 10);
            if (slotDurability >= minRequiredDurability) {
                bestDurability = slotDurability;
                bestSlot = i;
            }
        }
        
        // If found in hotbar, return it (simple slot switch)
        if (bestSlot != -1) {
            System.out.println("[DuraPing] Found better item in hotbar slot " + (bestSlot + 1));
            return bestSlot;
        }
        
        // PHASE 2: Search main inventory (slots 9-35) for better items
        // If found, we'll move it to current slot
        int bestInventorySlot = -1;
        int bestInventoryDurability = currentItem.getMaxDamage() - currentItem.getDamage();
        
        for (int i = 9; i < 36; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isDamageable()) continue;
            
            // Check if same item type
            if (stack.getItem() != currentItem.getItem()) continue;
            
            int slotDurability = stack.getMaxDamage() - stack.getDamage();
            
            // Find item with significantly more durability
            int minRequiredDurability = bestInventoryDurability + (currentItem.getMaxDamage() / 10);
            if (slotDurability >= minRequiredDurability) {
                bestInventoryDurability = slotDurability;
                bestInventorySlot = i;
            }
        }
        
        // If found in main inventory, swap it into the current hotbar slot using server-synced method
        if (bestInventorySlot != -1) {
            System.out.println("[DuraPing] Found better item in inventory slot " + bestInventorySlot + ", swapping with server sync");
            
            // Use the Minecraft client's interaction manager to perform server-synchronized swap
            // This prevents client-server desync issues!
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.interactionManager != null && client.player != null) {
                // Hotbar slots in the player inventory menu are offset by 36
                int serverHotbarSlot = currentSlot + 36;
                
                // Perform server-synchronized SWAP click
                // This sends a packet to the server and waits for confirmation
                client.interactionManager.clickSlot(
                    client.player.currentScreenHandler.syncId,
                    bestInventorySlot,
                    currentSlot,
                    SlotActionType.SWAP,
                    client.player
                );
            }
            
            // Return current slot (swap operation will place item there)
            return currentSlot;
        }
        
        return -1; // No suitable replacement found
    }
    
    public static boolean attemptAutoSwap(PlayerEntity player, EquipmentSlot slot) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) return false;
        
        // Check if auto-swap is enabled for this slot type
        if (!isAutoSwapEnabledForSlot(slot, cfg)) return false;
        
        ItemStack currentItem = player.getEquippedStack(slot);
        if (currentItem.isEmpty() || !currentItem.isDamageable()) return false;
        
        // Check cooldown to prevent rapid swapping
        String itemId = currentItem.getItem().toString() + "_" + currentItem.getMaxDamage();
        String cooldownKey = slot.getName() + "_" + player.getUuidAsString() + "_" + itemId;
        long currentTime = System.currentTimeMillis();
        if (lastArmorSwapTime.containsKey(cooldownKey)) {
            if (currentTime - lastArmorSwapTime.get(cooldownKey) < SWAP_COOLDOWN_MS) {
                return false; // Still on cooldown
            }
        }
        
        // Check if current item is below threshold
        int maxDurability = currentItem.getMaxDamage();
        int currentDurability = maxDurability - currentItem.getDamage();
        int durabilityPercent = (int) Math.floor((currentDurability * 100.0) / maxDurability);
        
        if (durabilityPercent > cfg.autoSwapThreshold) return false;
        
        // Find best replacement item
        ItemStack replacement = findBestReplacement(player, currentItem, slot, cfg);
        if (replacement.isEmpty()) {
            System.out.println("[DuraPing] No suitable replacement found for " + currentItem.getName().getString());
            return false;
        }
        
        System.out.println("[DuraPing] Found replacement: " + replacement.getName().getString() + 
                          " (" + (replacement.getMaxDamage() - replacement.getDamage()) + "/" + replacement.getMaxDamage() + ")");
        
        // Perform the swap
        return performSwap(player, slot, currentItem, replacement);
    }
    
    private static boolean isAutoSwapEnabledForSlot(EquipmentSlot slot, DuraPingConfig cfg) {
        return switch (slot) {
            case MAINHAND -> cfg.autoSwapTools;
            case HEAD, CHEST, LEGS, FEET -> cfg.autoSwapArmor;
            default -> false;
        };
    }
    
    private static ItemStack findBestReplacement(PlayerEntity player, ItemStack currentItem, EquipmentSlot slot, DuraPingConfig cfg) {
        List<ItemStack> candidates = new ArrayList<>();
        
        // Search inventory for suitable replacements
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isDamageable()) continue;
            
            // Check if item is suitable for this slot
            if (!isItemSuitableForSlot(stack, slot)) continue;
            
            // Check quality compatibility
            if (!cfg.autoSwapAllowLowerQuality && isLowerQuality(currentItem, stack)) continue;
            
            // Check if replacement has more durability
            int currentDurability = currentItem.getMaxDamage() - currentItem.getDamage();
            int replacementDurability = stack.getMaxDamage() - stack.getDamage();
            
            // Only consider replacements that are significantly better (at least 10% more durability)
            int minRequiredDurability = currentDurability + (currentItem.getMaxDamage() / 10);
            
            if (replacementDurability >= minRequiredDurability) {
                System.out.println("[DuraPing] Candidate found at slot " + i + ": " + stack.getName().getString() + 
                                 " (" + replacementDurability + "/" + stack.getMaxDamage() + ")");
                candidates.add(stack);
            }
        }
        
        if (candidates.isEmpty()) return ItemStack.EMPTY;
        
        // Sort by durability (highest first)
        candidates.sort(Comparator.comparingInt((ItemStack stack) -> 
            stack.getMaxDamage() - stack.getDamage()
        ).reversed());
        
        return candidates.get(0);
    }
    
    private static boolean isItemSuitableForSlot(ItemStack stack, EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> true; // Most items can be held in main hand
            case OFFHAND -> true; // Most items can go in offhand
            case HEAD -> stack.isIn(net.minecraft.registry.tag.ItemTags.HEAD_ARMOR);
            case CHEST -> stack.isIn(net.minecraft.registry.tag.ItemTags.CHEST_ARMOR);
            case LEGS -> stack.isIn(net.minecraft.registry.tag.ItemTags.LEG_ARMOR);
            case FEET -> stack.isIn(net.minecraft.registry.tag.ItemTags.FOOT_ARMOR);
            default -> false;
        };
    }
    
    private static boolean isLowerQuality(ItemStack current, ItemStack candidate) {
        // Simple quality comparison based on max durability
        // Higher max durability generally indicates higher quality
        return candidate.getMaxDamage() < current.getMaxDamage();
    }
    
    private static boolean performSwap(PlayerEntity player, EquipmentSlot slot, ItemStack currentItem, ItemStack replacement) {
        try {
            // Find the replacement item in inventory
            int replacementSlot = -1;
            for (int i = 0; i < player.getInventory().size(); i++) {
                if (player.getInventory().getStack(i) == replacement) {
                    replacementSlot = i;
                    break;
                }
            }
            
            if (replacementSlot == -1) return false;
            
            // CRITICAL: Copy the items BEFORE any modifications
            // Get the actual equipped item fresh to ensure we have the right data
            ItemStack actualCurrentItem = player.getEquippedStack(slot);
            ItemStack tempCurrent = actualCurrentItem.copy();
            ItemStack tempReplacement = replacement.copy();
            
            System.out.println("[DuraPing] Swapping: " + tempCurrent.getName().getString() + 
                             " (" + (tempCurrent.getMaxDamage() - tempCurrent.getDamage()) + "/" + tempCurrent.getMaxDamage() + ")" +
                             " with " + tempReplacement.getName().getString() + 
                             " (" + (tempReplacement.getMaxDamage() - tempReplacement.getDamage()) + "/" + tempReplacement.getMaxDamage() + ")");
            
            // Perform the actual swap using Minecraft's inventory operations
            // First, put the current equipped item into the replacement's inventory slot
            player.getInventory().setStack(replacementSlot, tempCurrent);
            // Then equip the replacement item
            player.equipStack(slot, tempReplacement);
            
            // Send feedback to player
            String message = String.format("Auto-swapped %s (%.0f%% durability) with %s (%.0f%% durability)", 
                tempCurrent.getName().getString(),
                ((double)(tempCurrent.getMaxDamage() - tempCurrent.getDamage()) / tempCurrent.getMaxDamage()) * 100,
                tempReplacement.getName().getString(),
                ((double)(tempReplacement.getMaxDamage() - tempReplacement.getDamage()) / tempReplacement.getMaxDamage()) * 100
            );
            
            DuraPingClient.toast("Auto-swap: " + tempReplacement.getName().getString());
            // Send message to player (client-side only)
            player.sendMessage(Text.literal("§6[DuraPing] " + message), false);
            
            // Update cooldown
            String itemId = currentItem.getItem().toString() + "_" + currentItem.getMaxDamage();
            String cooldownKey = slot.getName() + "_" + player.getUuidAsString() + "_" + itemId;
            lastArmorSwapTime.put(cooldownKey, System.currentTimeMillis());
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void manualAutoSwap(PlayerEntity player) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) {
            DuraPingClient.toast("Auto-swap is disabled");
            return;
        }
        
        boolean swapped = false;
        
        // Try to swap main hand
        if (cfg.autoSwapTools && attemptAutoSwap(player, EquipmentSlot.MAINHAND)) {
            swapped = true;
        }
        
        // Try to swap armor
        if (cfg.autoSwapArmor) {
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                if (attemptAutoSwap(player, slot)) {
                    swapped = true;
                }
            }
        }
        
        if (!swapped) {
            DuraPingClient.toast("No suitable replacements found");
        }
    }
    
    public static void manualAutoSwapMainHand(PlayerEntity player) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) {
            DuraPingClient.toast("Auto-swap is disabled");
            return;
        }
        
        if (cfg.autoSwapTools) {
            if (attemptAutoSwap(player, EquipmentSlot.MAINHAND)) {
                DuraPingClient.toast("Main hand auto-swapped");
            } else {
                DuraPingClient.toast("No suitable main hand replacement found");
            }
        } else {
            DuraPingClient.toast("Main hand auto-swap is disabled");
        }
    }
    
    public static void manualAutoSwapArmor(PlayerEntity player) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) {
            DuraPingClient.toast("Auto-swap is disabled");
            return;
        }
        
        if (cfg.autoSwapArmor) {
            boolean swapped = false;
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                if (attemptAutoSwap(player, slot)) {
                    swapped = true;
                }
            }
            
            if (swapped) {
                DuraPingClient.toast("Armor auto-swapped");
            } else {
                DuraPingClient.toast("No suitable armor replacement found");
            }
        } else {
            DuraPingClient.toast("Armor auto-swap is disabled");
        }
    }
}

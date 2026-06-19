package sh.redkey.mc.duraping.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import sh.redkey.mc.duraping.Constants;
import sh.redkey.mc.duraping.DuraPing;
import sh.redkey.mc.duraping.config.DuraPingConfig;

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
    public static void checkAndSwapArmor(Player player) {
        if (player == null || player.isSpectator()) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapArmor) return;
        
        // Check each armor slot
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack currentArmor = player.getItemBySlot(slot);
            if (currentArmor.isEmpty() || !currentArmor.isDamageableItem()) continue;
            
            int currentDurability = currentArmor.getMaxDamage() - currentArmor.getDamageValue();
            int durabilityPercent = (int) Math.floor((currentDurability * 100.0) / currentArmor.getMaxDamage());
            
            // Check threshold
            if (durabilityPercent <= cfg.autoSwapThreshold) {
                Constants.LOG.debug("Armor {} below threshold: {} at {}%",
                        slot.getName(), currentArmor.getHoverName().getString(), durabilityPercent);
                attemptAutoSwap(player, slot);
            }
        }
    }
    
    /**
     * Simple slot-based auto-swap (inspired by Low-Durability-Switcher)
     * Just changes the selected hotbar slot instead of complex inventory manipulation
     */
    public static void checkAndSwapTool(Player player) {
        if (player == null || player.isSpectator()) return;
        
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled || !cfg.autoSwapTools) return;
        
        // Use the mainhand item instead of getSelected() for cross-platform compatibility
        ItemStack currentItem = player.getMainHandItem();
        if (currentItem.isEmpty() || !currentItem.isDamageableItem()) return;
        
        int currentDurability = currentItem.getMaxDamage() - currentItem.getDamageValue();
        int durabilityPercent = (int) Math.floor((currentDurability * 100.0) / currentItem.getMaxDamage());
        
        // Check threshold
        if (durabilityPercent > cfg.autoSwapThreshold) return;
        
        // Check cooldown - get the currently selected slot
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
            // For cross-platform compatibility, use platform service to switch slots
            // We can't directly access inventory.selected field, so we'll use a workaround
            // by swapping items via click simulation
            switchHotbarSlot(player, newSlot);
            lastSwapTime.put(currentSlot, currentTime);
            
            DuraPing.sendActionBar(player,
                Component.literal("§6[DuraPing] Auto-swapped to slot " + (newSlot + 1)));
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            
            Constants.LOG.debug("Auto-swapped from slot {} to slot {}", currentSlot + 1, newSlot + 1);
        }
    }
    
    /**
     * Switch the held hotbar slot, and tell the server so it stops damaging the old item.
     */
    private static void switchHotbarSlot(Player player, int slot) {
        player.getInventory().setSelectedSlot(slot);
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.send(new ServerboundSetCarriedItemPacket(slot));
        }
    }
    
    /**
     * Find the next slot with a similar item that has more durability
     * First checks hotbar, then main inventory
     */
    private static int findNextSuitableSlot(Player player, int currentSlot, ItemStack currentItem) {
        int bestSlot = -1;
        int bestDurability = currentItem.getMaxDamage() - currentItem.getDamageValue();
        
        // PHASE 1: Search through hotbar for better items of the same type (simple slot switch)
        for (int i = 0; i < 9; i++) {
            if (i == currentSlot) continue; // Skip current slot
            
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !stack.isDamageableItem()) continue;
            
            // Check if same item type
            if (stack.getItem() != currentItem.getItem()) continue;
            
            int slotDurability = stack.getMaxDamage() - stack.getDamageValue();
            
            // Find item with significantly more durability
            int minRequiredDurability = bestDurability + (currentItem.getMaxDamage() / 10);
            if (slotDurability >= minRequiredDurability) {
                bestDurability = slotDurability;
                bestSlot = i;
            }
        }
        
        // If found in hotbar, return it (simple slot switch)
        if (bestSlot != -1) {
            Constants.LOG.debug("Found better item in hotbar slot {}", bestSlot + 1);
            return bestSlot;
        }
        
        // PHASE 2: Search main inventory (slots 9-35) for better items
        // If found, we'll move it to current slot
        int bestInventorySlot = -1;
        int bestInventoryDurability = currentItem.getMaxDamage() - currentItem.getDamageValue();
        
        for (int i = 9; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !stack.isDamageableItem()) continue;
            
            // Check if same item type
            if (stack.getItem() != currentItem.getItem()) continue;
            
            int slotDurability = stack.getMaxDamage() - stack.getDamageValue();
            
            // Find item with significantly more durability
            int minRequiredDurability = bestInventoryDurability + (currentItem.getMaxDamage() / 10);
            if (slotDurability >= minRequiredDurability) {
                bestInventoryDurability = slotDurability;
                bestInventorySlot = i;
            }
        }
        
        // If found in main inventory, swap it into the current hotbar slot with a
        // server-synced container click.
        if (bestInventorySlot != -1) {
            if (!canManipulateInventory(player)) return -1;
            Constants.LOG.debug("Found better item in inventory slot {}, swapping with server sync", bestInventorySlot);

            Minecraft client = Minecraft.getInstance();
            // SWAP moves the main-inventory item (menu slot 9-35) into hotbar slot currentSlot.
            client.gameMode.handleInventoryMouseClick(
                player.inventoryMenu.containerId,
                bestInventorySlot,
                currentSlot,
                ClickType.SWAP,
                player
            );

            // The better item now occupies the current hotbar slot.
            return currentSlot;
        }

        return -1; // No suitable replacement found
    }
    
    public static boolean attemptAutoSwap(Player player, EquipmentSlot slot) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) return false;
        
        // Check if auto-swap is enabled for this slot type
        if (!isAutoSwapEnabledForSlot(slot, cfg)) return false;
        
        ItemStack currentItem = player.getItemBySlot(slot);
        if (currentItem.isEmpty() || !currentItem.isDamageableItem()) return false;
        
        // Check cooldown to prevent rapid swapping
        String itemId = currentItem.getItem().toString() + "_" + currentItem.getMaxDamage();
        String cooldownKey = slot.getName() + "_" + player.getStringUUID() + "_" + itemId;
        long currentTime = System.currentTimeMillis();
        if (lastArmorSwapTime.containsKey(cooldownKey)) {
            if (currentTime - lastArmorSwapTime.get(cooldownKey) < SWAP_COOLDOWN_MS) {
                return false; // Still on cooldown
            }
        }
        
        // Check if current item is below threshold
        int maxDurability = currentItem.getMaxDamage();
        int currentDurability = maxDurability - currentItem.getDamageValue();
        int durabilityPercent = (int) Math.floor((currentDurability * 100.0) / maxDurability);
        
        if (durabilityPercent > cfg.autoSwapThreshold) return false;
        
        // Find best replacement item
        ItemStack replacement = findBestReplacement(player, currentItem, slot, cfg);
        if (replacement.isEmpty()) {
            Constants.LOG.debug("No suitable replacement found for {}", currentItem.getHoverName().getString());
            return false;
        }
        
        Constants.LOG.debug("Found replacement: {} ({}/{})", replacement.getHoverName().getString(),
                replacement.getMaxDamage() - replacement.getDamageValue(), replacement.getMaxDamage());
        
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
    
    private static ItemStack findBestReplacement(Player player, ItemStack currentItem, EquipmentSlot slot, DuraPingConfig cfg) {
        List<ItemStack> candidates = new ArrayList<>();
        
        // Search the hotbar and main inventory (raw indices 0-35) for suitable replacements.
        for (int i = 0; i <= 35; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !stack.isDamageableItem()) continue;
            
            // Check if item is suitable for this slot
            if (!isItemSuitableForSlot(stack, slot)) continue;

            // Hand slots only swap to the same item type (a pickaxe for a pickaxe),
            // matching the tick-based tool path. Armor still allows any matching piece.
            if ((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
                    && stack.getItem() != currentItem.getItem()) continue;

            // Check quality compatibility
            if (!cfg.autoSwapAllowLowerQuality && isLowerQuality(currentItem, stack)) continue;
            
            // Check if replacement has more durability
            int currentDurability = currentItem.getMaxDamage() - currentItem.getDamageValue();
            int replacementDurability = stack.getMaxDamage() - stack.getDamageValue();
            
            // Only consider replacements that are significantly better (at least 10% more durability)
            int minRequiredDurability = currentDurability + (currentItem.getMaxDamage() / 10);
            
            if (replacementDurability >= minRequiredDurability) {
                Constants.LOG.debug("Candidate found at slot {}: {} ({}/{})", i, stack.getHoverName().getString(),
                        replacementDurability, stack.getMaxDamage());
                candidates.add(stack);
            }
        }
        
        if (candidates.isEmpty()) return ItemStack.EMPTY;
        
        // Sort by durability (highest first)
        candidates.sort(Comparator.comparingInt((ItemStack stack) -> 
            stack.getMaxDamage() - stack.getDamageValue()
        ).reversed());
        
        return candidates.get(0);
    }
    
    private static boolean isItemSuitableForSlot(ItemStack stack, EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> true; // Most items can be held in main hand
            case OFFHAND -> true; // Most items can go in offhand
            case HEAD -> stack.is(ItemTags.HEAD_ARMOR);
            case CHEST -> stack.is(ItemTags.CHEST_ARMOR);
            case LEGS -> stack.is(ItemTags.LEG_ARMOR);
            case FEET -> stack.is(ItemTags.FOOT_ARMOR);
            default -> false;
        };
    }
    
    private static boolean isLowerQuality(ItemStack current, ItemStack candidate) {
        // Simple quality comparison based on max durability
        // Higher max durability generally indicates higher quality
        return candidate.getMaxDamage() < current.getMaxDamage();
    }
    
    private static boolean performSwap(Player player, EquipmentSlot slot, ItemStack currentItem, ItemStack replacement) {
        try {
            if (!canManipulateInventory(player)) return false;

            // Locate the replacement in the hotbar or main inventory (raw indices 0-35).
            int replacementInvIndex = -1;
            for (int i = 0; i <= 35; i++) {
                if (player.getInventory().getItem(i) == replacement) {
                    replacementInvIndex = i;
                    break;
                }
            }
            if (replacementInvIndex == -1) return false;

            int srcMenuSlot = containerMenuSlot(replacementInvIndex);
            if (srcMenuSlot == -1) return false;

            ItemStack oldStack = player.getItemBySlot(slot).copy();
            ItemStack newStack = replacement.copy();

            Constants.LOG.debug("Swapping {} ({}/{}) for {} ({}/{})",
                    oldStack.getHoverName().getString(),
                    oldStack.getMaxDamage() - oldStack.getDamageValue(), oldStack.getMaxDamage(),
                    newStack.getHoverName().getString(),
                    newStack.getMaxDamage() - newStack.getDamageValue(), newStack.getMaxDamage());

            Minecraft client = Minecraft.getInstance();
            int containerId = player.inventoryMenu.containerId;

            if (slot == EquipmentSlot.MAINHAND) {
                // Move the replacement into the held hotbar slot with one server-synced SWAP.
                int selected = player.getInventory().getSelectedSlot();
                client.gameMode.handleInventoryMouseClick(containerId, srcMenuSlot, selected, ClickType.SWAP, player);
            } else {
                int armorSlot = armorMenuSlot(slot);
                if (armorSlot == -1) return false;
                // Emulate the manual pick up / place / put back sequence, each click server-synced:
                // grab the replacement, drop it onto the worn piece (picking the worn piece up),
                // then drop the worn piece into the now-empty source slot.
                client.gameMode.handleInventoryMouseClick(containerId, srcMenuSlot, 0, ClickType.PICKUP, player);
                client.gameMode.handleInventoryMouseClick(containerId, armorSlot, 0, ClickType.PICKUP, player);
                client.gameMode.handleInventoryMouseClick(containerId, srcMenuSlot, 0, ClickType.PICKUP, player);
            }

            DuraPing.toast("Auto-swap: " + newStack.getHoverName().getString());
            DuraPing.sendChat(player, Component.literal("§6[DuraPing] Auto-swapped "
                    + oldStack.getHoverName().getString() + " for " + newStack.getHoverName().getString()));

            String itemId = currentItem.getItem().toString() + "_" + currentItem.getMaxDamage();
            String cooldownKey = slot.getName() + "_" + player.getStringUUID() + "_" + itemId;
            lastArmorSwapTime.put(cooldownKey, System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            Constants.LOG.error("Auto-swap failed", e);
            return false;
        }
    }

    /**
     * Map a raw hotbar/main-inventory index (0-35) to its slot index in the player's
     * inventory menu, which is what {@code handleInventoryMouseClick} expects.
     * Hotbar (0-8) lives at menu slots 36-44; the main inventory (9-35) is unchanged.
     */
    private static int containerMenuSlot(int invIndex) {
        if (invIndex >= 0 && invIndex <= 8) return 36 + invIndex;
        if (invIndex >= 9 && invIndex <= 35) return invIndex;
        return -1;
    }

    /** Inventory-menu slot index for each worn armor piece (head 5, chest 6, legs 7, feet 8). */
    private static int armorMenuSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            case FEET -> 8;
            default -> -1;
        };
    }

    /**
     * Auto-swap drives the inventory through server-synced container clicks, which are only
     * valid against the player's own inventory menu. Bail if another container (a chest, etc.)
     * is open: the slot indices would differ and we must not disturb an open GUI.
     */
    private static boolean canManipulateInventory(Player player) {
        Minecraft client = Minecraft.getInstance();
        if (client.gameMode == null || client.player == null) return false;
        // Only the player's own inventory menu, and only when nothing is on the cursor
        // (the pick-up/place sequence assumes an empty cursor).
        return player == client.player
                && player.containerMenu == player.inventoryMenu
                && player.inventoryMenu.getCarried().isEmpty();
    }
    
    public static void manualAutoSwap(Player player) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) {
            DuraPing.toast("Auto-swap is disabled");
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
            DuraPing.toast("No suitable replacements found");
        }
    }
    
    public static void manualAutoSwapMainHand(Player player) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) {
            DuraPing.toast("Auto-swap is disabled");
            return;
        }
        
        if (cfg.autoSwapTools) {
            if (attemptAutoSwap(player, EquipmentSlot.MAINHAND)) {
                DuraPing.toast("Main hand auto-swapped");
            } else {
                DuraPing.toast("No suitable main hand replacement found");
            }
        } else {
            DuraPing.toast("Main hand auto-swap is disabled");
        }
    }
    
    public static void manualAutoSwapArmor(Player player) {
        DuraPingConfig cfg = DuraPingConfig.get();
        if (!cfg.autoSwapEnabled) {
            DuraPing.toast("Auto-swap is disabled");
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
                DuraPing.toast("Armor auto-swapped");
            } else {
                DuraPing.toast("No suitable armor replacement found");
            }
        } else {
            DuraPing.toast("Armor auto-swap is disabled");
        }
    }
}


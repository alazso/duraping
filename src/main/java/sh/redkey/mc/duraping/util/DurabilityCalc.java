package sh.redkey.mc.duraping.util;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

public class DurabilityCalc {
    public static int effectiveUsesLeft(ItemStack s) {
        // Expected value factoring Unbreaking: EV ≈ remaining / (1 - p(no-damage))
        // For Unbreaking L on tools (not armor): p(no-damage)=L/(L+1)
        // Keep simple MVP; implement later.
        return Math.max(0, s.getMaxDamage() - s.getDamage());
    }
}


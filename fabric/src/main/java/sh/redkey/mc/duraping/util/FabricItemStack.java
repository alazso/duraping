package sh.redkey.mc.duraping.util;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

/**
 * Fabric implementation of the platform-agnostic ItemStack interface.
 */
public class FabricItemStack implements sh.redkey.mc.duraping.util.ItemStack {
    private final ItemStack stack;
    
    public FabricItemStack(ItemStack stack) {
        this.stack = stack;
    }
    
    @Override
    public int getDurability() {
        return stack.getMaxDamage() - stack.getDamage();
    }
    
    @Override
    public int getMaxDurability() {
        return stack.getMaxDamage();
    }
    
    @Override
    public String getItemId() {
        return Registries.ITEM.getId(stack.getItem()).toString();
    }
    
    @Override
    public String getDisplayName() {
        return stack.getName().getString();
    }
    
    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    @Override
    public int getCount() {
        return stack.getCount();
    }
    
    /**
     * Get the underlying Fabric ItemStack.
     * @return the Fabric ItemStack
     */
    public ItemStack getFabricStack() {
        return stack;
    }
}

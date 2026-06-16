package sh.redkey.mc.duraping.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ItemKey(ItemStack stack, ResourceLocation id) {
    public static ItemKey of(ItemStack s) {
        if (s == null || s.isEmpty()) return new ItemKey(ItemStack.EMPTY, ResourceLocation.withDefaultNamespace("air"));
        return new ItemKey(s, BuiltInRegistries.ITEM.getKey(s.getItem()));
    }
    public boolean isEmpty() { return stack == null || stack.isEmpty(); }
    public String displayName() { return stack.getHoverName().getString(); }
}


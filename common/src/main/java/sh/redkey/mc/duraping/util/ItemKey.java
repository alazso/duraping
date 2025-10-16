package sh.redkey.mc.duraping.util;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public record ItemKey(ItemStack stack, Identifier id) {
    public static ItemKey of(ItemStack s) {
        if (s == null || s.isEmpty()) return new ItemKey(ItemStack.EMPTY, Identifier.of("minecraft","air"));
        return new ItemKey(s, Registries.ITEM.getId(s.getItem()));
    }
    public boolean isEmpty() { return stack == null || stack.isEmpty(); }
    public String displayName() { return stack.getName().getString(); }
}


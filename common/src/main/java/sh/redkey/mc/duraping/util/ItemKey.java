package sh.redkey.mc.duraping.util;

/**
 * Platform-agnostic representation of an item key.
 * Used for identifying and comparing items across platforms.
 */
public record ItemKey(ItemStack stack, Identifier id) {
    public static ItemKey of(ItemStack s) {
        if (s == null || s.isEmpty()) {
            return new ItemKey(null, Identifier.of("minecraft", "air"));
        }
        return new ItemKey(s, Identifier.of(s.getItemId()));
    }
    
    public boolean isEmpty() { 
        return stack == null || stack.isEmpty(); 
    }
    
    public String displayName() { 
        return stack != null ? stack.getDisplayName() : "Air"; 
    }
}


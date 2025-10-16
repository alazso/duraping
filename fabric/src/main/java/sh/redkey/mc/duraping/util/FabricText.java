package sh.redkey.mc.duraping.util;

import net.minecraft.text.Text;

/**
 * Fabric implementation of the platform-agnostic Text interface.
 */
public class FabricText implements sh.redkey.mc.duraping.util.Text {
    private final Text text;
    
    public FabricText(Text text) {
        this.text = text;
    }
    
    @Override
    public String getString() {
        return text.getString();
    }
    
    @Override
    public String getFormattedString() {
        return text.getString();
    }
    
    /**
     * Get the underlying Fabric Text.
     * @return the Fabric Text
     */
    public Text getFabricText() {
        return text;
    }
    
    /**
     * Create a FabricText from a Fabric Text.
     */
    public static FabricText of(Text text) {
        return new FabricText(text);
    }
    
    /**
     * Create a FabricText from a string.
     */
    public static FabricText literal(String text) {
        return new FabricText(Text.literal(text));
    }
}

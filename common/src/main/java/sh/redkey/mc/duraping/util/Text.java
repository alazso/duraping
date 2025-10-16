package sh.redkey.mc.duraping.util;

/**
 * Platform-agnostic representation of text.
 * Platform-specific implementations should implement this interface.
 */
public interface Text {
    /**
     * Get the plain text content.
     * @return plain text
     */
    String getString();
    
    /**
     * Get the formatted text content.
     * @return formatted text
     */
    String getFormattedString();
    
    /**
     * Create a simple text from a string.
     * @param text text content
     * @return text instance
     */
    static Text of(String text) {
        return new SimpleText(text);
    }
    
    /**
     * Create a literal text from a string.
     * @param text text content
     * @return text instance
     */
    static Text literal(String text) {
        return new SimpleText(text);
    }
    
    /**
     * Simple text implementation.
     */
    record SimpleText(String content) implements Text {
        @Override
        public String getString() {
            return content;
        }
        
        @Override
        public String getFormattedString() {
            return content;
        }
    }
}

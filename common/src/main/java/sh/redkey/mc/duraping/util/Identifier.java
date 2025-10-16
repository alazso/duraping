package sh.redkey.mc.duraping.util;

/**
 * Platform-agnostic representation of an identifier.
 * Platform-specific implementations should implement this interface.
 */
public interface Identifier {
    /**
     * Get the namespace of this identifier.
     * @return namespace
     */
    String getNamespace();
    
    /**
     * Get the path of this identifier.
     * @return path
     */
    String getPath();
    
    /**
     * Get the full identifier as a string.
     * @return full identifier string
     */
    String toString();
    
    /**
     * Create an identifier from a string.
     * @param id identifier string
     * @return identifier instance
     */
    static Identifier of(String id) {
        if (id.contains(":")) {
            String[] parts = id.split(":", 2);
            return new SimpleIdentifier(parts[0], parts[1]);
        }
        return new SimpleIdentifier("minecraft", id);
    }
    
    /**
     * Create an identifier from namespace and path.
     * @param namespace namespace
     * @param path path
     * @return identifier instance
     */
    static Identifier of(String namespace, String path) {
        return new SimpleIdentifier(namespace, path);
    }
    
    /**
     * Simple identifier implementation.
     */
    record SimpleIdentifier(String namespace, String path) implements Identifier {
        @Override
        public String getNamespace() {
            return namespace;
        }
        
        @Override
        public String getPath() {
            return path;
        }
        
        @Override
        public String toString() {
            return namespace + ":" + path;
        }
    }
}

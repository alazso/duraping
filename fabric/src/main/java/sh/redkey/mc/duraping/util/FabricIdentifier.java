package sh.redkey.mc.duraping.util;

import net.minecraft.util.Identifier;

/**
 * Fabric implementation of the platform-agnostic Identifier interface.
 */
public class FabricIdentifier implements sh.redkey.mc.duraping.util.Identifier {
    private final Identifier identifier;
    
    public FabricIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public String getNamespace() {
        return identifier.getNamespace();
    }
    
    @Override
    public String getPath() {
        return identifier.getPath();
    }
    
    @Override
    public String toString() {
        return identifier.toString();
    }
    
    /**
     * Get the underlying Fabric Identifier.
     * @return the Fabric Identifier
     */
    public Identifier getFabricIdentifier() {
        return identifier;
    }
    
    /**
     * Create a FabricIdentifier from a Fabric Identifier.
     */
    public static FabricIdentifier of(Identifier identifier) {
        return new FabricIdentifier(identifier);
    }
    
    /**
     * Create a FabricIdentifier from namespace and path.
     */
    public static FabricIdentifier of(String namespace, String path) {
        return new FabricIdentifier(new Identifier(namespace, path));
    }
}

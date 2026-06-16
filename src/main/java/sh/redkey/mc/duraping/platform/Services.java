package sh.redkey.mc.duraping.platform;

import sh.redkey.mc.duraping.platform.services.IPlatformHelper;

public class Services {
    public static final IPlatformHelper PLATFORM = createPlatform();

    private static IPlatformHelper createPlatform() {
        //? fabric {
        return new FabricPlatformHelper();
        //?}
        //? neoforge {
        /*return new NeoForgePlatformHelper();*/
        //?}
    }
}

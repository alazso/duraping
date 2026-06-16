plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.14.10" apply false
    id("net.neoforged.moddev") version "2.0.115" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.7.4" apply false
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
    // Expose the active loader as a Stitcher constant so code can use //? if fabric { ... }
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge")
}

plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
    // Expose the active loader as a Stitcher constant so code can use //? if fabric { ... }
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge")
}

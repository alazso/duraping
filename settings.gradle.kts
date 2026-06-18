pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.2"
}

rootProject.name = "DuraPing"

stonecutter {
    create(rootProject) {
        // One Fabric jar covers 1.21.9-1.21.11: Fabric's intermediary mappings make the
        // 1.21.11 ResourceLocation->Identifier rename transparent at runtime (verified in-game).
        version("1.21.9-fabric", "1.21.9").buildscript = "build.fabric.gradle.kts"
        // NeoForge runs against Mojmap with no intermediary cushion, so the rename forces a split:
        // one jar for 1.21.9-1.21.10 (verified), one for 1.21.11.
        version("1.21.9-neoforge", "1.21.9").buildscript = "build.neoforge.gradle.kts"
        version("1.21.11-neoforge", "1.21.11").buildscript = "build.neoforge.gradle.kts"

        vcsVersion = "1.21.9-fabric"
    }
}

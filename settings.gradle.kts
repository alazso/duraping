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
        fun mc(version: String) {
            version("$version-fabric", version).buildscript = "build.fabric.gradle.kts"
            version("$version-neoforge", version).buildscript = "build.neoforge.gradle.kts"
        }
        mc("1.21.9")
        mc("1.21.10")
        mc("1.21.11")

        vcsVersion = "1.21.10-fabric"
    }
}

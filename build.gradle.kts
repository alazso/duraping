import org.gradle.jvm.tasks.Jar

plugins {
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("pl.allegro.tech.build.axion-release") version "1.18.15"
    java
}

scmVersion {
    tag {
        prefix.set("v")
    }
    // Axion only reads tags, doesn't create them
    // Create tags manually: git tag -a v0.2.0 -m "Release 0.2.0"
}

group = property("maven_group")!!
version = scmVersion.version

val minecraftVersion: String by project

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(property("java_version").toString()))
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.shedaniel.me/") // Cloth Config + Mod Menu
    maven("https://maven.terraformersmc.com/releases/") // Mod Menu
}

dependencies {
    val yarnMappings = property("yarn_mappings") as String
    val loaderVersion = property("loader_version") as String
    val fabricApiVersion = property("fabric_api_version") as String

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    // Config UI
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:15.0.130") // verify 1.21.9 line
    modImplementation("com.terraformersmc:modmenu:11.0.0") // verify 1.21.9 line
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "modid" to project.property("mod_id"),
            "name" to project.property("mod_name")
        )
    }
}

scmVersion {
    tag { prefix = "v" }
    // Using Conventional Commits by default; do not auto-push tags from CI.
}

tasks.jar {
    from("LICENSE") { rename { "${it}_${project.property("mod_name")}" } }
    archiveBaseName.set("duraping")
    archiveVersion.set("v${version}-${minecraftVersion}")
}

tasks.named<Jar>("remapJar") {
    archiveBaseName.set("duraping")
    archiveVersion.set("v${version}-${minecraftVersion}")
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("duraping")
    archiveVersion.set("v${version}-${minecraftVersion}")
    archiveClassifier.set("sources")
}


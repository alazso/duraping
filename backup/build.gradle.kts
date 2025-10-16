import org.gradle.jvm.tasks.Jar

plugins {
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("pl.allegro.tech.build.axion-release") version "1.18.15"
    id("me.modmuss50.mod-publish-plugin") version "0.7.4"
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

val minecraft_version: String by project

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

    minecraft("com.mojang:minecraft:$minecraft_version")
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
    archiveVersion.set("v${version}-${minecraft_version}")
}

tasks.named<Jar>("remapJar") {
    archiveBaseName.set("duraping")
    archiveVersion.set("v${version}-${minecraft_version}")
}

tasks.named<Jar>("sourcesJar") {
    archiveBaseName.set("duraping")
    archiveVersion.set("v${version}-${minecraft_version}")
    archiveClassifier.set("sources")
}

publishMods {
    file = tasks.named<Jar>("remapJar").get().archiveFile
    changelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asText
    type = STABLE
    modLoaders.add("fabric")
    displayName = "DuraPing v${version}"
    
    // Enable dry run if API keys are missing (for local testing)
    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null
    
    // CurseForge Publishing
    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
        projectId = property("curseforge_id").toString()
        minecraftVersions.add(minecraft_version)
        
        // Dependencies
        requires {
            slug = "fabric-api"
        }
        requires {
            slug = "cloth-config"
        }
        optional {
            slug = "modmenu"
        }
    }
    
    // Modrinth Publishing
    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = property("modrinth_id").toString()
        minecraftVersions.add(minecraft_version)
        
        // Dependencies
        requires("fabric-api")
        requires("cloth-config")
        optional("modmenu")
    }
    
    // Discord Webhook (Optional)
    // Uncomment and configure if you want Discord announcements
    // discord {
    //     webhookUrl = providers.environmentVariable("DISCORD_WEBHOOK")
    //     username = "DuraPing"
    //     avatarUrl = "https://raw.githubusercontent.com/redlynxlabs/duraping/main/src/main/resources/assets/duraping/icon.png"
    // }
}


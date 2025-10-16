plugins {
    // see https://fabricmc.net/develop/ for new versions
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
    // see https://projects.neoforged.net/neoforged/moddevgradle for new versions
    id("net.neoforged.moddev") version "2.0.115" apply false
    // Axion Release for version management
    id("pl.allegro.tech.build.axion-release") version "1.18.15"
    // Mod Publishing
    id("me.modmuss50.mod-publish-plugin") version "0.7.4"
}

// Temporarily disable Axion release due to git issues
// scmVersion {
//     tag {
//         prefix.set("v")
//     }
//     // Axion only reads tags, doesn't create them
//     // Create tags manually: git tag -a v0.2.0 -m "Release 0.2.0"
// }

group = property("group")!!
version = property("version")!!

val minecraft_version: String by project

// Publishing configuration for all platforms
publishMods {
    changelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asText
    type = STABLE

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


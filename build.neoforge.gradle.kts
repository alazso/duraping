plugins {
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
}

val mcVersion = project.name.substringBeforeLast("-")
fun dep(key: String): String = property("${key}_$mcVersion") as String

// NeoForge has no intermediary cushion, so the 1.21.11 ResourceLocation->Identifier rename
// splits the 1.21.x line (1.21.9-1.21.10 vs 1.21.11). Unobfuscated 26.x is its own jar.
val is26 = mcVersion.substringBefore(".").toInt() >= 26
val neoMcRange = when {
    is26 -> "[26.1.2,26.2)"
    mcVersion == "1.21.11" -> "[1.21.11,1.22)"
    else -> "[1.21.9,1.21.11)"
}
val neoGameVersions = when {
    is26 -> listOf(mcVersion)
    mcVersion == "1.21.11" -> listOf("1.21.11")
    else -> listOf("1.21.9", "1.21.10")
}
val neoLabel = when {
    is26 -> mcVersion
    mcVersion == "1.21.11" -> "1.21.11"
    else -> "1.21.9-1.21.10"
}

stonecutter {
    // Minecraft 1.21.11 renamed ResourceLocation -> Identifier (and location() -> identifier()).
    replacements.string(current.parsed >= "1.21.11") {
        replace("ResourceLocation", "Identifier")
        replace("location()", "identifier()")
    }
    // Minecraft 26.x renames in the common code (the fabric-only HUD/keybind changes live in
    // the //? fabric block and never reach NeoForge).
    replacements.string(current.parsed >= "26.1.2") {
        replace("ClickType", "ContainerInput")
        replace("handleInventoryMouseClick", "handleContainerInput")
        replace("player.displayClientMessage(msg, false)", "player.sendSystemMessage(msg)")
        replace("player.displayClientMessage(msg, true)", "player.sendOverlayMessage(msg)")
        // NeoForge HUD entrypoint: GuiGraphics was replaced by GuiGraphicsExtractor.
        replace("import net.minecraft.client.gui.GuiGraphics;", "import net.minecraft.client.gui.GuiGraphicsExtractor;")
        replace("renderFlashOverlay(GuiGraphics graphics)", "renderFlashOverlay(GuiGraphicsExtractor graphics)")
    }
}

version = "${property("version")}+$neoLabel"
group = property("mod_group")!!
base { archivesName = "${property("mod_id")}-neoforge" }

java {
    toolchain.languageVersion = JavaLanguageVersion.of(if (is26) 25 else (property("java_version") as String).toInt())
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.shedaniel.me/")
    maven("https://repo.faststats.dev/releases")
}

sourceSets {
    main {
        resources.srcDir(rootProject.file("src/neoforge/resources"))
    }
}

dependencies {
    // Cloth Config powers the in-game config screen (DuraPingConfigScreen), shared with Fabric.
    implementation("me.shedaniel.cloth:cloth-config-neoforge:${dep("cloth")}")
    // FastStats usage metrics requires Java 25, so only the 26.x node gets it.
    // gson is provided by Minecraft; drop FastStats's strict gson pin so it does not clash with MC's.
    if (is26) implementation("dev.faststats.metrics:neoforge:0.27.0") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
}

neoForge {
    version = dep("neoforge")
    findProperty("parchment_$mcVersion")?.let { pv ->
        parchment {
            minecraftVersion = mcVersion
            mappingsVersion = pv.toString()
        }
    }
    runs {
        register("client") { client() }
    }
    mods {
        register(property("mod_id") as String) {
            sourceSet(sourceSets["main"])
        }
    }
}

val resourceProps = mapOf(
    "version" to property("version").toString(),
    "minecraft_version_range" to neoMcRange,
    "neoforge_loader_version_range" to "[4,)",
)
tasks.processResources {
    inputs.properties(resourceProps)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(resourceProps)
    }
}

publishMods {
    file = tasks.named<org.gradle.api.tasks.bundling.AbstractArchiveTask>("jar").flatMap { it.archiveFile }
    type = me.modmuss50.mpp.ReleaseType.STABLE
    modLoaders.add("neoforge")
    displayName = "DuraPing ${property("version")} (NeoForge $neoLabel)"
    version = project.version.toString()
    changelog = providers.environmentVariable("RELEASE_CHANGELOG")
        .orElse("See https://github.com/redlynxlabs/duraping/blob/main/CHANGELOG.md")
    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = property("modrinth_id").toString()
        minecraftVersions.addAll(neoGameVersions)
        requires("cloth-config")
    }
    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
        projectId = property("curseforge_id").toString()
        minecraftVersions.addAll(neoGameVersions)
        requires { slug = "cloth-config" }
    }
}

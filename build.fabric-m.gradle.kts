plugins {
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
}

val mcVersion = project.name.substringBeforeLast("-")
fun dep(key: String): String = property("${key}_$mcVersion") as String

stonecutter {
    // 1.21.11+ (including unobfuscated 26.x) renamed ResourceLocation -> Identifier.
    replacements.string(current.parsed >= "1.21.11") {
        replace("ResourceLocation", "Identifier")
        replace("location()", "identifier()")
    }
    // Minecraft 26.x (unobfuscated) API renames.
    replacements.string(current.parsed >= "26.1.2") {
        // Inventory click enum + client-side click method (AutoSwapUtil).
        replace("ClickType", "ContainerInput")
        replace("handleInventoryMouseClick", "handleContainerInput")
        // fabric-api keybinding module: fabric-key-binding-api-v1 -> fabric-key-mapping-api-v1.
        replace("keybinding.v1.KeyBindingHelper", "keymapping.v1.KeyMappingHelper")
        replace("KeyBindingHelper.registerKeyBinding", "KeyMappingHelper.registerKeyMapping")
        // HudRenderCallback removed; the register call itself is gated in DuraPingFabric.
        replace("import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;", "import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;")
        // Player.displayClientMessage(Component, boolean) removed (rewrites the two helpers in DuraPing).
        replace("player.displayClientMessage(msg, false)", "player.sendSystemMessage(msg)")
        replace("player.displayClientMessage(msg, true)", "player.sendOverlayMessage(msg)")
    }
}

version = "${property("version")}+$mcVersion"
group = property("mod_group")!!
base { archivesName = "${property("mod_id")}-fabric" }

java {
    // 26.x runs on Java 25.
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://repo.faststats.dev/releases")
}

sourceSets {
    main {
        resources.srcDir(rootProject.file("src/fabric/resources"))
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    // Unobfuscated Minecraft: no mappings() block (officialMojangMappings does not exist for 26.x),
    // and dependencies are used as-is (plain implementation, no remap).
    implementation("net.fabricmc:fabric-loader:${dep("fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${dep("fabric_api")}")
    implementation("com.terraformersmc:modmenu:${dep("modmenu")}")
    implementation("me.shedaniel.cloth:cloth-config-fabric:${dep("cloth")}")
    // FastStats usage metrics (requires Java 25; this modern buildscript is 26.x only).
    // gson is provided by Minecraft; drop FastStats's strict gson pin so it does not clash with MC's.
    implementation("dev.faststats.metrics:fabric:0.27.0") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
}

// FastStats ships as a plain library (no fabric.mod.json), so merge its classes into the jar
// for runtime (gson is excluded above; Minecraft provides it).
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get()
                .filter { it.path.contains("dev.faststats") }
                .map { zipTree(it) }
    }) {
        exclude("module-info.class", "META-INF/MANIFEST.MF")
    }
}

val resourceProps = mapOf(
    "version" to property("version").toString(),
    "fabric_loader_version" to dep("fabric_loader"),
    "fabric_minecraft_version_range" to ">=26.1.2 <26.2",
)
tasks.processResources {
    inputs.properties(resourceProps)
    filesMatching("fabric.mod.json") {
        expand(resourceProps)
    }
}

publishMods {
    // No-remap loom: the final artifact is the jar task (no remapJar), with FastStats bundled in.
    file = tasks.named<org.gradle.api.tasks.bundling.AbstractArchiveTask>("jar").flatMap { it.archiveFile }
    type = me.modmuss50.mpp.ReleaseType.STABLE
    modLoaders.add("fabric")
    displayName = "DuraPing ${property("version")} (Fabric $mcVersion)"
    // Distinct version number from the NeoForge 26.x jar (both share the 26.1.2 game version).
    version = "${property("version")}+fabric-$mcVersion"
    changelog = providers.environmentVariable("RELEASE_CHANGELOG")
        .orElse("See https://github.com/redlynxlabs/duraping/blob/main/CHANGELOG.md")
    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = property("modrinth_id").toString()
        minecraftVersions.add(mcVersion)
        requires("fabric-api")
        requires("cloth-config")
        optional("modmenu")
    }
    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
        projectId = property("curseforge_id").toString()
        minecraftVersions.add(mcVersion)
        requires { slug = "fabric-api" }
        requires { slug = "cloth-config" }
        optional { slug = "modmenu" }
    }
}

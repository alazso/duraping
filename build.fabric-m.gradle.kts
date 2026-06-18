plugins {
    id("net.fabricmc.fabric-loom")
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
}

val resourceProps = mapOf(
    "version" to property("version").toString(),
    "fabric_loader_version" to dep("fabric_loader"),
    "fabric_minecraft_version_range" to ">=26.1 <27",
)
tasks.processResources {
    inputs.properties(resourceProps)
    filesMatching("fabric.mod.json") {
        expand(resourceProps)
    }
}

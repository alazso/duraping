plugins {
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
}

val mcVersion = project.name.substringBeforeLast("-")
fun dep(key: String): String = property("${key}_$mcVersion") as String

stonecutter {
    // Minecraft 1.21.11 renamed ResourceLocation -> Identifier (and location() -> identifier()).
    replacements.string(current.parsed >= "1.21.11") {
        replace("ResourceLocation", "Identifier")
        replace("location()", "identifier()")
    }
}

version = "${property("version")}+$mcVersion"
group = property("mod_group")!!
base { archivesName = "${property("mod_id")}-neoforge-$mcVersion" }

java {
    toolchain.languageVersion = JavaLanguageVersion.of((property("java_version") as String).toInt())
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.parchmentmc.org/")
}

sourceSets {
    main {
        resources.srcDir(rootProject.file("src/neoforge/resources"))
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
    "minecraft_version_range" to "[$mcVersion]",
    "neoforge_loader_version_range" to "[4,)",
)
tasks.processResources {
    inputs.properties(resourceProps)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(resourceProps)
    }
}

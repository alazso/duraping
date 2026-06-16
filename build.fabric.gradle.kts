plugins {
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
}

val mcVersion = project.name.substringBeforeLast("-")
fun dep(key: String): String = property("${key}_$mcVersion") as String

version = "${property("version")}+$mcVersion"
group = property("mod_group")!!
base { archivesName = "${property("mod_id")}-fabric-$mcVersion" }

java {
    toolchain.languageVersion = JavaLanguageVersion.of((property("java_version") as String).toInt())
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.parchmentmc.org/")
}

sourceSets {
    main {
        resources.srcDir(rootProject.file("src/fabric/resources"))
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$mcVersion:${dep("parchment")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${dep("fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${dep("fabric_api")}")
    modImplementation("com.terraformersmc:modmenu:${dep("modmenu")}")
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${dep("cloth")}")
}

loom {
    mixin {
        useLegacyMixinAp = true
    }
}

val resourceProps = mapOf(
    "version" to property("version").toString(),
    "fabric_loader_version" to dep("fabric_loader"),
    "fabric_minecraft_version_range" to mcVersion,
)
tasks.processResources {
    inputs.properties(resourceProps)
    filesMatching("fabric.mod.json") {
        expand(resourceProps)
    }
}

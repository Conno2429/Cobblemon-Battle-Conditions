plugins {
    id("java")
    id("dev.architectury.loom") version("1.10-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version "2.1.20"
}

group = "io.github.conno2429.cobblemonbattlecondtitions"
version = "1.0.0-SNAPSHOT"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
    maven("https://maven.parchmentmc.org")
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("net.minecraft:minecraft:1.21.1")
    "mappings"(loom.layered{
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.1:2024.11.17@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:0.16.12")

    modApi("net.fabricmc.fabric-api:fabric-api:0.115.4+1.21.1")
//    modImplementation(fabricApi.module("fabric-command-api-v2", "0.115.4+1.21.1"))

    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.2+kotlin.2.1.20")
    modImplementation("com.cobblemon:fabric:1.6.1+1.21.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}
tasks.withType<JavaCompile>().configureEach { options.release.set(21) }
kotlin.jvmToolchain(21)

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(project.properties)
    }
}
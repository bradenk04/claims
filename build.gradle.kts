import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.paperweight.userdev)
}

group = "io.github.bradenk04"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.bluecolored.de/releases")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    // Downloaded in loader
    compileOnly(kotlin("stdlib"))
    compileOnly(libs.ktoml.core)
    compileOnly(libs.ktoml.file)
    compileOnly(libs.lamp.common)
    compileOnly(libs.lamp.paper)
    compileOnly(libs.exposed.core)
    compileOnly(libs.exposed.jdbc)
    compileOnly(libs.exposed.dao)
    compileOnly(libs.hikaricp)

    // Plugin Dependencies
    compileOnly(libs.floodgate.api)
    compileOnly(libs.bluemap)
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
    reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

val kotlinVersion: String = libs.versions.kotlin.get() ?: "2.3.0"
val ktomlVersion = libs.versions.ktoml.get() ?: "0.7.1"
val lampVersion = libs.versions.lamp.get() ?: "4.0.0-rc.14"
val exposedVersion = libs.versions.exposed.get() ?: "1.0.0"
val hikaricpVersion = libs.versions.hikaricp.get() ?: "7.0.2"

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    withType<KotlinJvmCompile> {
        compilerOptions {
            javaParameters = true
        }
    }

    processResources {
        inputs.property("version", project.version)
        inputs.property("kotlinVersion", kotlinVersion)
        inputs.property("ktomlVersion", ktomlVersion)
        inputs.property("lampVersion", lampVersion)
        inputs.property("exposedVersion", exposedVersion)
        inputs.property("hikaricpVersion", hikaricpVersion)

        filesMatching("paper-plugin.yml") {
            expand("version" to project.version)
        }

        filesMatching("loader.properties") {
            expand(
                "kotlinVersion" to kotlinVersion,
                "ktomlVersion" to ktomlVersion,
                "lampVersion" to lampVersion,
                "exposedVersion" to exposedVersion,
                "hikaricpVersion" to hikaricpVersion
            )
        }
    }

    assemble {
        dependsOn(reobfJar)
    }
}
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
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    // Downloaded in loader
    compileOnly(kotlin("stdlib"))
    compileOnly(libs.ktoml.core)
    compileOnly(libs.ktoml.file)

    // Plugin Dependencies
    compileOnly(libs.floodgate.api)
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
    reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

val kotlinVersion: String = libs.versions.kotlin.get() ?: "2.3.0"
val ktomlVersion = libs.versions.ktoml.get() ?: "0.7.1"

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
        inputs.property("kotlinVersion", kotlinVersion)
        inputs.property("ktomlVersion", ktomlVersion)

        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }

        filesMatching("loader.properties") {
            expand(
                "kotlinVersion" to kotlinVersion,
                "ktomlVersion" to ktomlVersion
            )
        }
    }

    assemble {
        dependsOn(reobfJar)
    }
}
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.paperweight.userdev)
}

group = "io.github.bradenk04"
version = "0.0.1"

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
    reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

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
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    assemble {
        dependsOn(reobfJar)
    }
}
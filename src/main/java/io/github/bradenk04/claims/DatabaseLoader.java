package io.github.bradenk04.claims;

import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class DatabaseLoader {
    static DatabaseType getDatabaseType() {
        Path mainConfig = Paths.get("plugins", "Claims", "config.toml");

        if (!Files.exists(mainConfig)) {
            return DatabaseType.SQLITE;
        }

        try {
            List<String> lines = Files.readAllLines(mainConfig);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith("database")) {
                    int firstQuote = trimmed.indexOf('"');
                    int lastQuote = trimmed.lastIndexOf('"');
                    if (firstQuote != -1 && lastQuote > firstQuote) {
                        return DatabaseType.valueOf(trimmed.substring(firstQuote + 1, lastQuote).toUpperCase());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Claims] Failed to load database type from config.toml");
            e.printStackTrace();
        }
        return DatabaseType.SQLITE;
    }

    static void loadDatabaseDependencies(DatabaseType type, MavenLibraryResolver resolver, VersionsLoader versions) {
        switch (type) {
            case POSTGRESQL, MYSQL, SQLITE:
                resolver.addDependency(new Dependency(
                        new DefaultArtifact("org.jetbrains.exposed:exposed-core:" + versions.exposedVersion),
                        null
                ));
                resolver.addDependency(new Dependency(
                        new DefaultArtifact("org.jetbrains.exposed:exposed-jdbc:" + versions.exposedVersion),
                        null
                ));
                resolver.addDependency(new Dependency(
                        new DefaultArtifact("org.jetbrains.exposed:exposed-dao:" + versions.exposedVersion),
                        null
                ));
                resolver.addDependency(new Dependency(
                        new DefaultArtifact("com.zaxxer:HikariCP:" + versions.hikaricpVersion),
                        null
                ));
                break;
        }
    }
}

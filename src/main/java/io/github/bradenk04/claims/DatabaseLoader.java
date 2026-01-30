package io.github.bradenk04.claims;

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
}

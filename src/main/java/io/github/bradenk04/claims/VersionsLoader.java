package io.github.bradenk04.claims;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class VersionsLoader {
    public String kotlinVersion;
    public String ktomlVersion;
    public String lampVersion;

    private VersionsLoader(String kotlinVersion, String ktomlVersion, String lampVersion) {
        this.kotlinVersion = kotlinVersion;
        this.ktomlVersion = ktomlVersion;
    }


    public static VersionsLoader getVersions() {
        Properties props = new Properties();

        try(InputStream stream = VersionsLoader.class.getResourceAsStream("/loader.properties")) {
            if (stream == null) {
                throw new RuntimeException("loader.properties not found");
            }
            props.load(stream);
            return new VersionsLoader(props.getProperty("kotlin.version"), props.getProperty("ktoml.version"), props.getProperty("lamp.version"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dependency versions", e);
        }
    }
}

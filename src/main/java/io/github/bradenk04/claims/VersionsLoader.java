package io.github.bradenk04.claims;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionsLoader {
    public String kotlinVersion;
    public String ktomlVersion;

    private VersionsLoader(String _kotlinVersion, String _ktomlVersion) {
        this.kotlinVersion = _kotlinVersion;
        this.ktomlVersion = _ktomlVersion;
    }


    public static VersionsLoader getVersions() {
        Properties props = new Properties();

        try(InputStream stream = VersionsLoader.class.getResourceAsStream("/loader.properties")) {
            if (stream == null) {
                throw new RuntimeException("loader.properties not found");
            }
            props.load(stream);
            return new VersionsLoader(props.getProperty("kotlin.version"), props.getProperty("ktoml.version"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dependency versions", e);
        }
    }
}

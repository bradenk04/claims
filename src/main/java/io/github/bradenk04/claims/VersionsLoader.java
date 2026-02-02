package io.github.bradenk04.claims;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class VersionsLoader {
    public String kotlinVersion;
    public String ktomlVersion;
    public String lampVersion;
    public String exposedVersion;
    public String hikaricpVersion;

    private VersionsLoader(String kotlinVersion, String ktomlVersion, String lampVersion, String exposedVersion, String hikaricpVersion) {
        this.kotlinVersion = kotlinVersion;
        this.ktomlVersion = ktomlVersion;
        this.lampVersion = lampVersion;
        this.exposedVersion = exposedVersion;
        this.hikaricpVersion = hikaricpVersion;
    }


    public static VersionsLoader getVersions() {
        Properties props = new Properties();

        try(InputStream stream = VersionsLoader.class.getResourceAsStream("/loader.properties")) {
            if (stream == null) {
                throw new RuntimeException("loader.properties not found");
            }
            props.load(stream);
            return new VersionsLoader(
                    props.getProperty("kotlin.version"),
                    props.getProperty("ktoml.version"),
                    props.getProperty("lamp.version"),
                    props.getProperty("exposed.version"),
                    props.getProperty("hikaricp.version")
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dependency versions", e);
        }
    }
}

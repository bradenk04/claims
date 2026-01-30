package io.github.bradenk04.claims;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

@SuppressWarnings("UnstableApiUsage")
public class PluginLoader implements io.papermc.paper.plugin.loader.PluginLoader {
    @Override
    public void classloader(PluginClasspathBuilder pluginClasspathBuilder) {
        VersionsLoader versions = VersionsLoader.getVersions();

        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());

        resolver.addDependency(new Dependency(
                new DefaultArtifact("org.jetbrains.kotlin-stdlib:" + versions.kotlinVersion),
                null
        ));
        resolver.addDependency(new Dependency(
                new DefaultArtifact("com.akuleshov7:ktoml-core:" + versions.ktomlVersion),
                null
        ));
        resolver.addDependency(new Dependency(
                new DefaultArtifact("com.akuleshov7:ktoml-file:" + versions.ktomlVersion),
                null
        ));

        pluginClasspathBuilder.addLibrary(resolver);
    }
}

package io.github.bradenk04.claims.config

import com.akuleshov7.ktoml.file.TomlFileReader
import io.github.bradenk04.claims.ClaimPlugin
import java.io.File

object ConfigHandler {
    lateinit var config: PluginConfig

    fun initialize() {
        val dataFolder = ClaimPlugin.plugin.dataFolder

        val pluginConfigFile = File(dataFolder, "config.toml")
        if (!pluginConfigFile.exists()) {
            pluginConfigFile.createNewFile()
            pluginConfigFile.writeText(TomlFileReader.encodeToString(PluginConfig.serializer(), PluginConfig(
                database = DatabaseConfig(type = "sqlite")
            )))
        }

        config = TomlFileReader.decodeFromFile<PluginConfig>(PluginConfig.serializer(), pluginConfigFile.absolutePath)
    }
}
package io.github.bradenk04.claims.config

import com.akuleshov7.ktoml.file.TomlFileReader
import io.github.bradenk04.claims.ClaimPlugin
import io.github.bradenk04.claims.config.language.LanguageConfig
import java.io.File

object ConfigHandler {
    lateinit var config: PluginConfig
    lateinit var language: LanguageConfig

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

        val languageStr = config.language
        val langFile = File(dataFolder, "lang/$languageStr.toml")
        if (!langFile.exists()) {
            langFile.mkdirs()
            langFile.createNewFile()
            langFile.writeText(TomlFileReader.encodeToString(LanguageConfig.serializer(), LanguageConfig()))
        }
        language = TomlFileReader.decodeFromFile<LanguageConfig>(LanguageConfig.serializer(), langFile.absolutePath)
    }
}
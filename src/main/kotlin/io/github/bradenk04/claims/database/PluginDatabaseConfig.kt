package io.github.bradenk04.claims.database

data class PluginDatabaseConfig(
    val url: String,
    val driver: String,
    val username: String = "",
    val password: String = ""
)

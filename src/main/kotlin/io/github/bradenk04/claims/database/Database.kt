package io.github.bradenk04.claims.database

import io.github.bradenk04.claims.ClaimPlugin
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.database.repositories.ClaimRepository
import io.github.bradenk04.claims.database.repositories.SqlClaimRepository
import java.io.File

object Database {
    lateinit var claims: ClaimRepository

    fun initialize() {
        val config = when (ConfigHandler.config.database.type) {
            "mysql" -> PluginDatabaseConfig(
                url = "jdbc:mysql://${ConfigHandler.config.database.mysql.host}:${ConfigHandler.config.database.mysql.port}/${ConfigHandler.config.database.mysql.database}",
                driver = "com.mysql.cj.jdbc.Driver",
                username = ConfigHandler.config.database.mysql.username,
                password = ConfigHandler.config.database.mysql.password
            )
            else -> PluginDatabaseConfig(
                url = "jdbc:sqlite:${File(ClaimPlugin.plugin.dataFolder, "claims.db").absolutePath}",
                driver = "org.sqlite.JDBC",
            )
        }

        claims = SqlClaimRepository(config)
        claims.initialize()
    }
}
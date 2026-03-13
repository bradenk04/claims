package io.github.bradenk04.claims

import io.github.bradenk04.claims.command.CommandHandler
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.listener.LiquidEvents
import io.github.bradenk04.claims.listener.PlayerBlockEvents
import io.github.bradenk04.claims.listener.PlayerItemEvents
import io.github.bradenk04.claims.listener.PlayerMoveEvents
import io.github.bradenk04.claims.listener.PlayerTeleportEvents
import io.github.bradenk04.claims.permission.PermissionHelper
import org.bukkit.plugin.java.JavaPlugin

class ClaimPlugin : JavaPlugin() {
    companion object {
        lateinit var plugin: ClaimPlugin
        var isFolia = getIsFolia()
        lateinit var claimService: ClaimService
        internal lateinit var metrics: Metrics

        private fun getIsFolia(): Boolean {
            try {
                Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
                return true
            } catch (_: ClassNotFoundException) {
                return false
            }
        }
    }

    override fun onEnable() {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        plugin = this
        metrics = Metrics(this, 29288)
        ConfigHandler.initialize()
        FloodgateHelper.setupFloodgate()
        Database.initialize()
        PermissionHelper.initialize()

        claimService = ClaimService(Database.claims)
        registerListeners()
        CommandHandler.setup()

        BluemapHelper.initialize()
    }

    fun registerListeners() {
        server.pluginManager.registerEvents(PlayerItemEvents(), this)
        server.pluginManager.registerEvents(PlayerTeleportEvents(), this)
        server.pluginManager.registerEvents(PlayerMoveEvents(), this)
        server.pluginManager.registerEvents(PlayerBlockEvents(), this)
        server.pluginManager.registerEvents(LiquidEvents(), this)
    }
}
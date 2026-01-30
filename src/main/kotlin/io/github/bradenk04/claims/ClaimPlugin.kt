package io.github.bradenk04.claims

import io.github.bradenk04.claims.listener.PlayerItemEvents
import org.bukkit.plugin.java.JavaPlugin

class ClaimPlugin : JavaPlugin() {
    companion object {
        lateinit var plugin: ClaimPlugin
        var isFolia = getIsFolia()

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

        registerListeners()
    }

    fun registerListeners() {
        server.pluginManager.registerEvents(PlayerItemEvents(), this)
    }
}
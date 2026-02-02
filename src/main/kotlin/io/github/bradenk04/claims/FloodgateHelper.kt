package io.github.bradenk04.claims

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.geysermc.floodgate.api.FloodgateApi
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.UUID

object FloodgateHelper {
    var isFloodgateServer = false
        private set
    private lateinit var floodgateApi: FloodgateApi

    internal fun setupFloodgate() {
        isFloodgateServer = Bukkit.getPluginManager().isPluginEnabled("floodgate")
        if (isFloodgateServer) floodgateApi = FloodgateApi.getInstance()
    }

    fun isBedrockPlayer(player: OfflinePlayer): Boolean {
        if (!isFloodgateServer) return false
        return floodgateApi.isFloodgatePlayer(player.uniqueId)
    }
    fun isBedrockPlayer(player: UUID): Boolean {
        if (!isFloodgateServer) return false
        return floodgateApi.isFloodgatePlayer(player)
    }

    fun getPlayer(player: OfflinePlayer): FloodgatePlayer? {
        return floodgateApi.getPlayer(player.uniqueId)
    }
}
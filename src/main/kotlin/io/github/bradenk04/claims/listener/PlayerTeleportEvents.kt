package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.ClaimPlugin
import io.github.bradenk04.claims.domain.ClaimPermission
import io.github.bradenk04.claims.safeTeleport
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class PlayerTeleportEvents : Listener {
    @EventHandler
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        val to = e.to

        val toClaim = ClaimManager.getClaim(to) ?: return

        if (!toClaim.hasPermission(e.player, ClaimPermission.ENTER_REGION)) { // TODO: Check if staff bypass
            cancelTeleport(e, e.player, "Message")
            // TODO("Send message")
            return
        }
        if (toClaim.isBanned(e.player)) { // TODO: Check if staff bypass
            cancelTeleport(e, e.player, "You are banned from this claim.")
            // TODO("Send message")
            return
        }

        // TODO: Send entrance message
    }

    fun cancelTeleport(e: PlayerTeleportEvent, player: Player, reason: String? = null) {
        if (ClaimPlugin.isFolia) {
            Bukkit.getServer().globalRegionScheduler.execute(ClaimPlugin.plugin) {
                player.safeTeleport(player, e.from)
            }
        } else {
            e.isCancelled = true
        }
        TODO("Send message")
    }
}
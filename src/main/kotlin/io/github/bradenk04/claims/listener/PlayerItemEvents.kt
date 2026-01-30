package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.domain.ClaimPermission
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent

class PlayerItemEvents : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerDroppedItemEvent(e: PlayerDropItemEvent) {
        val claim = ClaimManager.getClaim(e.player.location) ?: return
        if (claim.hasPermission(e.player, ClaimPermission.DROP_ITEMS)) return
        e.isCancelled = true
        // TODO: Send message
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerPickupItemEvent(e: PlayerAttemptPickupItemEvent) {
        val claim = ClaimManager.getClaim(e.player.location) ?: return
        if (claim.hasPermission(e.player, ClaimPermission.PICKUP_ITEMS)) return
        e.isCancelled = true
        // TODO: Send message
    }
}
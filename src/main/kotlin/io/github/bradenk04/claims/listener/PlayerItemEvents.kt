package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.LanguageService
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.domain.ClaimPermission
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerItemEvents : Listener {
    private var messageCooldownDrop = ConcurrentHashMap<UUID, Long>()
    private var messageCooldownPickup = ConcurrentHashMap<UUID, Long>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerDroppedItemEvent(e: PlayerDropItemEvent) {
        val claim = ClaimManager.getClaim(e.player.location) ?: return
        if (claim.hasPermission(e.player, ClaimPermission.DROP_ITEMS)) return
        e.isCancelled = true
        val cooldownTimeElapsed = System.currentTimeMillis() - messageCooldownDrop.getOrDefault(e.player.uniqueId, 0L)
        if (cooldownTimeElapsed > 30_000) {
            messageCooldownDrop[e.player.uniqueId] = System.currentTimeMillis()
            e.player.sendMessage(
                LanguageService.parseMessageWithClaim(
                    ConfigHandler.language.claimLanguage.permissions.cantDropItems,
                    claim
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun playerPickupItemEvent(e: PlayerAttemptPickupItemEvent) {
        val claim = ClaimManager.getClaim(e.player.location) ?: return
        if (claim.hasPermission(e.player, ClaimPermission.PICKUP_ITEMS)) return
        e.isCancelled = true
        val cooldownTimeElapsed = System.currentTimeMillis() - messageCooldownPickup.getOrDefault(e.player.uniqueId, 0L)
        if (cooldownTimeElapsed > 30_000) {
            messageCooldownPickup[e.player.uniqueId] = System.currentTimeMillis()
            e.player.sendMessage(
                LanguageService.parseMessageWithClaim(
                    ConfigHandler.language.claimLanguage.permissions.cantPickupItems,
                    claim
                )
            )
        }
    }
}
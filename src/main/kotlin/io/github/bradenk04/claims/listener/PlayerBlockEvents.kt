package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.domain.ClaimPermission
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class PlayerBlockEvents : Listener {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val claim = ClaimManager.getClaim(e.block.location) ?: return
        if (claim.hasPermission(e.player, ClaimPermission.BREAK_BLOCKS)) return
        e.isCancelled = true
        e.player.sendMessage("You do not have permission to break this block.")
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val claim = ClaimManager.getClaim(e.block.location) ?: return
        if (claim.hasPermission(e.player, ClaimPermission.PLACE_BLOCKS)) return
        e.isCancelled = true
        e.player.sendMessage("You do not have permission to place this block.")
    }
}
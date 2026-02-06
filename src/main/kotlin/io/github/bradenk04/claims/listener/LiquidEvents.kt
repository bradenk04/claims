package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockFromToEvent

class LiquidEvents : Listener {
    @EventHandler
    fun onLiquidSpread(e: BlockFromToEvent) {
        if (!e.block.isLiquid) return
        val toClaim = ClaimManager.getClaim(e.toBlock.chunk) ?: return
        val fromClaim = ClaimManager.getClaim(e.block.chunk)
        if (fromClaim != null && fromClaim == toClaim) return
        e.isCancelled = true
    }

    @EventHandler
    fun onCobbleGen(e: BlockFormEvent) {

    }
}
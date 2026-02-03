package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.LanguageService
import io.github.bradenk04.claims.config.ConfigHandler
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
        e.player.sendMessage(
            LanguageService.parseMessageWithClaim(
                ConfigHandler.language.claimLanguage.permissions.cantBreakBlocks,
                claim
            )
        )
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val claim = ClaimManager.getClaim(e.block.location) ?: return
        if (claim.hasPermission(e.player, ClaimPermission.PLACE_BLOCKS)) return
        e.isCancelled = true
        e.player.sendMessage(
            LanguageService.parseMessageWithClaim(
                ConfigHandler.language.claimLanguage.permissions.cantPlaceBlocks,
                claim
            )
        )
    }
}
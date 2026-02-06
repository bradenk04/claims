package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.LanguageService
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.domain.ClaimPermission
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

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

    @Suppress("removal")
    @EventHandler
    fun onBlockInteract(e: PlayerInteractEvent) {
        when (e.clickedBlock?.type) {
            Material.BIRCH_BUTTON,
            Material.BAMBOO_BUTTON,
            Material.STONE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.CHERRY_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.MANGROVE_BUTTON,
            Material.OAK_BUTTON,
            Material.PALE_OAK_BUTTON,
            Material.POLISHED_BLACKSTONE_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.WARPED_BUTTON,
            Material.LEGACY_STONE_BUTTON,
            Material.LEGACY_WOOD_BUTTON -> {
                val claim = ClaimManager.getClaim(e.clickedBlock?.location ?: return) ?: return
                if (claim.hasPermission(e.player, ClaimPermission.CLICK_BUTTONS)) return
                e.isCancelled = true
                e.player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.permissions.cantInteract,
                        claim
                    )
                )
                return
            }

            Material.OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.ACACIA_PRESSURE_PLATE,
            Material.BAMBOO_PRESSURE_PLATE,
            Material.CHERRY_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Material.MANGROVE_PRESSURE_PLATE,
            Material.PALE_OAK_PRESSURE_PLATE,
            Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE -> {
                val claim = ClaimManager.getClaim(e.clickedBlock?.location ?: return) ?: return
                if (claim.hasPermission(e.player, ClaimPermission.USE_PRESSURE_PLATES)) return
                e.isCancelled = true
                e.player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.permissions.cantInteract,
                        claim
                    )
                )
            }

            Material.LEVER -> {
                val claim = ClaimManager.getClaim(e.clickedBlock?.location ?: return) ?: return
                if (claim.hasPermission(e.player, ClaimPermission.USE_LEVERS)) return
                e.isCancelled = true
                e.player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.permissions.cantInteract,
                        claim
                    )
                )
            }

            else -> {
                val claim = ClaimManager.getClaim(e.clickedBlock?.location ?: return) ?: return
                if (claim.hasPermission(e.player, ClaimPermission.GENERIC_INTERACT)) return
                e.isCancelled = true
                e.player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.permissions.cantInteract,
                        claim
                    )
                )
            }
        }
    }
}
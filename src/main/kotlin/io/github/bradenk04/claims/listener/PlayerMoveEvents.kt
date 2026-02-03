package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.ClaimPlugin
import io.github.bradenk04.claims.LanguageService
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.domain.ClaimPermission
import io.github.bradenk04.claims.safeTeleport
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveEvents : Listener {
    // TODO: Vehicle move

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (!changedBlocks(e.from, e.to)) return

        val claim = ClaimManager.getClaim(e.to)
        val oldClaim = ClaimManager.getClaim(e.from)

        if (claim != null) {
            if (!claim.hasPermission(e.player, ClaimPermission.ENTER_REGION)) { // TODO: Check bypass
                sendBack(e)
                e.player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.permissions.cantEnterRegion,
                        claim
                    )
                )
                // TODO: Send message
            }else if (claim.isBanned(e.player)) { // TODO: Check bypass
                sendBack(e)
                e.player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.permissions.banned,
                        claim
                    )
                )
            } else if (oldClaim?.id != claim.id) {
                if (ConfigHandler.config.claimSettings.showEnterRegionMessage) {
                    e.player.sendMessage(
                        LanguageService.parseMessageWithClaim(
                            ConfigHandler.language.claimLanguage.enteredRegionMessage,
                            claim
                        )
                    )
                }
                if (ConfigHandler.language.claimLanguage.enteredRegionActionBar != null && ConfigHandler.config.claimSettings.showEnterRegionActionBar) {
                    e.player.sendActionBar(
                        LanguageService.parseMessageWithClaim(
                            ConfigHandler.language.claimLanguage.enteredRegionActionBar!!,
                            claim
                        )
                    )
                }
            }
        }

        if (oldClaim != null && oldClaim.id != claim?.id) {
            if (ConfigHandler.config.claimSettings.showLeftRegionMessage) {
                e.player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.leftRegionMessage,
                        oldClaim
                    )
                )
            }
            if (ConfigHandler.language.claimLanguage.leftRegionActionBar != null && ConfigHandler.config.claimSettings.showLeftRegionActionBar) {
                e.player.sendActionBar(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.leftRegionActionBar!!,
                        oldClaim
                    )
                )
            }
        }

    }

    fun changedBlocks(from: Location, to: Location): Boolean {
        if (from.blockX != to.blockX) return true
        if (from.blockZ != to.blockZ) return true
        return false
    }

    fun sendBack(e: PlayerMoveEvent) {
        e.isCancelled = true
        val loc = e.player.location.clone()
        val xDelta = e.from.blockX - e.to.blockX
        val zDelta = e.from.blockZ - e.to.blockZ
        if (xDelta > 0) {
            loc.x = e.from.x - 3
        } else if (xDelta < 0) {
            loc.x = e.from.x + 3
        }

        if (zDelta > 0) {
            loc.z = e.from.z - 3
        } else if (zDelta < 0) {
            loc.z = e.from.z + 3
        }

        loc.y = e.from.y

        Bukkit.getRegionScheduler().run(ClaimPlugin.plugin, loc) {
            e.player.safeTeleport(loc)
        }
    }
}
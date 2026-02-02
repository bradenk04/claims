package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.ClaimPlugin
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
                // TODO: Send message
            }else if (claim.isBanned(e.player)) { // TODO: Check bypass
                sendBack(e)
                // TODO: Send message
            } else if (oldClaim?.id != claim.id) {
                val claimOwner = Bukkit.getOfflinePlayer(claim.owner)
                e.player.sendActionBar(
                    Component.text("You have entered ").color(NamedTextColor.WHITE)
                        .append(
                            Component.text(claim.getFormattedClaimName()).color(NamedTextColor.GREEN)
                        )
                )
                e.player.sendMessage(Component.text("You have entered ")
                    .append(
                        Component.text(claim.getFormattedClaimName())
                            .hoverEvent(
                                HoverEvent.showText(
                                    Component
                                        .text("Claim: ${claim.getFormattedClaimName()}")
                                        .appendNewline()
                                        .append(Component.text("Owner: ${claimOwner.name}"))
                                        .appendNewline()
                                        .append(Component.text("Description: ${claim.description}"))
                                )
                            )
                    )
                )
            }
        }

        if (oldClaim != null) {
            // TODO: Send exit message
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
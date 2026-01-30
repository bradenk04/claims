package io.github.bradenk04.claims.listener

import io.github.bradenk04.claims.ClaimManager
import io.github.bradenk04.claims.domain.ClaimPermission
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
        if (claim != null) {
            if (!claim.hasPermission(e.player, ClaimPermission.ENTER_REGION)) { // TODO: Check bypass
                sendBack(e)
                // TODO: Send message
            }else if (claim.isBanned(e.player)) { // TODO: Check bypass
                sendBack(e)
                // TODO: Send message
            } else {
                // TODO: Send entrance message
            }
        }

        val oldClaim = ClaimManager.getClaim(e.from)
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
        val xDelta = e.from.blockX - e.to.blockX
        val zDelta = e.from.blockZ - e.to.blockZ
        if (xDelta > 0) {
            e.player.location.x = e.from.x - 3
        } else if (xDelta < 0) {
            e.player.location.x = e.from.x + 3
        }

        if (zDelta > 0) {
            e.player.location.z = e.from.z - 3
        } else if (zDelta < 0) {
            e.player.location.z = e.from.z + 3
        }

        e.player.location.y = e.from.y
    }
}
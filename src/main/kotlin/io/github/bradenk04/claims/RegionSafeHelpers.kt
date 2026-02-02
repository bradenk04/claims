package io.github.bradenk04.claims

import io.github.bradenk04.claims.domain.ChunkLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

fun Player.safeTeleport(loc: Location) {
    if (ClaimPlugin.isFolia) {
        if (!isOnline || isDead) return
        val playerLoc = location
        teleportAsync(loc).thenAccept {
            scheduler.run(ClaimPlugin.plugin, {
                val event = PlayerTeleportEvent(this, playerLoc, loc, PlayerTeleportEvent.TeleportCause.PLUGIN)
                Bukkit.getPluginManager().callEvent(event)
            }, null)
        }
    } else if (isOnline && !isDead) teleport(loc)
}

fun Location.toChunkLocation() = ChunkLocation(this.world.uid, this.chunk.x, this.chunk.z)
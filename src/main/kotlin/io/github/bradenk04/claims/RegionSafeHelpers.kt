package io.github.bradenk04.claims

import io.github.bradenk04.claims.domain.ChunkLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

fun Player.safeTeleport(player: Player, loc: Location) {
    if (ClaimPlugin.isFolia) {
        if (!player.isOnline || player.isDead) return
        val playerLoc = player.location
        player.teleportAsync(loc).thenAccept {
            player.scheduler.run(ClaimPlugin.plugin, {
                val event = PlayerTeleportEvent(player, playerLoc, loc, PlayerTeleportEvent.TeleportCause.PLUGIN)
                Bukkit.getPluginManager().callEvent(event)
            }, null)
        }
    } else if (player.isOnline && !player.isDead) player.teleport(loc)
}

fun Location.toChunkLocation() = ChunkLocation(this.world.uid, this.chunk.x, this.chunk.z)
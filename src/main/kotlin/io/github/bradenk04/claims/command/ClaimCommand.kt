package io.github.bradenk04.claims.command

import io.github.bradenk04.claims.ClaimPlugin
import io.github.bradenk04.claims.ClaimService
import io.github.bradenk04.claims.toChunkLocation
import net.kyori.adventure.text.Component
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.actor.BukkitCommandActor

class ClaimCommand {
    @Command("claim")
    fun claimLand(author: BukkitCommandActor) {
        if (!author.isPlayer) {
            author.sender().sendMessage(Component.text("Only players can claim land!"))
            return
        }
        val player = author.requirePlayer()
        when (val claimResult = ClaimPlugin.claimService.claimChunk(player, player.location.toChunkLocation())) {
            is ClaimService.ClaimResult.Success -> {
                if (claimResult.isNewClaim) {
                    player.sendMessage(Component.text("Claimed new land"))
                } else {
                    player.sendMessage(Component.text("Added chunk to existing claim"))
                }
                return
            }
            is ClaimService.ClaimResult.AlreadyClaimed -> {
                player.sendMessage(Component.text("You already own this claim!"))
                return
            }
            is ClaimService.ClaimResult.NoPermission -> {
                player.sendMessage(Component.text(claimResult.reason))
                return
            }
            is ClaimService.ClaimResult.InvalidLocation -> {
                player.sendMessage(Component.text(claimResult.reason))
                return
            }
            is ClaimService.ClaimResult.Error -> {
                player.sendMessage(Component.text(claimResult.reason))
                return
            }
        }
    }
}
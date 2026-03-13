package io.github.bradenk04.claims.command

import io.github.bradenk04.claims.ClaimPlugin
import io.github.bradenk04.claims.ClaimService
import io.github.bradenk04.claims.LanguageService
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.config.language.LanguageConfig
import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.toChunkLocation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission

class ClaimCommand {
    @Command("claim")
    @CommandPermission("claims.claim")
    fun claimLand(author: BukkitCommandActor) {
        if (!author.isPlayer) {
            author.sender().sendMessage(Component.text("Only players can claim land!"))
            return
        }
        val player = author.requirePlayer()
        if (!player.hasPermission("claims.claim") && !player.hasPermission("claims.bypass")) {
            player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                    ConfigHandler.language.claimLanguage.noPermission
                )
            )
            return
        }
        when (val claimResult = ClaimPlugin.claimService.claimChunk(player, player.location.toChunkLocation())) {
            is ClaimService.ClaimResult.Success -> {
                if (claimResult.isNewClaim) {
                    val claim = Database.claims.getClaim(claimResult.claimId)
                    if (claim == null) {
                        player.sendMessage(
                            MiniMessage.miniMessage().deserialize(
                                ConfigHandler.language.claimLanguage.somethingWentWrongClaiming
                            )
                        )
                        return
                    }
                    player.sendMessage(
                        LanguageService.parseMessageWithClaim(
                            ConfigHandler.language.claimLanguage.newLandClaimed,
                            claim
                        )
                    )
                } else {
                    val claim = Database.claims.getClaim(claimResult.claimId)
                    if (claim == null) {
                        player.sendMessage(
                            MiniMessage.miniMessage().deserialize(
                                ConfigHandler.language.claimLanguage.somethingWentWrongClaiming
                            )
                        )
                        return
                    }
                    player.sendMessage(
                        LanguageService.parseMessageWithClaim(
                            ConfigHandler.language.claimLanguage.claimAddedLand,
                            claim
                        )
                    )
                }
                return
            }
            is ClaimService.ClaimResult.AlreadyClaimed -> {
                player.sendMessage(
                    LanguageService.parseMessageWithClaim(
                        ConfigHandler.language.claimLanguage.alreadyOwned,
                        claimResult.existingClaim
                    )
                )
                return
            }
            is ClaimService.ClaimResult.NoPermission -> {
                if (claimResult.becauseNoMoreClaims) {
                    player.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                            ConfigHandler.language.claimLanguage.maxClaimsReached,
                        )
                    )
                } else {
                    player.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                            ConfigHandler.language.claimLanguage.noPermission,
                        )
                    )
                }
                return
            }
            is ClaimService.ClaimResult.InvalidLocation -> {
                player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                        ConfigHandler.language.claimLanguage.invalidLocation,
                    )
                )
                return
            }
            is ClaimService.ClaimResult.Error -> {
                player.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                        ConfigHandler.language.claimLanguage.somethingWentWrongClaiming,
                    )
                )
                return
            }
        }
    }
}
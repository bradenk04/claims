package io.github.bradenk04.claims.command

import io.github.bradenk04.claims.menu.claims.ClaimListMenu
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.actor.BukkitCommandActor

class ClaimsCommand {
    @Command("claims")
    fun claims(actor: BukkitCommandActor) {
        ClaimListMenu.openDialog(actor.requirePlayer())
    }
}
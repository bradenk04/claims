package io.github.bradenk04.claims.command

import io.github.bradenk04.claims.ClaimPlugin
import revxrsal.commands.bukkit.BukkitLamp

object CommandHandler {
    fun setup() {
        val lamp = BukkitLamp.builder(ClaimPlugin.plugin).build()

        lamp.register(ClaimCommand())
    }
}
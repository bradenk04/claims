package io.github.bradenk04.claims.menu

import io.github.bradenk04.claims.FloodgateHelper
import io.github.bradenk04.claims.menu.claims.ClaimListMenu
import org.bukkit.entity.Player

object MenuHandler {
    fun openClaimsMenu(player: Player) {
        if (FloodgateHelper.isBedrockPlayer(player)) {
            val brPlayer = FloodgateHelper.getPlayer(player)
            if (brPlayer == null) {
                println("Error: Player is bedrock but getPlayer returned null")
                return
            }
            ClaimListMenu.openForm(player, brPlayer)
        } else {
            ClaimListMenu.openDialog(player)
        }
    }
}
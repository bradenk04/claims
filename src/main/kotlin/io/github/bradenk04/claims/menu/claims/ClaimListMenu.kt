package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.database.Database
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.floodgate.api.player.FloodgatePlayer

object ClaimListMenu {
    fun openDialog(player: Player) {
        val claims = Database.claims.getUsersClaims(player.uniqueId)
        if (claims.count() < 1) {
            player.sendMessage(Component.text("You have no claims! Do /claim to make a claim").color(NamedTextColor.RED))
            return
        }
        val actions = claims.map {
            ActionButton.builder(Component.text(it.getFormattedClaimName())).action(DialogAction.staticAction(
                ClickEvent.showDialog(ClaimMenu.getDialog(player, it))
            )).build()
        }

        val dialog = Dialog.create {
            it.empty()
                .base(DialogBase.builder(Component.text("Your Claims")).build())
                .type(DialogType.multiAction(
                    actions, null, 1
                ))
        }

        player.showDialog(dialog)
    }
    fun openForm(player: Player, floodgatePlayer: FloodgatePlayer) {
        val menu = CustomForm.builder()
            .title("Your Claims")

        val claims = Database.claims.getUsersClaims(player.uniqueId)
        if (claims.count() < 1) {
            player.sendMessage(Component.text("You have no claims! Do /claim to make a claim").color(NamedTextColor.RED))
            return
        }
        menu.dropdown("Claim", claims.map { it.getFormattedClaimName() })

        menu.validResultHandler { form, response ->
            val claimIndex = response.asDropdown(0)
            val claim = claims[claimIndex]
            ClaimMenu.openForm(player, floodgatePlayer, claim)
        }
        floodgatePlayer.sendForm(menu.build())
    }
}
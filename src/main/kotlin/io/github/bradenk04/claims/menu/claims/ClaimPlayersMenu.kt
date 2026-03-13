package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.FloodgateHelper
import io.github.bradenk04.claims.domain.Claim
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.Form
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.floodgate.api.player.FloodgatePlayer

object ClaimPlayersMenu {
    @Suppress("UnstableApiUsage")
    fun getDialog(player: Player, claim: Claim): Dialog {
        val actions = claim.playerRoles.map {
            val target = Bukkit.getOfflinePlayer(it.key)
            val targetName = target.name ?: (FloodgateHelper.getPlayer(target)?.username ?: "N/A")
            val role = claim.roles.find { role -> role.name == it.value }
            ActionButton.builder(
                Component.text("${targetName}: ")
                    .append(Component.text(it.value, role?.color))
            )
                .action(DialogAction.staticAction(ClickEvent.showDialog(
                    ClaimSetPlayerRoleMenu.getDialog(player, claim, target, it.value)
                )))
                .build()
        }.toMutableList()

        actions.add(
            ActionButton
                .builder(Component.text("AddPlayer"))
                .action(DialogAction.staticAction(ClickEvent.showDialog(ClaimAddPlayerMenu.getDialog(player, claim))))
                .build()
        )

        return Dialog.create {
            it.empty()
                .base(
                    DialogBase
                        .builder(Component.text("${claim.getFormattedClaimName()} Players"))
                        .build()
                )
                .type(DialogType.multiAction(actions, null, 1))
        }
    }

    fun getForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim): Form {
        val form = SimpleForm.builder()
        form.title("Claim Players")
        claim.playerRoles.forEach { (uuid, role) ->
            val player = Bukkit.getOfflinePlayer(uuid)
            form.button(player.name ?: ((FloodgateHelper.getPlayer(player)?.username ?: "N/A") + ": " + role))
        }

        form.validResultHandler { form, response ->
            val clicked = response.clickedButtonId()
            val list = claim.playerRoles.toList()
            val playerClicked = list[clicked].first

            floodgatePlayer.sendForm(ClaimSetPlayerRoleMenu.getForm(player, floodgatePlayer, claim, playerClicked))
        }

        return form.build()
    }
}
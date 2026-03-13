package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.domain.Claim
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.Form
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.floodgate.api.player.FloodgatePlayer

object ClaimRoleListMenu {
    @Suppress("UnstableApiUsage")
    fun getDialog(player: Player, claim: Claim): Dialog {
        val roles = claim.roles.toMutableList()

        return Dialog.create {
            it.empty()
                .base(
                    DialogBase
                        .builder(Component.text("${claim.getFormattedClaimName()} Roles"))
                        .build()
                )
                .type(
                    DialogType.multiAction(listOf(*(roles.map { role ->
                        ActionButton
                            .builder(Component.text(role.name.capitalize()).color(role.color))
                            .action(DialogAction.staticAction(
                                ClickEvent.showDialog(ClaimEditRoleMenu.getDialog(player, claim, role))
                            ))
                            .build()
                    }.toTypedArray()), ActionButton
                        .builder(Component.text("Create new role").color(NamedTextColor.GREEN))
                        .action(DialogAction.staticAction(ClickEvent.showDialog(ClaimCreateRoleMenu.getDialog(player, claim))))
                        .build()
                    ), null, 1)
                )
        }
    }

    fun getForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim): Form {
        val form = SimpleForm.builder()
            .title("Claim Roles")

        claim.roles.forEach { role ->
            form.button(role.name)
        }
        form.button("Create new role")


        form.validResultHandler { form, response ->
            val clicked = response.clickedButtonId()

            if (clicked == claim.roles.size) {
                floodgatePlayer.sendForm(ClaimCreateRoleMenu.getForm(player, floodgatePlayer, claim))
                return@validResultHandler
            }

            val clickedRole = claim.roles[clicked]
            floodgatePlayer.sendForm(ClaimEditRoleMenu.getForm(player, floodgatePlayer, claim, clickedRole))
        }
        return form.build()
    }
}
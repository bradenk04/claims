package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.domain.Claim
import io.github.bradenk04.claims.menu.ClaimRoleDialog.createRoleDialog
import io.github.bradenk04.claims.menu.ClaimRoleDialog.getRoleDialog
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
                                ClickEvent.showDialog(getRoleDialog(player, claim, role))
                            ))
                            .build()
                    }.toTypedArray()), ActionButton
                        .builder(Component.text("Create new role").color(NamedTextColor.GREEN))
                        .action(DialogAction.staticAction(ClickEvent.showDialog(createRoleDialog(player, claim)))) // TODO: implement
                        .build()
                    ), null, 1)
                )
        }
    }

    fun getForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim): Form {
        TODO("Not yet implemented")
    }
}
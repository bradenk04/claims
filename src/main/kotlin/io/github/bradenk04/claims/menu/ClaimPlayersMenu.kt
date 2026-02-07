package io.github.bradenk04.claims.menu

import io.github.bradenk04.claims.FloodgateHelper
import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.Claim
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

object ClaimPlayersMenu {
    fun getDialog(player: Player, claim: Claim): Dialog {
        val actions = claim.playerRoles.map {
            val target = Bukkit.getOfflinePlayer(it.key)
            val targetName = target.name ?: (FloodgateHelper.getPlayer(target)?.username ?: "N/A")
            ActionButton.builder(
                Component.text("${targetName}: ${it.value}")
            )
                .action(DialogAction.staticAction(ClickEvent.showDialog(
                    getSetRoleDialog(player, claim, it.key, it.value)
                )))
                .build()
        }.toMutableList()

        actions.add(
            ActionButton
                .builder(Component.text("AddPlayer"))
                .action(DialogAction.staticAction(ClickEvent.showDialog(addPlayerDialog(player, claim))))
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

    fun getSetRoleDialog(player: Player, claim: Claim, target: UUID, current: String): Dialog {
        val roleButtons = claim.roles.map { role ->
            SingleOptionDialogInput.OptionEntry.create(role.name.lowercase(), Component.text(role.name).color(role.color), role.name == current)
        }

        return Dialog.create {
            it.empty()
                .base(
                    DialogBase.builder(Component.text("Set Role"))
                        .inputs(listOf(
                            DialogInput.singleOption("role", Component.text("Role"), roleButtons)
                                .build()
                        ))
                        .build()
                )
                .type(DialogType.notice(
                    ActionButton.builder(Component.text("Set Role"))
                        .action(DialogAction.customClick())
                        .build()
                ))
        }
    }

    fun addPlayerDialog(player: Player, claim: Claim): Dialog {

    }
}
package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.domain.Claim
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.Form
import org.geysermc.cumulus.form.SimpleForm
import org.geysermc.floodgate.api.player.FloodgatePlayer
import java.util.UUID

object ClaimSetPlayerRoleMenu {
    @Suppress("UnstableApiUsage")
    fun getDialog(player: Player, claim: Claim, target: OfflinePlayer, currentRole: String = "guest"): Dialog {
        val roleButtons = claim.roles.map { role ->
            SingleOptionDialogInput.OptionEntry.create(role.name.lowercase(), Component.text(role.name).color(role.color), role.name == currentRole)
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
                        .action(DialogAction.customClick(
                            { view, aud ->
                                val roleChoice = view.getText("role")
                                val role = claim.roles.firstOrNull { it.name.lowercase() == roleChoice } ?: return@customClick
                                claim.setPlayerRole(target.uniqueId, role.name)
                                ClaimMenu.openDialog(player, claim)
                            },
                            ClickCallback.Options.builder()
                                .uses(1)
                                .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                .build()
                        ))
                        .build()
                ))
        }
    }

    fun getForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim, editPlayer: UUID): Form {
        val form = SimpleForm.builder()
        form.title("Set Player Role")
        claim.roles.forEach { role ->
            form.button(role.name)
        }
        form.validResultHandler { form, response ->
            val clicked = response.clickedButtonId()
            val role = claim.roles[clicked]
            claim.setPlayerRole(editPlayer, role.name)
            ClaimMenu.openDialog(player, claim)
        }

        return form.build()
    }
}
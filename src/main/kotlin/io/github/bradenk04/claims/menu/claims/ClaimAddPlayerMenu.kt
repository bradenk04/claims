package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.FloodgateHelper
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
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.Form

object ClaimAddPlayerMenu {
    @Suppress("UnstableApiUsage")
    fun getDialog(executor: Player, claim: Claim): Dialog {
        val roleButtons = claim.roles.map { role ->
            SingleOptionDialogInput.OptionEntry.create(role.name.lowercase(), Component.text(role.name).color(role.color), role.name.lowercase() == "guest")
        }

        return Dialog.create {
            it.empty()
                .base(
                    DialogBase
                        .builder(Component.text("Add Player"))
                        .inputs(listOf(
                            DialogInput
                                .text("username", Component.text("Players Username"))
                                .maxLength(32)
                                .build(),
                            DialogInput.singleOption("role", Component.text("Role"), roleButtons)
                                .build()
                        ))
                        .build()
                )
                .type(
                    DialogType.notice(
                        ActionButton.builder(Component.text("Set Role"))
                            .action(DialogAction.customClick(
                                { view, aud ->
                                    val username = view.getText("username") ?: return@customClick
                                    val profile = Bukkit.getOfflinePlayer(username)
                                    val playerId = profile.playerProfile.id ?: (FloodgateHelper.getPlayer(profile)?.javaUniqueId ?: return@customClick)

                                    val roleChoice = view.getText("role")
                                    val role = claim.roles.firstOrNull { it.name.lowercase() == roleChoice } ?: return@customClick
                                    claim.setPlayerRole(playerId, role.name)
                                    executor.showDialog(ClaimMenu.getDialog(executor, claim))
                                },
                                ClickCallback.Options.builder()
                                    .uses(1)
                                    .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                    .build()
                            ))
                            .build()
                    )
                )
        }
    }

    fun getForm(executor: Player, claim: Claim): Form {
        TODO("Not yet implemented")
    }
}
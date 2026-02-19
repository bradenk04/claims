package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.Claim
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.TextDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.player.FloodgatePlayer

@Suppress("UnstableApiUsage")
object ClaimMenu {
    fun openDialog(player: Player, claim: Claim) = player.showDialog(getDialog(player, claim))
    fun getDialog(player: Player, claim: Claim): Dialog {
        val dialog = Dialog.create {
            it.empty()
                .base(DialogBase
                    .builder(Component.text(claim.getFormattedClaimName()))
                    .inputs(listOf(
                        DialogInput
                            .text("claim_name", Component.text("Claim Name"))
                            .initial(claim.getFormattedClaimName())
                            .maxLength(32)
                            .build(),
                        DialogInput
                            .text("claim_description", Component.text("Claim Description"))
                            .maxLength(128)
                            .multiline(TextDialogInput.MultilineOptions.create(5, 64))
                            .build()
                    ))
                    .build()
                )
                .type(
                    DialogType.multiAction(listOf(
                        ActionButton
                            .builder(Component.text("Players Roles"))
                            .action(DialogAction.staticAction(ClickEvent.showDialog(
                                ClaimPlayersMenu.getDialog(player, claim)
                            )))
                            .build(),
                        ActionButton
                            .builder(Component.text("Edit Role Permissions"))
                            .action(DialogAction.staticAction(ClickEvent.showDialog(
                                ClaimRoleListMenu.getDialog(player, claim)
                            )))
                            .build(),
                        ActionButton
                            .builder(Component.text("Save Claim").color(NamedTextColor.GREEN))
                            .action(DialogAction.customClick(
                                { view, aud ->
                                    val newName = view.getText("claim_name")
                                    val newDescription = view.getText("claim_description")
                                    if (claim.name == null || claim.name != newName) claim.name = newName
                                    if (claim.description == null || claim.description != newDescription) claim.description = newDescription
                                    Database.claims.saveClaim(claim)
                                    ClaimListMenu.openDialog(player)
                                },
                                ClickCallback.Options.builder()
                                    .uses(1)
                                    .lifetime(ClickCallback.DEFAULT_LIFETIME)
                                    .build()
                            ))
                            .build()
                    ), null, 2)
                )
        }

        return dialog
    }
    fun openForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim) = floodgatePlayer.sendForm(getForm(player, floodgatePlayer, claim))
    fun getForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim): Form {
        val menu = CustomForm.builder()
            .title("Claim Settings")
            .optionalInput("Claim Name", claim.getFormattedClaimName(), true)
            .optionalInput("Claim Description", claim.description ?: "", true)
            .optionalDropdown("Edit other settings", listOf(
                "None",
                "Player Roles",
                "Edit Role Permissions",
                "Banned Players"
            ), true)

        menu.validResultHandler { form, response ->
            val newName = response.next<String?>()
            val newDescription = response.next<String?>()
            val newSettings = response.next<Int?>()

            when (newSettings) {
                1 -> {
                    player.sendMessage("WIP")
                    return@validResultHandler
                }
                2 -> {
                    player.sendMessage("WIP")
                    return@validResultHandler
                }
                3 -> {
                    player.sendMessage("WIP")
                    return@validResultHandler
                }
            }

            if (newName != null && newName != "") claim.name = newName
            if (newDescription != null && newName != "") claim.description = newDescription

            Database.claims.saveClaim(claim)
        }

        return menu.build()
    }
}
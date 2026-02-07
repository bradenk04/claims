package io.github.bradenk04.claims.menu

import io.github.bradenk04.claims.FloodgateHelper
import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.Claim
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.TextDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.floodgate.api.player.FloodgatePlayer

@Suppress("UnstableApiUsage")
object ClaimListMenu {
    fun open(player: Player) {
        if (FloodgateHelper.isBedrockPlayer(player)) {
            val brPlayer = FloodgateHelper.getPlayer(player)
            if (brPlayer == null) {
                println("Error: Player is bedrock but getPlayer returned null")
                return
            }
            openBedrockMenu(player, brPlayer)
        } else {
            openJavaDialog(player)
        }
    }


    private fun openBedrockMenu(player: Player, brPlayer: FloodgatePlayer) {
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
            showClaimForm(player, brPlayer, claim)
        }
        brPlayer.sendForm(menu.build())
    }

    private fun openJavaDialog(player: Player) {
        val claims = Database.claims.getUsersClaims(player.uniqueId)
        if (claims.count() < 1) {
            player.sendMessage(Component.text("You have no claims! Do /claim to make a claim").color(NamedTextColor.RED))
            return
        }
        val actions = claims.map {
            ActionButton.builder(Component.text(it.getFormattedClaimName())).action(DialogAction.staticAction(
                ClickEvent.showDialog(getClaimDialog(player, it))
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


    private fun getClaimDialog(player: Player, claim: Claim) = Dialog.create {
        it.empty()
            .base(DialogBase
                .builder(Component.text(claim.getFormattedClaimName()))
                .inputs(listOf(
                    DialogInput
                        .text("claim_name", Component.text("Claim Name"))
                        .initial(claim.getFormattedClaimName())
                        .maxLength(64)
                        .build(),
                    DialogInput
                        .text("claim_description", Component.text("Claim Description"))
                        .maxLength(128)
                        .multiline(TextDialogInput.MultilineOptions.create(5, 64))
                        .build()
                ))
                .build())
            .type(DialogType.multiAction(listOf(
                ActionButton.builder(Component.text("Players Roles"))
                    .build(),
                ActionButton
                    .builder(Component.text("Edit Role Permissions"))
                    .action(DialogAction.staticAction(ClickEvent.showDialog(ClaimRoleDialog.getDialog(player, claim))))
                    .build(),
                ActionButton
                    .builder(Component.text("Save Claim").color(NamedTextColor.GREEN))
                    .action(DialogAction.customClick(
                        { view, audience ->
                            val newName = view.getText("claim_name")
                            val newDescription = view.getText("claim_description")
                            if (claim.name == null || claim.name != newName) {
                                claim.name = newName
                            }
                            if (claim.description == null || claim.description != newDescription) {
                                claim.description = newDescription
                            }

                            Database.claims.saveClaim(claim)
                        },
                        ClickCallback.Options.builder()
                            .uses(1)
                            .lifetime(ClickCallback.DEFAULT_LIFETIME)
                            .build()
                        )
                    )
                    .build()
            ), null, 2))
    }

    private fun showClaimForm(player: Player, brPlayer: FloodgatePlayer, claim: Claim) {
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

        brPlayer.sendForm(menu.build())

    }
}
package io.github.bradenk04.claims.menu.claims

import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.Claim
import io.github.bradenk04.claims.domain.ClaimPermissionGroups
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.player.FloodgatePlayer

object ClaimEditRoleMenu {
    fun getDialog(player: Player, claim: Claim, role: Claim.ClaimRole): Dialog {
        val permissionOptions = ClaimPermissionGroups.entries.map {
            val permId = it.name.lowercase()
            DialogInput
                .bool("perm_$permId", Component.text(permId.replace("_", " ").capitalize()))
                .initial(role.hasPermission(it))
                .onTrue("true")
                .onFalse("false")
                .build()
        }.toTypedArray()

        val saveBtn = ActionButton
            .builder(Component.text("Save Role"))
            .action(DialogAction.customClick( { view, _ ->
                val newColor = view.getText("role_color")?.let { net.kyori.adventure.text.format.TextColor.fromHexString(it) }
                if (newColor != null && newColor != role.color) {
                    Database.claims.setRoleColor(claim.id, role.name, newColor.asHexString())
                }

                ClaimPermissionGroups.entries.forEachIndexed { index, perm ->
                    val setValue = view.getBoolean("perm_${perm.name.lowercase()}") ?: false
                    if (setValue != role.hasPermission(perm)) {
                        if (setValue) {
                            claim.addPermission(role.name, perm)
                        } else {
                            claim.removePermission(role.name, perm)
                        }
                    }
                }
                player.showDialog(ClaimRoleListMenu.getDialog(player, claim))
            },
                ClickCallback.Options.builder()
                    .uses(1)
                    .lifetime(ClickCallback.DEFAULT_LIFETIME)
                    .build()
            )
            )
            .build()
        val deleteBtn = ActionButton.builder(Component.text("Delete Role"))
            .action(DialogAction.customClick({ view, _ ->
                Database.claims.deleteRole(claim.id, role.name)
                player.showDialog(ClaimRoleListMenu.getDialog(player, claim))
            },
                ClickCallback.Options.builder()
                    .uses(1)
                    .lifetime(ClickCallback.DEFAULT_LIFETIME)
                    .build()
            ))
            .build()
        val btns = if (role.name == "owner") listOf(
            ActionButton.builder(Component.text("Close").color(NamedTextColor.RED)).build(),
        ) else if (role.name == "guest") listOf(saveBtn)
        else listOf(
            saveBtn,
            deleteBtn,
        )
        return Dialog.create {
            it.empty()
                .base(
                    DialogBase
                        .builder(Component.text("${claim.getFormattedClaimName()} Roles: ${role.name.capitalize()}"))
                        .inputs(listOf(
                            DialogInput
                                .text("role_color", Component.text("Role Name"))
                                .initial(role.color?.asHexString() ?: "#FFFFFF")
                                .maxLength(7)
                                .build(),
                            *permissionOptions
                        ))
                        .build()
                )
                .type(DialogType.multiAction(btns, null, 2))
        }
    }

    fun getForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim, role: Claim.ClaimRole): Form {
        val form = CustomForm.builder()
        form.title("Create a Role")
        form.input("Role Name", role.name)
        form.input("Role Color", role.color?.asHexString() ?: "#ffffff")

        ClaimPermissionGroups.entries.forEach { perm ->
            form.toggle(perm.name.replace("_", " ").capitalize(), role.hasPermission(perm))
        }

        form.validResultHandler { form, response ->
            val roleName = response.next<String>() ?: return@validResultHandler
            val roleColor = response.next<String>()?.let { TextColor.fromHexString(it) } ?: TextColor.fromHexString("#ffffff")

            Database.claims.setRoleColor(claim.id, role.name, roleColor?.asHexString() ?: "#FFFFFF")

            for (perm in ClaimPermissionGroups.entries) {
                val setValue = response.next<Boolean>() ?: false
                if (setValue != role.hasPermission(perm)) {
                    if (setValue) {
                        claim.addPermission(role.name, perm)
                    } else {
                        claim.removePermission(role.name, perm)
                    }
                }
            }

            ClaimMenu.openForm(player, floodgatePlayer, claim)
        }

        return form.build()
    }
}
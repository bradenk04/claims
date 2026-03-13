package io.github.bradenk04.claims.menu.claims

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
import org.bukkit.entity.Player

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
}
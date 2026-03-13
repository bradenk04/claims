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
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.geysermc.cumulus.component.ButtonComponent
import org.geysermc.cumulus.form.CustomForm
import org.geysermc.cumulus.form.Form
import org.geysermc.floodgate.api.player.FloodgatePlayer

object ClaimCreateRoleMenu {
    fun getDialog(player: Player, claim: Claim): Dialog {
        val permissionOptions = ClaimPermissionGroups.entries.map {
            val permId = it.name.lowercase()
            DialogInput
                .bool("perm_$permId", Component.text(permId.replace("_", " ").capitalize()))
                .initial(ConfigHandler.config.defaultGuestPermissions.contains(it))
                .onTrue("true")
                .onFalse("false")
                .build()
        }.toTypedArray()

        return Dialog.create {
            it.empty()
                .base(
                    DialogBase
                        .builder(Component.text("${claim.getFormattedClaimName()} Roles: New Role"))
                        .inputs(listOf(
                            DialogInput
                                .text("role_name", Component.text("Role Name"))
                                .maxLength(32)
                                .build(),
                            DialogInput
                                .text("role_color", Component.text("Role Name"))
                                .initial("#FFFFFF")
                                .maxLength(7)
                                .build(),
                            *permissionOptions
                        ))
                        .build()
                )
                .type(DialogType.notice(
                    ActionButton
                        .builder(Component.text("Create Role"))
                        .action(DialogAction.customClick(
                            { view, _ ->
                                val name = view.getText("role_name") ?: return@customClick
                                if (name.trim().isBlank()) {
                                    player.showDialog(ClaimRoleListMenu.getDialog(player, claim))
                                    return@customClick
                                }
                                val color = view.getText("role_color")?.let { net.kyori.adventure.text.format.TextColor.fromHexString(it) }

                                Database.claims.setRoleColor(claim.id, name, color?.asHexString() ?: "#FFFFFF")
                                ClaimPermissionGroups.entries.forEachIndexed { _, perm ->
                                    val permValue = view.getBoolean("perm_${perm.name.lowercase()}") ?: return@forEachIndexed
                                    if (permValue) {
                                        claim.addPermission(name, perm)
                                    }
                                }
                                player.showDialog(ClaimRoleListMenu.getDialog(player, claim))
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
    fun getForm(player: Player, floodgatePlayer: FloodgatePlayer, claim: Claim): Form {
        val form = CustomForm.builder()
        form.title("Create a Role")
        form.input("Role Name", "Role Name")
        form.input("Role Color", "#ffffff")

        ClaimPermissionGroups.entries.forEach { perm ->
            form.toggle(perm.name.replace("_", " ").capitalize(), ConfigHandler.config.defaultGuestPermissions.contains(perm))
        }

        form.validResultHandler { form, response ->
            val roleName = response.next<String>() ?: return@validResultHandler
            val roleColor = response.next<String>()?.let { TextColor.fromHexString(it) } ?: TextColor.fromHexString("#ffffff")

            Database.claims.setRoleColor(claim.id, roleName, roleColor?.asHexString() ?: "#FFFFFF")

            for (perm in ClaimPermissionGroups.entries) {
                val setValue = response.next<Boolean>() ?: false
                if (setValue) claim.addPermission(roleName, perm)
            }

            ClaimMenu.openForm(player, floodgatePlayer, claim)
        }

        return form.build()
    }
}
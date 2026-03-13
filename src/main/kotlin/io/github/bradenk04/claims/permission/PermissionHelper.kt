package io.github.bradenk04.claims.permission

import org.bukkit.Bukkit
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

object PermissionHelper {
    fun initialize() {
        val claimPerm = Permission("claims.claim", PermissionDefault.TRUE)
        Bukkit.getPluginManager().addPermission(claimPerm)

        val claimBypassPerm = Permission("claims.bypass", PermissionDefault.OP)
        Bukkit.getPluginManager().addPermission(claimBypassPerm)
    }
}
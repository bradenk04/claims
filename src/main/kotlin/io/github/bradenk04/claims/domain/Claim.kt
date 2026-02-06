package io.github.bradenk04.claims.domain

import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.database.Database
import net.kyori.adventure.text.format.TextColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class Claim internal constructor(
    val id: Int,
    var owner: UUID,
    var name: String?,
    var description: String?,
    val chunks: MutableSet<ChunkLocation>,
    val bans: Set<UUID> = emptySet(),
    val playerRoles: ConcurrentHashMap<UUID, String> = ConcurrentHashMap(),
    val permissions: ConcurrentHashMap<String, Set<ClaimPermission>> = ConcurrentHashMap(),
    val roleColors: ConcurrentHashMap<String, String> = ConcurrentHashMap()
) {
    fun getFormattedClaimName(): String {
        if (name != null) return name!!
        return "Claim (Chunk ${chunks.minOfOrNull { chunk -> chunk.x }}-${chunks.maxOfOrNull { chunk -> chunk.x }}-${chunks.minOfOrNull { chunk -> chunk.z }}-${chunks.maxOfOrNull { chunk -> chunk.z }})"

    }

    val roles: List<ClaimRole>
        get() {
            val list = mutableListOf<ClaimRole>()
            val ids = permissions.keys.union(roleColors.keys)
            ids.forEach { role ->
                list.add(
                    ClaimRole(
                        id,
                        role,
                        TextColor.fromHexString(roleColors[role] ?: "#FFFFFF"),
                        permissions[role] ?: setOf()
                    )
                )
            }
            return list
        }

    fun hasPermission(player: OfflinePlayer, permission: ClaimPermission): Boolean {
        val role = getPlayerRole(player.uniqueId)
        return getPermission(role, permission)
    }
    fun hasPermission(player: UUID, permission: ClaimPermission): Boolean {
        val role = getPlayerRole(player)
        return getPermission(role, permission)
    }
    fun hasPermission(player: UUID, permission: ClaimPermissionGroups): Boolean {
        val role = getPlayerRole(player)
        var hasPerm = true
        for (perm in permission.groupedPerms) {
            if (!getPermission(role, perm)) hasPerm = false
        }
        return hasPerm
    }

    fun addPermission(role: String, permission: ClaimPermission) {
        Database.claims.addPermission(id, role, permission.name)
    }

    fun addPermission(role: String, permission: ClaimPermissionGroups) {
        for (perm in permission.groupedPerms) {
            Database.claims.addPermission(id, role, perm.name)
        }
    }

    fun removePermission(role: String, permission: ClaimPermission) {
        Database.claims.removePermission(id, role, permission.name)
    }

    fun removePermission(role: String, permission: ClaimPermissionGroups) {
        for (perm in permission.groupedPerms) {
            Database.claims.removePermission(id, role, perm.name)
        }
    }

    fun getRolePermissions(role: String): Set<ClaimPermission> = permissions[role] ?: setOf()

    fun getPermission(role: String, permission: ClaimPermission): Boolean = getRolePermissions(role).contains(permission)

    fun isBanned(player: OfflinePlayer): Boolean = bans.contains(player.uniqueId)

    fun getPlayerRole(player: Player): String {
        return playerRoles[player.uniqueId] ?: if (owner == player.uniqueId) "owner" else "guest"
    }
    fun getPlayerRole(player: UUID): String {
        return playerRoles[player] ?: if (owner == player) "owner" else "guest"
    }

    class ClaimRole(
        val claim: Int,
        val name: String,
        val color: TextColor?,
        val permissions: Set<ClaimPermission>
    )
}

data class ChunkLocation(val world: UUID, val x: Int, val z: Int)
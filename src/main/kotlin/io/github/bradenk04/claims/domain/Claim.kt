package io.github.bradenk04.claims.domain

import org.bukkit.OfflinePlayer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class Claim internal constructor(
    val id: Int,
    val owner: UUID,
    val name: String?,
    val description: String?,
    val chunks: MutableSet<ChunkLocation>,
    val bans: Set<UUID> = emptySet(),
    val roles: ConcurrentHashMap<UUID, String> = ConcurrentHashMap(),
    val permissions: ConcurrentHashMap<String, Set<ClaimPermission>> = ConcurrentHashMap()
) {
    private val defaultGuestPermissions = setOf<ClaimPermission>(
        ClaimPermission.ENTER_REGION
    )
    fun hasPermission(player: OfflinePlayer, permission: ClaimPermission): Boolean {
        val role = roles[player.uniqueId] ?: "guest"
        return getPermission(role, permission)
    }
    fun hasPermission(player: UUID, permission: ClaimPermission): Boolean {
        val role = roles[player] ?: "guest"
        return getPermission(role, permission)
    }

    fun getRolePermissions(role: String): Set<ClaimPermission> = permissions[role] ?: defaultGuestPermissions

    fun getPermission(role: String, permission: ClaimPermission): Boolean = getRolePermissions(role).contains(permission)

    fun isBanned(player: OfflinePlayer): Boolean = bans.contains(player.uniqueId)
}

data class ChunkLocation(val world: UUID, val x: Int, val z: Int)
package io.github.bradenk04.claims.domain

import io.github.bradenk04.claims.config.ConfigHandler
import net.kyori.adventure.text.format.TextColor
import org.bukkit.OfflinePlayer
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
            val ids = permissions.keys.toList()
            ids.forEach { role ->
                list.add(
                    ClaimRole(
                        id,
                        role,
                        TextColor.fromHexString(roleColors[role] ?: "#FFFFFF"),
                        permissions[role] ?: ConfigHandler.config.defaultGuestPermissions
                    )
                )
            }
            return list
        }

    fun hasPermission(player: OfflinePlayer, permission: ClaimPermission): Boolean {
        val role = playerRoles[player.uniqueId] ?: "guest"
        return getPermission(role, permission)
    }
    fun hasPermission(player: UUID, permission: ClaimPermission): Boolean {
        val role = playerRoles[player] ?: "guest"
        return getPermission(role, permission)
    }

    fun getRolePermissions(role: String): Set<ClaimPermission> = permissions[role] ?: ConfigHandler.config.defaultGuestPermissions

    fun getPermission(role: String, permission: ClaimPermission): Boolean = getRolePermissions(role).contains(permission)

    fun isBanned(player: OfflinePlayer): Boolean = bans.contains(player.uniqueId)

    class ClaimRole(
        val claim: Int,
        val name: String,
        val color: TextColor?,
        val permissions: Set<ClaimPermission>
    )
}

data class ChunkLocation(val world: UUID, val x: Int, val z: Int)
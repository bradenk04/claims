package io.github.bradenk04.claims.domain

import org.bukkit.OfflinePlayer
import java.util.UUID

class Claim private constructor(
    val id: UUID,
    val ownerUUID: UUID,
    val name: String? = null,
    val metadata: ClaimMetadata = ClaimMetadata(),
    val pos1: ClaimPos,
    val pos2: ClaimPos
) {
    fun hasPermission(player: OfflinePlayer, permission: ClaimPermission): Boolean {
        val role = metadata.roles[player.uniqueId] ?: "guest"
        return getPermission(role, permission)
    }

    fun getPermission(role: String, permission: ClaimPermission): Boolean = metadata.permissions[role]?.contains(permission) ?: false

    fun isBanned(player: OfflinePlayer): Boolean = metadata.bans.contains(player.uniqueId)
}
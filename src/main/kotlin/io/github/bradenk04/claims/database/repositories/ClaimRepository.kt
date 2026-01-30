package io.github.bradenk04.claims.database.repositories

import io.github.bradenk04.claims.domain.Claim
import io.github.bradenk04.claims.domain.ClaimPermission
import io.github.bradenk04.claims.domain.ClaimPos
import org.bukkit.Location
import java.util.UUID

interface ClaimRepository {
    suspend fun getClaim(uuid: UUID): Claim?
    suspend fun getClaim(loc: Location): Claim?
    suspend fun createClaim(claim: Claim): Boolean
    suspend fun deleteClaim(claim: Claim): Boolean

    suspend fun setOwner(claim: UUID, owner: UUID): Boolean
    suspend fun setName(claim: UUID, name: String?): Boolean
    suspend fun setDescription(claim: UUID, name: String?): Boolean
    suspend fun removePos(claim: UUID, pos1: ClaimPos, pos2: ClaimPos): Boolean
    suspend fun addPos(claim: UUID, pos1: ClaimPos, pos2: ClaimPos): Boolean
    suspend fun addBan(claim: UUID, player: UUID): Boolean
    suspend fun removeBan(claim: UUID, player: UUID): Boolean
    suspend fun setRole(claim: UUID, player: UUID, role: String): Boolean
    suspend fun removeRole(claim: UUID, player: UUID): Boolean
    suspend fun setPermission(claim: UUID, role: String, permission: ClaimPermission): Boolean
    suspend fun removePermission(claim: UUID, role: String, permission: ClaimPermission): Boolean
}
package io.github.bradenk04.claims.database.repositories

import io.github.bradenk04.claims.domain.ChunkLocation
import io.github.bradenk04.claims.domain.Claim
import io.github.bradenk04.claims.domain.ClaimPermission
import io.github.bradenk04.claims.domain.ClaimPos
import org.bukkit.Location
import java.util.UUID

interface ClaimRepository {
    fun initialize()
    fun shutdown()

    fun createClaim(owner: UUID, chunk: ChunkLocation): Claim
    fun getClaim(id: Int): Claim?
    fun getClaim(loc: Location): Claim?
    fun getClaim(loc: ChunkLocation): Claim?
    fun getClaims(locations: List<ChunkLocation>): List<Claim>
    fun getUsersClaims(player: UUID): List<Claim>
    fun saveClaim(claim: Claim): Boolean
    fun deleteClaim(claim: Claim): Boolean
    fun getUsersClaimCount(player: UUID): Int

    fun addChunk(claimId: Int, chunk: ChunkLocation)
    fun removeChunk(claimId: Int, chunk: ChunkLocation)
    fun setRole(claimId: Int, user: UUID, role: String)
    fun deleteRole(claimId: Int, role: String)
    fun addPermission(claimId: Int, role: String, permission: String)
    fun removePermission(claimId: Int, role: String, permission: String)
    fun setRoleColor(claimId: Int, role: String, color: String)
    fun banPlayer(claimId: Int, user: UUID)
}
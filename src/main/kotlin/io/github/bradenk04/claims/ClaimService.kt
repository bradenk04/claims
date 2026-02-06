package io.github.bradenk04.claims

import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.database.repositories.ClaimRepository
import io.github.bradenk04.claims.domain.ChunkLocation
import io.github.bradenk04.claims.domain.Claim
import io.github.bradenk04.claims.domain.ClaimPermission
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class ClaimService(
    val repository: ClaimRepository
) {
    fun claimChunk(claimer: OfflinePlayer, chunk: ChunkLocation): ClaimResult {
        val existing = repository.getClaim(chunk)
        if (existing != null) {
            return ClaimResult.AlreadyClaimed(existing)
        }

        val neighbors = listOf(
            chunk.copy(x = chunk.x + 1),
            chunk.copy(x = chunk.x - 1),
            chunk.copy(z = chunk.z + 1),
            chunk.copy(z = chunk.z - 1),
        )

        val neighborClaims = repository.getClaims(neighbors)

        var parentClaim: Claim? = null

        for (neighbor in neighborClaims) {
            if (neighbor.owner == claimer.uniqueId) {
                parentClaim = neighbor
                break
            }
            if (neighbor.hasPermission(claimer, ClaimPermission.ADD_TO_CLAIM)) {
                parentClaim = neighbor
                break
            }
        }

        return if (parentClaim != null) {
            repository.addChunk(parentClaim.id, chunk)
            Database.claims.getClaim(parentClaim.id)?.let {
                BluemapHelper.registerClaim(it)
            }
            ClaimResult.Success(
                false,
                parentClaim.id,
                chunk
            )
        } else {
            val playersClaims = repository.getUsersClaimCount(claimer.uniqueId)
            // TODO: Implement permission check.
            if (playersClaims > 0 && !claimer.isOp) {
                ClaimResult.NoPermission("Reached max claims", true)
            } else {
                val newClaim = ClaimManager.createClaim(claimer.uniqueId, chunk)
                ClaimResult.Success(true, newClaim.id, chunk)
            }
        }
    }

    sealed class ClaimResult {
        data class Success(val isNewClaim: Boolean, val claimId: Int, val loc: ChunkLocation) : ClaimResult()
        data class AlreadyClaimed(val existingClaim: Claim) : ClaimResult()
        data class NoPermission(val reason: String, val becauseNoMoreClaims: Boolean) : ClaimResult()
        data class InvalidLocation(val reason: String) : ClaimResult()
        data class Error(val reason: String) : ClaimResult()
    }
}
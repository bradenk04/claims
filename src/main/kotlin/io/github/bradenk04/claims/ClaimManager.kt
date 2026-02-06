package io.github.bradenk04.claims

import io.github.bradenk04.claims.BluemapHelper.registerClaim
import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.ChunkLocation
import io.github.bradenk04.claims.domain.Claim
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.UUID

object ClaimManager {
    fun createClaim(owner: UUID, chunk: ChunkLocation): Claim {
        val newClaim = Database.claims.createClaim(owner, chunk)
        registerClaim(newClaim)
        return newClaim
    }

    fun getClaim(pos: Location): Claim? {
        return Database.claims.getClaim(pos)
    }
    fun getClaim(pos: Chunk): Claim? {
        return Database.claims.getClaim(ChunkLocation(pos.world.uid, pos.x, pos.z))
    }
}
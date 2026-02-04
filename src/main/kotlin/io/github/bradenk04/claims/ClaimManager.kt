package io.github.bradenk04.claims

import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.ChunkLocation
import io.github.bradenk04.claims.domain.Claim
import org.bukkit.Chunk
import org.bukkit.Location

object ClaimManager {
    fun getClaim(pos: Location): Claim? {
        return Database.claims.getClaim(pos)
    }
    fun getClaim(pos: Chunk): Claim? {
        return Database.claims.getClaim(ChunkLocation(pos.world.uid, pos.x, pos.z))
    }
}
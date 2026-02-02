package io.github.bradenk04.claims

import io.github.bradenk04.claims.database.Database
import io.github.bradenk04.claims.domain.Claim
import org.bukkit.Location

object ClaimManager {
    fun getClaim(pos: Location): Claim? {
        return Database.claims.getClaim(pos)
    }
}
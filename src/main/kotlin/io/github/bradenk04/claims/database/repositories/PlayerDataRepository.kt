package io.github.bradenk04.claims.database.repositories

import io.github.bradenk04.claims.domain.PlayerData
import java.util.UUID

interface PlayerDataRepository {
    fun initialize()
    fun shutdown()

    fun getPlayerData(uuid: UUID): PlayerData?
    fun savePlayerData(playerData: PlayerData): Boolean
}
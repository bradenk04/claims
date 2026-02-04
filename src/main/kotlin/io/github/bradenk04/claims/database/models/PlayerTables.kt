package io.github.bradenk04.claims.database.models

import org.jetbrains.exposed.v1.core.Table
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object PlayerData : Table("player_data") {
    val uuid = uuid("uuid")
    val username = varchar("username", 32)
    val claimEntranceMessages = bool("claim_entrance_messages")
    val claimEntranceActionBar = bool("claim_entrance_action_bar")

    override val primaryKey = PrimaryKey(uuid)
}
package io.github.bradenk04.claims.database.models

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import kotlin.uuid.ExperimentalUuidApi

object Claims : Table("claims") {
    val id = integer("id").autoIncrement()
    @OptIn(ExperimentalUuidApi::class)
    val ownerUUID = uuid("owner_uuid")
    val name = varchar("name", 128).nullable()
    val description = varchar("description", 128).nullable()
    override val primaryKey = PrimaryKey(id)
}

@OptIn(ExperimentalUuidApi::class)
object ClaimChunks : Table("claim_chunks") {
    val worldUuid = uuid("world_uuid")
    val x = integer("x")
    val z = integer("z")
    val claimId = reference("claim_id", Claims.id, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(worldUuid, x, z)
}

@OptIn(ExperimentalUuidApi::class)
object ClaimRoles : Table("claim_roles") {
    val claimId = reference("claim_id", Claims.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 32)
    val player = uuid("player")
    override val primaryKey = PrimaryKey(claimId, role)
}

object ClaimPermissions : Table("claim_permissions") {
    val claimId = reference("claim_id", Claims.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 32)
    val permission = varchar("permission", 32)
    override val primaryKey = PrimaryKey(claimId, role, permission)
}

object ClaimRoleMetadata : Table("claim_role_metadata") {
    val claimId = reference("claim_id", Claims.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 32)
    val color = varchar("color", 32).nullable()
    override val primaryKey = PrimaryKey(claimId, role)
}

@OptIn(ExperimentalUuidApi::class)
object ClaimBans : Table("claim_bans") {
    val claimId = reference("claim_id", Claims.id, onDelete = ReferenceOption.CASCADE)
    val player = uuid("player")
    override val primaryKey = PrimaryKey(claimId, player)
}
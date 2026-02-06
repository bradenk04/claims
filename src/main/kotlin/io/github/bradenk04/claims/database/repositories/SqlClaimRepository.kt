@file:OptIn(ExperimentalUuidApi::class)

package io.github.bradenk04.claims.database.repositories

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.database.PluginDatabaseConfig
import io.github.bradenk04.claims.database.models.ClaimBans
import io.github.bradenk04.claims.database.models.ClaimChunks
import io.github.bradenk04.claims.database.models.ClaimPermissions
import io.github.bradenk04.claims.database.models.ClaimRoleMetadata
import io.github.bradenk04.claims.database.models.ClaimRoles
import io.github.bradenk04.claims.database.models.Claims
import io.github.bradenk04.claims.domain.ChunkLocation
import io.github.bradenk04.claims.domain.Claim
import io.github.bradenk04.claims.domain.ClaimPermission
import org.bukkit.Location
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsert
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class SqlClaimRepository(
    private val config: PluginDatabaseConfig
) : ClaimRepository {
    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database

    override fun initialize() {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.url
            driverClassName = config.driver
            username = config.username
            password = config.password

            maximumPoolSize = ConfigHandler.config.database.pool.maximumPoolSize
            minimumIdle = 2
            connectionTimeout = ConfigHandler.config.database.pool.connectionTimeout

            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }

        dataSource = HikariDataSource(hikariConfig)
        database = Database.connect(dataSource)
        transaction(database) {
            SchemaUtils.create(Claims, ClaimChunks, ClaimRoles, ClaimPermissions, ClaimBans, ClaimRoleMetadata)
        }
    }

    override fun shutdown() {
        if (::dataSource.isInitialized) dataSource.close()
    }

    override fun createClaim(owner: UUID, chunk: ChunkLocation): Claim = transaction(database) {
        val newId = Claims.insert {
            it[Claims.ownerUUID] = owner.toKotlinUuid()
        }[Claims.id]

        ClaimChunks.insert {
            it[ClaimChunks.claimId] = newId
            it[ClaimChunks.worldUuid] = chunk.world.toKotlinUuid()
            it[ClaimChunks.x] = chunk.x
            it[ClaimChunks.z] = chunk.z
        }

        ClaimPermission.entries.forEach { perm ->
            ClaimPermissions.insertIgnore {
                it[ClaimPermissions.claimId] = newId
                it[ClaimPermissions.role] = "owner"
                it[ClaimPermissions.permission] = perm.toString()
            }
        }

        ClaimRoleMetadata.insertIgnore {
            it[ClaimRoleMetadata.claimId] = newId
            it[ClaimRoleMetadata.role] = "owner"
            it[ClaimRoleMetadata.color] = "#FF0000"
        }

        ConfigHandler.config.defaultGuestPermissions.forEach { perm ->
            perm.groupedPerms.forEach { subPerm ->
                ClaimPermissions.insertIgnore {
                    it[ClaimPermissions.claimId] = newId
                    it[ClaimPermissions.role] = "guest"
                    it[ClaimPermissions.permission] = subPerm.toString()
                }
            }
        }

        ClaimRoleMetadata.insertIgnore {
            it[ClaimRoleMetadata.claimId] = newId
            it[ClaimRoleMetadata.role] = "guest"
            it[ClaimRoleMetadata.color] = "#FFFFFF"
        }

        ClaimRoles.insertIgnore {
            it[ClaimRoles.claimId] = newId
            it[ClaimRoles.player] = owner.toKotlinUuid()
            it[ClaimRoles.role] = "owner"
        }

        Claim(newId, owner, null, null, mutableSetOf(chunk))
    }

    override fun getClaim(id: Int): Claim? = transaction(database) {
        val row = Claims.selectAll().where { Claims.id eq id }.singleOrNull() ?: return@transaction null
        mapRowToClaim(row, id)
    }

    override fun getClaim(loc: Location): Claim? = transaction(database) {
        val claimId = ClaimChunks
            .select(ClaimChunks.claimId)
            .where { (ClaimChunks.worldUuid eq loc.world.uid.toKotlinUuid()) and (ClaimChunks.x eq loc.chunk.x) and (ClaimChunks.z eq loc.chunk.z) }
            .singleOrNull()
            ?.get(ClaimChunks.claimId) ?: return@transaction null

        getClaim(claimId)
    }

    override fun getClaim(loc: ChunkLocation): Claim? = transaction(database) {
        val claimId = ClaimChunks
            .select(ClaimChunks.claimId)
            .where { (ClaimChunks.worldUuid eq loc.world.toKotlinUuid()) and (ClaimChunks.x eq loc.x) and (ClaimChunks.z eq loc.z) }
            .singleOrNull()
            ?.get(ClaimChunks.claimId) ?: return@transaction null

        getClaim(claimId)
    }

    override fun getClaims(locations: List<ChunkLocation>): List<Claim> = transaction(database) {
        if (locations.isEmpty()) return@transaction emptyList()

        val condition = locations
            .map { (ClaimChunks.worldUuid eq it.world.toKotlinUuid()) and (ClaimChunks.x eq it.x) and (ClaimChunks.z eq it.z) }
            .reduce { acc, next -> acc or next }

        val claimIds = ClaimChunks
            .select(ClaimChunks.claimId)
            .where { condition }
            .map { it[ClaimChunks.claimId] }
            .distinct()

        if (claimIds.isEmpty()) return@transaction emptyList()

        Claims.selectAll()
            .where { Claims.id inList claimIds }
            .map { mapRowToClaim(it, it[Claims.id]) }
    }

    override fun getUsersClaims(player: UUID): List<Claim>  = transaction(database) {
        Claims.selectAll()
            .where { Claims.ownerUUID eq player.toKotlinUuid() }
            .map { row -> mapRowToClaim(row, row[Claims.id]) }
    }

    override fun saveClaim(claim: Claim): Boolean = transaction(database) {
        Claims.update({ Claims.id eq claim.id }) {
            it[Claims.ownerUUID] = claim.owner.toKotlinUuid()
            it[Claims.name] = claim.name
            it[Claims.description] = claim.description
        }
        return@transaction true
    }

    override fun deleteClaim(claim: Claim): Boolean {
        Claims.deleteWhere { Claims.id eq claim.id }
        return true
    }

    override fun getUsersClaimCount(player: UUID): Int = transaction(database) {
        Claims.selectAll()
            .where { Claims.ownerUUID eq player.toKotlinUuid() }
            .count().toInt()
    }

    override fun addChunk(claimId: Int, chunk: ChunkLocation) = transaction(database) {
        try {
            ClaimChunks.insert {
                it[ClaimChunks.claimId] = claimId
                it[ClaimChunks.worldUuid] = chunk.world.toKotlinUuid()
                it[ClaimChunks.x] = chunk.x
                it[ClaimChunks.z] = chunk.z
            }
        } catch (_: Exception) {
        }
        Unit
    }

    override fun removeChunk(claimId: Int, chunk: ChunkLocation) {
        ClaimChunks.deleteWhere {
            (ClaimChunks.worldUuid eq chunk.world.toKotlinUuid()) and (x eq chunk.x) and (z eq chunk.z)
        }
    }

    override fun setRole(claimId: Int, user: UUID, role: String) {
        ClaimRoles.deleteWhere {
            (ClaimRoles.claimId eq claimId) and (ClaimRoles.player eq user.toKotlinUuid())
        }

        ClaimRoles.insert {
            it[ClaimRoles.claimId] = claimId
            it[ClaimRoles.player] = user.toKotlinUuid()
            it[ClaimRoles.role] = role
        }
    }

    override fun deleteRole(claimId: Int, role: String) = transaction(database) {
        ClaimRoleMetadata.deleteWhere { (ClaimRoleMetadata.claimId eq claimId) and (ClaimRoleMetadata.role eq role) }
        ClaimPermissions.deleteWhere { (ClaimPermissions.claimId eq claimId) and (ClaimPermissions.role eq role) }
        Unit
    }

    override fun addPermission(claimId: Int, role: String, permission: String) = transaction(database) {
        ClaimPermissions.insertIgnore {
            it[ClaimPermissions.claimId] = claimId
            it[ClaimPermissions.role] = role
            it[ClaimPermissions.permission] = permission
        }
        Unit
    }

    override fun removePermission(claimId: Int, role: String, permission: String) = transaction(database) {
        ClaimPermissions.deleteWhere { (ClaimPermissions.claimId eq claimId) and (ClaimPermissions.role eq role) and (ClaimPermissions.permission eq permission) }
        Unit
    }

    override fun setRoleColor(claimId: Int, role: String, color: String) = transaction(database) {
        ClaimRoleMetadata.upsert {
            it[ClaimRoleMetadata.claimId] = claimId
            it[ClaimRoleMetadata.role] = role
            it[ClaimRoleMetadata.color] = color
        }
        Unit
    }

    override fun banPlayer(claimId: Int, user: UUID) = transaction(database) {
        val exists = ClaimBans.selectAll()
            .where { (ClaimBans.claimId eq claimId) and (ClaimBans.player eq user.toKotlinUuid()) }
            .count() > 0

        if (!exists) {
            ClaimBans.insert {
                it[ClaimBans.claimId] = claimId
                it[ClaimBans.player] = user.toKotlinUuid()
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun mapRowToClaim(row: ResultRow, claimId: Int): Claim {
        return Claim(
            id = claimId,
            owner = row[Claims.ownerUUID].toJavaUuid(),
            name = row[Claims.name],
            description = row[Claims.description],
            chunks = fetchChunks(claimId),
            playerRoles = fetchRoles(claimId),
            permissions = fetchPermissions(claimId),
            bans = fetchBans(claimId),
            roleColors = ConcurrentHashMap(fetchRoleMetadata(claimId).associate {
                it.name to it.color
            })
        )
    }


    private fun fetchChunks(id: Int): MutableSet<ChunkLocation> {
        return ClaimChunks.selectAll()
            .where { ClaimChunks.claimId eq id }
            .map { ChunkLocation(it[ClaimChunks.worldUuid].toJavaUuid(), it[ClaimChunks.x], it[ClaimChunks.z]) }
            .toMutableSet()
    }

    private fun fetchRoles(id: Int): ConcurrentHashMap<UUID, String> {
        return ConcurrentHashMap(ClaimRoles.selectAll()
            .where { ClaimRoles.claimId eq id }
            .associate { it[ClaimRoles.player].toJavaUuid() to it[ClaimRoles.role] })
    }

    private fun fetchPermissions(id: Int): ConcurrentHashMap<String, Set<ClaimPermission>> {
        val map = mutableMapOf<String, MutableSet<ClaimPermission>>()

        ClaimPermissions.selectAll()
            .where { ClaimPermissions.claimId eq id }
            .forEach { row ->
                val role = row[ClaimPermissions.role]
                val perm = ClaimPermission.valueOf(row[ClaimPermissions.permission].uppercase())
                map.getOrPut(role) { mutableSetOf() }.add(perm)
            }
        return ConcurrentHashMap(map.mapValues { it.value.toSet() })
    }

    private fun fetchRoleMetadata(claim: Int): Set<RoleMetadata> {
        return ClaimRoleMetadata.selectAll().where {
            (ClaimRoleMetadata.claimId eq claim)
        }.map {
            RoleMetadata(
                name = it[ClaimRoleMetadata.role],
                claim = claim,
                color = it[ClaimRoleMetadata.color]
            )
        }.toSet()
    }

    private data class RoleMetadata(val claim: Int, val name: String, val color: String?)

    private fun fetchBans(id: Int): MutableSet<UUID> {
        return ClaimBans.selectAll()
            .where { ClaimBans.claimId eq id }
            .map { it[ClaimBans.player].toJavaUuid() }
            .toMutableSet()
    }
}
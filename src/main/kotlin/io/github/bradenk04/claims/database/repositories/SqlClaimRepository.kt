@file:OptIn(ExperimentalUuidApi::class)

package io.github.bradenk04.claims.database.repositories

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.database.PluginDatabaseConfig
import io.github.bradenk04.claims.database.models.ClaimBans
import io.github.bradenk04.claims.database.models.ClaimChunks
import io.github.bradenk04.claims.database.models.ClaimPermissions
import io.github.bradenk04.claims.database.models.ClaimRoles
import io.github.bradenk04.claims.database.models.Claims
import io.github.bradenk04.claims.domain.ChunkLocation
import io.github.bradenk04.claims.domain.Claim
import io.github.bradenk04.claims.domain.ClaimPermission
import org.bukkit.Location
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
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
            SchemaUtils.create(Claims, ClaimChunks, ClaimRoles, ClaimPermissions, ClaimBans)
        }
    }

    override fun shutdown() {
        if (::dataSource.isInitialized) dataSource.close()
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

    override fun getUsersClaims(player: UUID): List<Claim>  = transaction(database) {
        Claims.selectAll()
            .where { Claims.ownerUUID eq player.toKotlinUuid() }
            .map { row -> mapRowToClaim(row, row[Claims.id]) }
    }

    override fun saveClaim(claim: Claim): Boolean {
        Claims.update({ Claims.id eq claim.id }) {
            it[Claims.ownerUUID] = claim.owner.toKotlinUuid()
            it[Claims.name] = claim.name
            it[Claims.description] = claim.description
        }
        return true
    }

    override fun deleteClaim(claim: Claim): Boolean {
        Claims.deleteWhere { Claims.id eq claim.id }
        return true
    }

    override fun addChunk(claimId: Int, chunk: ChunkLocation) {
        try {
            ClaimChunks.insert {
                it[ClaimChunks.claimId] = claimId
                it[ClaimChunks.worldUuid] = chunk.world.toKotlinUuid()
                it[ClaimChunks.x] = chunk.x
                it[ClaimChunks.z] = chunk.z
            }
        } catch (_: Exception) {
        }
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

    override fun addPermission(claimId: Int, role: String, permission: String) = transaction(database) {
        val exists = ClaimPermissions.selectAll()
        .where { (ClaimPermissions.claimId eq claimId) and (ClaimPermissions.role eq role) and (ClaimPermissions.permission eq permission) }
        .count() > 0

        if (!exists) {
            ClaimPermissions.insert {
                it[ClaimPermissions.claimId] = claimId
                it[ClaimPermissions.role] = role
                it[ClaimPermissions.permission] = permission
            }
        }
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
            roles = fetchRoles(claimId),
            permissions = fetchPermissions(claimId),
            bans = fetchBans(claimId)

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

    private fun fetchBans(id: Int): MutableSet<UUID> {
        return ClaimBans.selectAll()
            .where { ClaimBans.claimId eq id }
            .map { it[ClaimBans.player].toJavaUuid() }
            .toMutableSet()
    }
}
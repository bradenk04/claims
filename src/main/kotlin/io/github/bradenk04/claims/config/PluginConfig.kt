package io.github.bradenk04.claims.config

import io.github.bradenk04.claims.domain.ClaimPermissionGroups
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginConfig(
    val database: DatabaseConfig = DatabaseConfig("sqlite"),
    @SerialName("defaultGuestPermissions")
    private val defaultGuestPermissionsConfig: Set<String> = setOf(
        "ENTER_CLAIM"
    ),
    @SerialName("defaultTrustedPermissions")
    private val defaultTrustedPermissionsConfig: Set<String> = setOf(
        "ENTER_CLAIM",
        "MODIFY_BLOCKS",
        "DROPPED_ITEMS",
        "BLOCK_INTERACTION"
    ),
    val language: String = "en_US",
    val claimSettings: ClaimSettings = ClaimSettings()
) {

    val defaultGuestPermissions: Set<ClaimPermissionGroups>
        get() = defaultGuestPermissionsConfig.map { ClaimPermissionGroups.valueOf(it) }.toSet()
    val defaultTrustedPermissions: Set<ClaimPermissionGroups>
        get() = defaultTrustedPermissionsConfig.map { ClaimPermissionGroups.valueOf(it) }.toSet()
}
@Serializable
data class DatabaseConfig(
    val type: String,
    val mysql: MysqlConfig = MysqlConfig(),
    val pool: PoolConfig = PoolConfig() // Default values if missing
)

@Serializable
data class MysqlConfig(
    val host: String = "localhost",
    val port: Int = 3306,
    val database: String = "claims",
    val username: String = "root",
    val password: String = ""
)

@Serializable
data class PoolConfig(
    val maximumPoolSize: Int = 10,
    val connectionTimeout: Long = 30000
)

@Serializable
data class ClaimSettings(
    var defaultMaxClaims: Int = 1,
    var showEnterRegionMessage: Boolean = true,
    var showEnterRegionActionBar: Boolean = true,
    var showLeftRegionMessage: Boolean = true,
    var showLeftRegionActionBar: Boolean = true
)
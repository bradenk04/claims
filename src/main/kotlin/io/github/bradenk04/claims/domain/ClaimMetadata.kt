package io.github.bradenk04.claims.domain

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class ClaimMetadata(
    val name: String? = null,
    val description: String? = null,
    val bans: Set<UUID> = emptySet(),
    val roles: ConcurrentHashMap<UUID, String> = ConcurrentHashMap(),
    val permissions: ConcurrentHashMap<String, Set<ClaimPermission>> = ConcurrentHashMap()
)

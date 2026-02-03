package io.github.bradenk04.claims.domain

import java.util.UUID

class PlayerData(
    val id: UUID,
    val username: String

) {

    data class Settings(
        var claimEntranceMessages: Boolean,
        var claimEntranceActionBar: Boolean,

    )
}

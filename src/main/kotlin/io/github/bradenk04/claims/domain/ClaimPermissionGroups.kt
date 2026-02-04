package io.github.bradenk04.claims.domain

enum class ClaimPermissionGroups {
    MODIFY_CLAIM {
        override val groupedPerms: Set<ClaimPermission> = setOf(
            ClaimPermission.ADD_TO_CLAIM
        )
    },
    DROPPED_ITEMS {
        override val groupedPerms: Set<ClaimPermission> = setOf(
            ClaimPermission.DROP_ITEMS,
            ClaimPermission.PICKUP_ITEMS
        )
    },
    ENTER_CLAIM {
        override val groupedPerms: Set<ClaimPermission> = setOf(
            ClaimPermission.ENTER_REGION
        )
    },
    MODIFY_BLOCKS {
        override val groupedPerms: Set<ClaimPermission> = setOf(
            ClaimPermission.BREAK_BLOCKS,
            ClaimPermission.PLACE_BLOCKS
        )
    },
    BLOCK_INTERACTION {
        override val groupedPerms: Set<ClaimPermission> = setOf(
            ClaimPermission.CLICK_BUTTONS,
            ClaimPermission.MODIFY_ARMOR_STANDS,
            ClaimPermission.USE_LEVERS,
            ClaimPermission.USE_PRESSURE_PLATES,
            ClaimPermission.OPEN_DOORS,
            ClaimPermission.OPEN_TRAPDOORS,
            ClaimPermission.OPEN_FENCE_GATES,
            ClaimPermission.TRIGGER_TRIPWIRE,
            ClaimPermission.RING_BELL,
            ClaimPermission.GENERIC_INTERACT
        )
    };
    abstract val groupedPerms: Set<ClaimPermission>
}
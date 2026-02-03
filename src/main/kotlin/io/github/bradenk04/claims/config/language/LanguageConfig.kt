package io.github.bradenk04.claims.config.language

import com.akuleshov7.ktoml.annotations.TomlComments
import kotlinx.serialization.Serializable

@Serializable
data class LanguageConfig(
    @TomlComments(
        "Claim related messages, MiniMessage format supported.",
        "You can view minimessage syntax here: https://docs.papermc.io/adventure/minimessage/format/"
    )
    var claimLanguage: ClaimLanguage = ClaimLanguage()
)

@Serializable
data class ClaimLanguage(
    @TomlComments(
        "Hover lines for claim placeholder in chat.",
        "Valid Placeholders: <claim_name>, <claim_owner>, <claim_description> (multiline), <claim_id>"
    )
    var claimHoverLines: List<String> = listOf(
        "Claim: <claim_name>",
        "Owner: <claim_owner>",
        "Description: <claim_description>"
    ),
    var newLandClaimed: String = "You have claimed new land!",
    var claimAddedLand: String = "Added chunk to existing claim!",
    var alreadyOwned: String = "You already own this claim!",
    var alreadyClaimed: String = "This chunk is already claimed!",
    var invalidLocation: String = "You cannot claim this chunk!",
    var noPermission: String = "You do not have permission to do this!",
    var maxClaimsReached: String = "You have reached the maximum number of claims!",
    var enteredRegionMessage: String = "<white>You have entered </white><green><claim></green><white>.",
    var enteredRegionActionBar: String? = "<white>You have entered </white><green><claim></green><white>.",
    var leftRegionMessage: String = "<white>You have left </white><green><claim></green><white>.",
    var leftRegionActionBar: String? = "<white>You have left </white><green><claim></green><white>.",
    var somethingWentWrongClaiming: String = "<red>Something went wrong while claiming this chunk!",
    @TomlComments("Permission related messages. Valid placeholders are <claim_name>")
    var permissions: ClaimPermissionLanguage = ClaimPermissionLanguage()
)

@Serializable
data class ClaimPermissionLanguage(
    var banned: String = "You are banned from this region!",
    var cantEnterRegion: String = "You do not have permission to enter this region!",
    var cantPlaceBlocks: String = "You do not have permission to place blocks in this region!",
    var cantBreakBlocks: String = "You do not have permission to break blocks in this region!",
    var cantDropItems: String = "You do not have permission to drop items in this region!",
    var cantInteract: String = "You do not have permission to interact with this region!",
    var cantPickupItems: String = "You do not have permission to pickup items in this region!",
)
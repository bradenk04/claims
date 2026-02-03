package io.github.bradenk04.claims

import io.github.bradenk04.claims.config.ConfigHandler
import io.github.bradenk04.claims.domain.Claim
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit

object LanguageService {
    private var mm = MiniMessage.miniMessage()

    fun parseMessageWithClaim(message: String, claim: Claim): Component {
        var lines: Component? = null
        val claimOwner = Bukkit.getOfflinePlayer(claim.owner)
        for (line in ConfigHandler.language.claimLanguage.claimHoverLines) {
            val line = mm.deserialize(
                line,
                Placeholder.unparsed("claim_name", claim.getFormattedClaimName()),
                Placeholder.parsed("claim_description", claim.description ?: "N/A"),
                Placeholder.unparsed("claim_owner", claimOwner.playerProfile.name ?: "Unknown"),
                Placeholder.unparsed("claim_id", claim.id.toString())
            )
            lines = lines?.append(line) ?: line
        }
        var claimComponent = Component.text(claim.getFormattedClaimName())
        if (lines != null) claimComponent = claimComponent.hoverEvent(HoverEvent.showText(lines))
        return mm.deserialize(message, Placeholder.component("claim", claimComponent))
    }
}
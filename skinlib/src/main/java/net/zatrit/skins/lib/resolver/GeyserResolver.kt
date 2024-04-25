package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.util.*
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.Base64

private const val GEYSER_XUID_API = "https://api.geysermc.org/v2/xbox/xuid/"
private const val GEYSER_SKIN_API = "https://api.geysermc.org/v2/skin/"

/**
 * An implementation of the GeyserMC skin API based on
 * [CustomPlayerHeads](https://github.com/onebeastchris/customplayerheads).
 */
class GeyserResolver(
    private val floodgatePrefixes: Collection<String>) : Resolver {

    override fun requiresUuid() = false

    override fun resolve(profile: Profile): PlayerTextures {
        var name = profile.name
        val prefix = floodgatePrefixes.first { name.startsWith(it) }

        name = name.substring(prefix.length)
        val xuidUrl = URL(GEYSER_XUID_API + name)
        val xuid = xuidUrl.openStream().jsonObject.getInt("xuid")
        val skinUrl = URL(GEYSER_SKIN_API + xuid)

        /* value contains literally the same data as properties[0] in the
         Mojang API response, so it can be decoded in the same way */
        val response = skinUrl.openStream().jsonObject.getString("value")

        val decoder = Base64.getDecoder()
        val textureData = decoder.decode(response)
        val bytesStream = ByteArrayInputStream(textureData)

        return PlayerTextures(loadTextureMap(bytesStream.jsonObject))
    }
}

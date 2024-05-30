package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.api.Resolver
import net.zatrit.skins.lib.util.jsonObject
import net.zatrit.skins.lib.util.loadTextureMap
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.Base64

private const val GEYSER_XUID_API = "https://api.geysermc.org/v2/xbox/xuid/"
private const val GEYSER_SKIN_API = "https://api.geysermc.org/v2/skin/"

/**
 * An implementation of the GeyserMC skin API based on
 * [CustomPlayerHeads](https://github.com/onebeastchris/customplayerheads).
 */
class GeyserResolver : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val xuidUrl = URL(GEYSER_XUID_API + profile.name)
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

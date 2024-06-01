package zatrit.skins.lib.resolver

import zatrit.skins.lib.PlayerTextures
import zatrit.skins.lib.api.*
import zatrit.skins.lib.data.MojangResponse
import zatrit.skins.lib.util.*
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.Base64

private const val MOJANG_SKIN_API =
    "https://sessionserver.mojang.com/session/minecraft/profile/"

/**
 * [Mojang API](https://wiki.vg/Mojang_API) implementation
 * for skinlib. */
class MojangResolver : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = MOJANG_SKIN_API + profile.shortId

        val stream = URL(url).openStream()
        val response = MojangResponse(stream.jsonObject)
        val decoder = Base64.getDecoder()

        val textureData = decoder.decode(response.properties[0].value)
        val bytesStream = ByteArrayInputStream(textureData)

        return PlayerTextures(
          loadTextureMap(bytesStream.jsonObject.getJSONObject("textures"))
        )
    }
}

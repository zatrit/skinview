package net.zatrit.skins.lib.resolver

import lombok.AllArgsConstructor
import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.MojangResponse
import net.zatrit.skins.lib.util.*
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.Base64

private const val MOJANG_SKIN_API =
    "https://sessionserver.mojang.com/session/minecraft/profile/"

/**
 * [Mojang API](https://wiki.vg/Mojang_API) implementation
 * for OpenMCSkins.
 */
@AllArgsConstructor
class MojangResolver(private val config: Config) : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = MOJANG_SKIN_API + profile.shortId

        val stream = URL(url).openStream()
        val response = MojangResponse().apply { loadJson(stream.jsonObject) }
        val decoder = Base64.getDecoder()

        val textureData = decoder.decode(
            response.properties[0].value
        )

        val bytesStream = ByteArrayInputStream(textureData)

        return BasePlayerTextures(
            loadTextureMap(
                bytesStream.jsonObject.getJSONObject("textures")
            ), config.layers
        )
    }
}

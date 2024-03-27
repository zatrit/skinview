package net.zatrit.skins.lib.resolver.capes

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.texture.BytesTexture
import net.zatrit.skins.lib.util.jsonObject
import java.net.URL
import java.util.*

private const val FIVEZIG_API = "https://textures.5zigreborn.eu/profile/"

class FiveZigResolver(private val config: Config) : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = FIVEZIG_API + profile.id
        val stream = URL(url).openStream()

        val textures = EnumMap<TextureType, BytesTexture>(
            TextureType::class.java
        )

        val textureData = stream.jsonObject["d"] as String?

        if (textureData != null) {
            val decoder = Base64.getDecoder()
            val texture = BytesTexture(
                textureData, decoder.decode(textureData), null
            )
            textures[TextureType.CAPE] = texture
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return BasePlayerTextures(textures, config.layers)
    }
}

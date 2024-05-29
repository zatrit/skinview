package net.zatrit.skins.lib.resolver.capes

import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.TextureType
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.api.Resolver
import net.zatrit.skins.lib.texture.BytesTexture
import net.zatrit.skins.lib.util.jsonObject
import java.net.URL
import java.util.Base64
import java.util.EnumMap

private const val FIVEZIG_API = "https://textures.5zigreborn.eu/profile/"

class FiveZigResolver : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = FIVEZIG_API + profile.id
        val stream = URL(url).openStream()

        val textures = EnumMap<TextureType, BytesTexture>(
          TextureType::class.java
        )

        val textureData: String? = stream.jsonObject.optString("d")

        if (textureData != null) {
            val decoder = Base64.getDecoder()
            val texture = BytesTexture(decoder.decode(textureData), null)
            textures[TextureType.CAPE] = texture
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return PlayerTextures(textures)
    }
}

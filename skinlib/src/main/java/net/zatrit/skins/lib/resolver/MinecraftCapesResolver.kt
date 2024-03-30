package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.*
import net.zatrit.skins.lib.texture.BytesTexture
import net.zatrit.skins.lib.util.jsonObject
import java.net.URL
import java.util.*


private const val MINECRAFTCAPES_API = "https://api.minecraftcapes.net/profile/"

/**
 * Resolver for [Minecraft Capes](https://minecraftcapes.net/)
 * based on the behavior of the Minecraft Capes mod. Connects to the API at
 * `https://api.minecraftcapes.net/`
 *
 *
 * Does not cache skins, because connecting to API already loads textures.
 */
class MinecraftCapesResolver : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = MINECRAFTCAPES_API + profile.shortId

        val stream = URL(url).openStream()
        val response = MCCapesResponse().apply { loadJson(stream.jsonObject) }
        val textures = EnumMap<TextureType, Texture>(TextureType::class.java)

        for (entry in response.textures.entries) {
            val type = entry.key
            val textureData = entry.value ?: continue
            val metadata = Metadata()
            val decoder = Base64.getDecoder()

            if (type === TextureType.CAPE) {
                metadata.isAnimated = response.animatedCape
            }

            val texture = BytesTexture(
                textureData, decoder.decode(textureData), metadata
            )

            textures[type] = texture
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return BasePlayerTextures(textures)
    }
}

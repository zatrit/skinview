package zatrit.skins.lib.resolver

import zatrit.skins.lib.*
import zatrit.skins.lib.api.*
import zatrit.skins.lib.data.*
import zatrit.skins.lib.texture.BytesTexture
import zatrit.skins.lib.util.jsonObject
import java.net.URL
import java.util.*

private const val MINECRAFTCAPES_API = "https://api.minecraftcapes.net/profile/"

/**
 * Resolver for [Minecraft Capes](https://minecraftcapes.net/)
 * based on the behavior of the Minecraft Capes mod. Connects to the API at
 * [MINECRAFTCAPES_API].
 */
class MinecraftCapesResolver : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = MINECRAFTCAPES_API + profile.shortId

        val stream = URL(url).openStream()
        val response = MCCapesResponse(stream.jsonObject)
        val textures = EnumMap<TextureType, Texture>(TextureType::class.java)

        for (entry in response.textures.entries) {
            val type = entry.key
            val textureData = entry.value.takeIf { it != "null" } ?: continue

            val metadata = Metadata()
            val decoder = Base64.getDecoder()

            if (type === TextureType.CAPE) {
                metadata.isAnimated = response.animatedCape
            }

            val texture = BytesTexture(decoder.decode(textureData), metadata)
            textures[type] = texture
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return PlayerTextures(textures)
    }
}

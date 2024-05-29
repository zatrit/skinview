package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.TextureType
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.api.Resolver
import net.zatrit.skins.lib.api.Texture
import net.zatrit.skins.lib.data.MCCapesResponse
import net.zatrit.skins.lib.data.Metadata
import net.zatrit.skins.lib.texture.BytesTexture
import net.zatrit.skins.lib.util.jsonObject
import java.net.URL
import java.util.Base64
import java.util.EnumMap


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

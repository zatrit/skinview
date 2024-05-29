package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.texture.URLTexture
import java.net.URL
import java.util.*

/** An abstract implementation of [Resolver] that creates
 * [PlayerTextures] with a single [URLTexture] by substitution via [getUrl]. */
abstract class DirectResolver(private val type: TextureType) : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val textures = EnumMap<TextureType, Texture>(TextureType::class.java)
        textures[type] = downloadTexture(type, profile)

        return PlayerTextures(textures)
    }

    private fun downloadTexture(type: TextureType, profile: Profile): Texture {
        val url = getUrl(type, profile.id, profile.name, profile.shortId)
        return URLTexture(url, null)
    }

    /** Creates a [URL] from the data. */
    abstract fun getUrl(
      type: TextureType, id: UUID, name: String, shortId: String): String
}

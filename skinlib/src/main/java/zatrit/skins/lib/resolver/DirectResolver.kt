package zatrit.skins.lib.resolver

import zatrit.skins.lib.*
import zatrit.skins.lib.api.*
import zatrit.skins.lib.texture.URLTexture
import java.net.URL
import java.util.*

/** An abstract implementation of [Resolver] that creates
 * [PlayerTextures] with a single [URLTexture] by substitution via [getUrl]. */
abstract class DirectResolver : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val textures = PlayerTextures()
        populateTextures(textures, downloadTexture(profile))

        return textures
    }

    private fun downloadTexture(profile: Profile): Texture {
        val url = getUrl(profile.id, profile.name, profile.shortId)
        return URLTexture(url, null)
    }

    /** Creates a [URL] from the data. */
    abstract fun getUrl(id: UUID, name: String, shortId: String): String

    open fun populateTextures(textures: PlayerTextures, texture: Texture) {
        textures.cape = texture
    }
}

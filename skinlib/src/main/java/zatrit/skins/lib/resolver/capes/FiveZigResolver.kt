package zatrit.skins.lib.resolver.capes

import zatrit.skins.lib.PlayerTextures
import zatrit.skins.lib.api.*
import zatrit.skins.lib.texture.BytesTexture
import zatrit.skins.lib.util.jsonObject
import java.net.URL
import java.util.Base64

private const val FIVEZIG_API = "https://textures.5zigreborn.eu/profile/"

/** A simple [Resolver] implementation for loading capes from [5zig](https://5zigreborn.eu/) textures server. */
class FiveZigResolver : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = FIVEZIG_API + profile.id
        val stream = URL(url).openStream()

        val textures = PlayerTextures()
        val textureData: String? = stream.jsonObject.optString("d")

        if (textureData != null) {
            val decoder = Base64.getDecoder()
            val texture = BytesTexture(decoder.decode(textureData), null)
            textures.cape = texture
        }

        /* Since you can't resolve a list of textures without
        fetching those textures, they may not be cached */
        return textures
    }
}

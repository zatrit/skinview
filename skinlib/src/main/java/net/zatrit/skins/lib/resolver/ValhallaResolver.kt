package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.BasePlayerTextures
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.util.*
import java.net.URL

/**
 * Valhalla Skin Server Resolver.
 * Read more [here](https://skins.minelittlepony-mod.com/docs).
 */
class ValhallaResolver(private val baseUrl: String) : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = baseUrl + profile.id
        val stream = URL(url).openStream()

        return BasePlayerTextures(
            loadTextureMap(stream.jsonObject.getJSONObject("textures"))
        )
    }
}

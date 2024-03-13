package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.util.*
import java.net.URL

/**
 * Valhalla Skin Server Resolver.
 * Read more [here](https://skins.minelittlepony-mod.com/docs).
 */
class ValhallaResolver(
    private val config: Config, private val baseUrl: String) : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val url = baseUrl + profile.getId()
        val stream = URL(url).openStream()

        return BasePlayerTextures(
            loadTextureMap(
                stream.jsonObject.getJSONObject("textures")
            ), config.layers
        )
    }
}

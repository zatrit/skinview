package zatrit.skins.lib.resolver

import zatrit.skins.lib.*
import zatrit.skins.lib.api.*
import zatrit.skins.lib.util.*
import java.io.IOException
import java.net.URL

/**
 * [ely.by API](https://docs.ely.by/en/skins-system.html) implementation
 * for skinlib. Works for some other APIs.
 */
class NamedHTTPResolver(private val baseUrl: String) : Resolver {
    @Throws(IOException::class)
    override fun resolve(profile: Profile): PlayerTextures {
        val url = URL(baseUrl + profile.name)
        val stream = url.openStream()

        return loadTextures(stream.jsonObject)
    }
}

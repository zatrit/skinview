package zatrit.skins.lib.resolver.capes

import zatrit.skins.lib.api.Profile
import zatrit.skins.lib.api.Resolver
import zatrit.skins.lib.util.jsonObject
import java.net.URL

private const val CAPES_URL = "https://www.wurstclient.net/api/v1/capes.json"

/**
 * Implementation of [Resolver] for [Wurst Client](https://www.wurstclient.net/)
 * based on [CapesListResolver]. */
class WurstResolver : CapesListResolver() {
    override fun fetchList(): Map<String, String> {
        val obj = URL(CAPES_URL).openStream().jsonObject
        val map = mutableMapOf<String, String>()

        for (key in obj.keys()) map[key] = obj.getString(key)

        return map
    }

    override fun getUrl(capeName: String) = capeName

    override fun getCapeName(profile: Profile): String = owners!!.getOrDefault(
      profile.name, owners!![profile.id.toString()]
    )
}

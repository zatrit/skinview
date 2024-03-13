package net.zatrit.skins.lib.resolver.capes

import net.zatrit.skins.lib.Config
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.resolver.CapesListResolver
import net.zatrit.skins.lib.util.jsonObject
import java.net.URL

private const val CAPES_URL = "https://www.wurstclient.net/api/v1/capes.json"

class WurstResolver(config: Config) : CapesListResolver(config) {
    override fun fetchList(): Map<String, String> {
        val obj = URL(CAPES_URL).openStream().jsonObject
        val map = mutableMapOf<String, String>()

        for (key in obj.keys()) {
            map[key] = obj.getString(key)
        }

        return map
    }

    override fun getUrl(capeName: String) = capeName

    override fun getCapeName(profile: Profile): String = owners!!.getOrDefault(
        profile.getName(), owners!![profile.getId().toString()]
    )
}

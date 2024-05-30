package zatrit.skins.lib.resolver.capes

import zatrit.skins.lib.api.Resolver
import zatrit.skins.lib.util.jsonArray
import java.net.URL

private const val BASE_URL = "http://capes.liquidbounce.net/api/v1/cape"
private const val CARRIERS_URL = "$BASE_URL/carriers"
private const val NAME_URL = "$BASE_URL/name/"

/**
 * Implementation of [Resolver] for [LiquidBounce](https://liquidbounce.net/)
 * based on [CapesListResolver]. */
class LiquidBounceResolver : CapesListResolver() {
    override fun fetchList(): Map<String, String> {
        val array = URL(CARRIERS_URL).openStream().jsonArray
        val uuidOwners = HashMap<String, String>()

        for (i in 0..<array.length()) {
            val pair = array.optJSONArray(i)

            uuidOwners[pair.optString(0)] = pair.optString(1)
        }

        return uuidOwners
    }

    override fun getUrl(capeName: String) = NAME_URL + capeName
}

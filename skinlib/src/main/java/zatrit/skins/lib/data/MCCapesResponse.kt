package zatrit.skins.lib.data

import org.jetbrains.annotations.ApiStatus.Internal
import org.json.JSONObject

/**
 * An API response from the Minecraft Capes
 * server containing player-related fields.
 */
@Internal
class MCCapesResponse(json: JSONObject) {
    val animatedCape = json.getBoolean("animatedCape")
    val textures = HashMap<String, String>(3)

    init {
        val texturesJson = json.getJSONObject("textures")
        for (key in texturesJson.keys()) {
            textures[key] = texturesJson.getString(key)
        }
    }
}

package net.zatrit.skins.lib.data

import net.zatrit.skins.lib.TextureType
import net.zatrit.skins.lib.util.*
import org.jetbrains.annotations.ApiStatus.Internal
import org.json.JSONObject
import java.util.EnumMap

/**
 * An API response from the Minecraft Capes
 * server containing player-related fields.
 */
@Internal
class MCCapesResponse : LoadJson {
    var animatedCape = false
        private set
    val textures = EnumMap<_, String>(TextureType::class.java)

    override fun loadJson(json: JSONObject) {
        animatedCape = json.getBoolean("animatedCape")

        val texturesJson = json.getJSONObject("textures")
        for (key in texturesJson.keys()) {
            textureType(key)?.let { textures[it] = texturesJson.getString(key) }
        }
    }
}

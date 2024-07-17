package zatrit.skins.lib.util

import org.json.*
import zatrit.skins.lib.PlayerTextures
import zatrit.skins.lib.data.Metadata
import zatrit.skins.lib.texture.URLTexture
import java.io.*

/** Loads textures as a map to use in [PlayerTextures]. */
fun loadTextures(json: JSONObject): PlayerTextures {
    val textures = PlayerTextures()

    for (key in json.keys()) {
        val obj = json.getJSONObject(key)

        // Gets metadata, otherwise creates empty metadata
        val metadata = if (obj.has("metadata")) Metadata(
          obj.getJSONObject("metadata")
        ) else Metadata()

        val texture = URLTexture(obj.getString("url"), metadata)

        when (key.uppercase()) {
            "SKIN" -> textures.skin = texture
            "CAPE" -> textures.cape = texture
            "EARS" -> textures.ears = texture
        }
    }

    return textures
}

/** Creates a [JSONObject] for the given [InputStream] and closes it. */
val InputStream.jsonObject: JSONObject
    get() = use2 { JSONObject(InputStreamReader(it).readText()) }

/** Creates a [JSONArray] for the given [InputStream] and closes it. */
val InputStream.jsonArray: JSONArray
    get() = use2 { JSONArray(InputStreamReader(it).readText()) }
package zatrit.skins.lib.util

import org.json.*
import zatrit.skins.lib.*
import zatrit.skins.lib.data.Metadata
import zatrit.skins.lib.texture.URLTexture
import java.io.*
import java.util.EnumMap

/** Loads textures as a map to use in [PlayerTextures]. */
fun loadTextureMap(json: JSONObject): Map<TextureType, URLTexture> {
    val map = EnumMap<_, URLTexture>(TextureType::class.java)

    for (key in json.keys()) {
        val type = textureType(key)
        val obj = json.getJSONObject(key)

        // Gets metadata, otherwise creates empty metadata
        val metadata = if (obj.has("metadata")) Metadata(
          obj.getJSONObject("metadata")
        ) else Metadata()

        map[type] = URLTexture(obj.getString("url"), metadata)
    }

    return map
}

/** Converts [string] to [TextureType], ignoring case. */
fun textureType(string: String) = try {
    TextureType.valueOf(string.uppercase())
} catch (ex: Exception) {
    null
}

/** Creates a [JSONObject] for the given [InputStream] and closes it. */
val InputStream.jsonObject: JSONObject
    get() = use2 { JSONObject(InputStreamReader(it).readText()) }

/** Creates a [JSONArray] for the given [InputStream] and closes it. */
val InputStream.jsonArray: JSONArray
    get() = use2 { JSONArray(InputStreamReader(it).readText()) }
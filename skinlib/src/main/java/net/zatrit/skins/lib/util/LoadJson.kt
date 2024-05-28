package net.zatrit.skins.lib.util

import net.zatrit.skins.lib.TextureType
import net.zatrit.skins.lib.data.Metadata
import net.zatrit.skins.lib.texture.URLTexture
import org.json.*
import java.io.*
import java.util.EnumMap

fun loadTextureMap(json: JSONObject): Map<TextureType, URLTexture> {
    val map = EnumMap<_, URLTexture>(TextureType::class.java)

    for (key in json.keys()) {
        val type = textureType(key)
        val obj = json.getJSONObject(key)

        val metadata = if (obj.has("metadata")) Metadata(
            obj.getJSONObject("metadata")
        ) else Metadata()

        map[type] = URLTexture(obj.getString("url"), metadata)
    }

    return map
}

fun textureType(string: String) = try {
    TextureType.valueOf(string.uppercase())
} catch (ex: Exception) {
    null
}

val InputStream.jsonObject: JSONObject
    get() = use { JSONObject(InputStreamReader(it).readText()) }

val InputStream.jsonArray: JSONArray
    get() = use { JSONArray(InputStreamReader(it).readText()) }
package net.zatrit.skins.lib.util

import net.zatrit.skins.lib.TextureType
import net.zatrit.skins.lib.data.Metadata
import net.zatrit.skins.lib.texture.URLTexture
import org.json.*
import java.io.*
import java.util.EnumMap

interface LoadJson {
    fun loadJson(json: JSONObject)
}

fun loadTextureMap(json: JSONObject): Map<TextureType, URLTexture> {
    val map = EnumMap<_, URLTexture>(TextureType::class.java)

    for (key in json.keys()) {
        val type = textureType(key)!!
        val obj = json.getJSONObject(key)

        map[type] = URLTexture(obj.getString("url"), Metadata().apply {
            if (obj.has("metadata")) {
                loadJson(obj.getJSONObject("metadata"))
            }
        })
    }

    return map
}

fun textureType(string: String) = try {
    TextureType.valueOf(string.uppercase())
} catch (ex: Exception) {
    null
}

val InputStream.jsonObject: JSONObject
    get() {
        val string = InputStreamReader(this).readText()
        this.close()

        return JSONObject(JSONTokener(string))
    }

val InputStream.jsonArray: JSONArray
    get() {
        val string = InputStreamReader(this).readText()
        this.close()

        return JSONArray(JSONTokener(string))
    }
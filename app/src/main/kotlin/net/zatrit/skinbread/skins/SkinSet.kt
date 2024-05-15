package net.zatrit.skinbread.skins

import android.graphics.*
import android.os.Parcelable
import android.util.Base64
import kotlinx.parcelize.Parcelize
import net.zatrit.skinbread.Textures
import net.zatrit.skinbread.gl.model.ModelType
import org.json.*
import java.io.ByteArrayOutputStream

@Parcelize
class SkinSet(
    private val size: Int,

    val enabled: BooleanArray = BooleanArray(size) { false },
    val order: IntArray = IntArray(size) { it },
    val textures: Array<Textures?> = arrayOfNulls(size),
) : Parcelable {
    private fun bitmapFromBase64(data: String): Bitmap {
        val bytes = Base64.decode(data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.toByteArray()
        }
        return String(Base64.encode(bytes, Base64.DEFAULT))
    }

    fun loadJson(json: JSONObject) {
        val enabled =
            json.optJSONArray("enabled")?.takeIf { it.length() == size }
        val order = json.optJSONArray("order")?.takeIf { it.length() == size }
        val cachedSkins =
            json.optJSONArray("textures")?.takeIf { it.length() == size }

        for (i in 0..<size) {
            this.enabled[i] = enabled?.getBoolean(i) ?: false
            this.order[i] = order?.getInt(i) ?: i

            val textureData = cachedSkins?.getJSONObject(i) ?: JSONObject()
            this.textures[i] = Textures(
                skin = textureData.optString("skin").takeIf { it.isNotBlank() }
                    ?.run(::bitmapFromBase64),
                cape = textureData.optString("cape").takeIf { it.isNotBlank() }
                    ?.run(::bitmapFromBase64),
                ears = textureData.optString("ears").takeIf { it.isNotBlank() }
                    ?.run(::bitmapFromBase64),
                model = textureData.optString("model").takeIf { it.isNotBlank() }
                    ?.run(ModelType::fromName),
            )
        }
    }

    fun saveJson(): String {
        val json = JSONObject()
        val enabled = JSONArray()
        val order = JSONArray()
        val textures = JSONArray()

        for (i in 0..<size) {
            enabled.put(this.enabled[i])
            order.put(this.order[i])

            val textureData = JSONObject()
            val texture = this.textures[i]
            textureData.put("skin", texture?.skin?.run(::bitmapToBase64))
            textureData.put("cape", texture?.cape?.run(::bitmapToBase64))
            textureData.put("ears", texture?.ears?.run(::bitmapToBase64))
            textureData.put("model", texture?.model?.toString())

            textures.put(textureData)
        }

        json.put("enabled", enabled)
        json.put("order", order)
        json.put("textures", textures)

        return json.toString(0)
    }
}

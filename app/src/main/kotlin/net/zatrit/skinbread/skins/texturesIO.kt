package net.zatrit.skinbread.skins

import android.content.SharedPreferences
import android.graphics.*
import android.util.Base64
import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.model.ModelType
import org.json.*
import java.io.ByteArrayOutputStream

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

const val TEXTURES = "textures"

private fun JSONObject.optBitmap(key: String) =
    optString(key).takeIf { it.isNotBlank() }?.run(::bitmapFromBase64)

fun loadTextures(preferences: SharedPreferences): Array<Textures?> {
    val textures = preferences.getString(TEXTURES, null)?.run(::JSONArray)
        ?: return textures
    val array = arrayOfNulls<Textures>(textures.length())

    for (i in 0..<textures.length()) {
        val data = textures.getJSONObject(i) ?: continue

        array[i] = Textures(
            skin = data.optBitmap("skin"),
            cape = data.optBitmap("cape"),
            ears = data.optBitmap("ears"),
            model = ModelType.fromName(data.optString("model")),
        )
    }

    return array
}

fun serializeTextures(array: Array<Textures?>): String {
    val textures = JSONArray()

    array.map { texture ->
        val textureData = JSONObject()
        textureData.put("skin", texture?.skin?.run(::bitmapToBase64))
        textureData.put("cape", texture?.cape?.run(::bitmapToBase64))
        textureData.put("ears", texture?.ears?.run(::bitmapToBase64))
        textureData.put("model", texture?.model?.toString())

        textures.put(textureData)
    }

    return textures.toString()
}
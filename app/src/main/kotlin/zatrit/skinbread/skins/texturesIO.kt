package zatrit.skinbread.skins

import android.content.Context
import android.graphics.*
import android.util.Log
import zatrit.skinbread.*
import zatrit.skinbread.gl.model.ModelType
import zatrit.skins.lib.util.use2
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync

fun clearTextures(context: Context, i: Int) {
    deleteTexture(context, "skin", i)
    deleteTexture(context, "cape", i)
    deleteTexture(context, "ears", i)
    context.deleteFile("model$i")
}

private fun deleteTexture(context: Context, type: String, index: Int) =
    context.deleteFile(file(type, index))

fun saveTextures(context: Context, i: Int, textures: Textures?) {
    Log.d(TAG, "Saving textures $i")

    textures?.skin?.let { saveTexture(context, "skin", i, it) }
    textures?.cape?.let { saveTexture(context, "cape", i, it) }
    textures?.ears?.let { saveTexture(context, "ears", i, it) }
    textures?.model?.let { saveModelType(context, i, it) }
}

private fun saveTexture(
  context: Context, type: String, index: Int, bitmap: Bitmap) = try {
    context.openFileOutput(file(type, index), Context.MODE_PRIVATE).use2 {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
} catch (ex: Exception) {
    ex.printDebug()
}

private fun saveModelType(context: Context, index: Int, modelType: ModelType) =
    try {
        context.openFileOutput("model$index", Context.MODE_PRIVATE)
          .use2 { it.write(modelType.name.toByteArray()) }
    } catch (ex: Exception) {
        ex.printDebug()
    }

inline fun loadTexturesAsync(
  context: Context,
  crossinline callback: (Array<Textures?>) -> Unit): CompletableFuture<Void> =
    runAsync {
        val textures = arrayOfNulls<Textures?>(defaultSources.size)

        for (i in defaultSources.indices) {
            textures[i] = Textures(
              skin = loadTexture(context, "skin", i),
              cape = loadTexture(context, "cape", i),
              ears = loadTexture(context, "ears", i),
              model = loadModelType(context, i),
            )
        }

        callback(textures)
    }

fun loadTexture(context: Context, type: String, index: Int) = try {
    context.openFileInput(file(type, index)).use2 {
        BitmapFactory.decodeStream(it)
    }
} catch (ex: Exception) {
    null
}

fun loadModelType(context: Context, index: Int) = try {
    val raw = InputStreamReader(context.openFileInput("model$index")).readText()
    ModelType.fromName(raw)
} catch (ex: Exception) {
    null
}

fun file(type: String, index: Int) = "$type$index.png"
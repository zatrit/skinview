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

const val SKIN = "skin"
const val CAPE = "cape"
const val EARS = "ears"
const val MODEL = "model"

/** Deletes textures from local storage. */
fun deleteTextures(context: Context, i: Int) {
    context.deleteLocal(SKIN, i)
    context.deleteLocal(CAPE, i)
    context.deleteLocal(EARS, i)
    context.deleteLocal(MODEL, i)
}

/** Deletes the texture or model type by [type] and [index]. */
private fun Context.deleteLocal(type: String, index: Int) =
  deleteFile(file(type, index))

/** Saves a set of textures to local storage. */
fun saveTextures(context: Context, i: Int, textures: Textures?) {
    Log.d(TAG, "Saving textures $i")

    textures?.skin?.let { context.saveTexture(SKIN, i, it) }
    textures?.cape?.let { context.saveTexture(CAPE, i, it) }
    textures?.ears?.let { context.saveTexture(EARS, i, it) }
    textures?.model?.let { context.saveModelType(i, it) }
}

/** Saves the texture by [type] and [index]. */
private fun Context.saveTexture(type: String, index: Int, bitmap: Bitmap) = try {
    openFileOutput(file(type, index), Context.MODE_PRIVATE).use2 {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
} catch (ex: Exception) {
    ex.printDebug()
}

/** Saves the model type by [index]. */
private fun Context.saveModelType(index: Int, modelType: ModelType) = try {
    openFileOutput(file(MODEL, index), Context.MODE_PRIVATE).use2 {
        it.write(modelType.name.toByteArray())
    }
} catch (ex: Exception) {
    ex.printDebug()
}

/** Loads textures asynchronously and then calls [callback]. */
inline fun loadTexturesAsync(
  context: Context,
  crossinline callback: (Array<Textures?>) -> Unit): CompletableFuture<Void> =
  runAsync {
      val textures = arrayOfNulls<Textures?>(defaultSources.size)

      for (i in defaultSources.indices) {
          textures[i] = Textures(
            skin = context.loadTexture(SKIN, i),
            cape = context.loadTexture(CAPE, i),
            ears = context.loadTexture(EARS, i),
            model = context.loadModelType(i),
          )
      }

      callback(textures)
  }

/** Loads the texture by [type] and [index]. */
fun Context.loadTexture(type: String, index: Int) = try {
    openFileInput(file(type, index)).use2 {
        BitmapFactory.decodeStream(it)
    }
} catch (ex: Exception) {
    null
}

/** Loads the model type by [index]. */
fun Context.loadModelType(index: Int) = try {
    val raw = InputStreamReader(openFileInput(file(MODEL, index))).readText()
    ModelType.fromName(raw)
} catch (ex: Exception) {
    null
}

/** @return file path for given file type. */
fun file(type: String, index: Int) = "$type$index"
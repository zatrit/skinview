package net.zatrit.skinbread.skins

import android.content.Context
import android.graphics.*
import android.util.Log
import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.model.ModelType
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync

fun saveTexturesAsync(
    context: Context, textures: Array<Textures?>,
    indices: IntArray? = null): CompletableFuture<Void> = runAsync {
    textures.forEachIndexed { i, textures ->
        if (textures == null || indices?.contains(i) == false) {
            return@forEachIndexed
        }

        textures.skin?.let { saveTexture(context, "skin", i, it) }
        textures.cape?.let { saveTexture(context, "cape", i, it) }
        textures.ears?.let { saveTexture(context, "ears", i, it) }
        textures.model?.let { saveModelType(context, i, it) }
    }
}

fun saveTexture(context: Context, type: String, index: Int, bitmap: Bitmap) =
    try {
        context.openFileOutput("$type$index.png", Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    } catch (ex: Exception) {
        ex.printDebug()
    }

fun saveModelType(context: Context, index: Int, modelType: ModelType) = try {
    context.openFileOutput("model$index", Context.MODE_PRIVATE).bufferedWriter()
        .use { it.write(modelType.name) }
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
    context.openFileInput("$type$index.png").use {
        BitmapFactory.decodeStream(it)
    }
} catch (ex: Exception) {
    null
}

fun loadModelType(context: Context, index: Int) = try {
    val raw = context.openFileInput("model$index").bufferedReader()
        .use { it.readText() }
    Log.d(TAG, raw)

    ModelType.fromName(raw)
} catch (ex: Exception) {
    null
}
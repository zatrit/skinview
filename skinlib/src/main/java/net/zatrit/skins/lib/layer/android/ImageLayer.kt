package net.zatrit.skins.lib.layer.android

import android.graphics.Bitmap
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.texture.BitmapTexture

abstract class ImageLayer : Layer<Texture> {
    open fun predicate(input: Texture) = true

    override fun apply(input: Texture): Texture {
        return if (!predicate(input)) {
            input
        } else {
            val bitmap = input.getBitmap()
            BitmapTexture(apply(bitmap), input)
        }
    }

    abstract fun apply(input: Bitmap): Bitmap
}
package zatrit.skins.lib.layer.android

import android.graphics.Bitmap
import zatrit.skins.lib.api.Layer
import zatrit.skins.lib.api.Texture
import zatrit.skins.lib.texture.BitmapTexture

/** An abstract layer that transforms [Bitmap] or [Texture] in a certain way. */
abstract class ImageLayer : Layer<Texture> {
    open fun predicate(input: Texture) = true

    override fun apply(input: Texture): Texture {
        return if (!predicate(input)) input
        else {
            val bitmap = input.bitmap
            BitmapTexture(apply(bitmap), input)
        }
    }

    abstract fun apply(input: Bitmap): Bitmap
}
package net.zatrit.skins.lib.layer.android

import android.graphics.Bitmap
import net.zatrit.skins.lib.api.Layer
import net.zatrit.skins.lib.data.TypedTexture
import net.zatrit.skins.lib.texture.BitmapTexture

typealias BitmapLayer = Layer<Bitmap>

abstract class ImageLayer(private val layer: BitmapLayer) : Layer<TypedTexture> {
    abstract fun predicate(input: TypedTexture): Boolean

    override fun apply(input: TypedTexture): TypedTexture {
        return if (!predicate(input)) {
            input
        } else {
            val old = input.texture
            val bitmap = old.asBitmap()

            val texture = BitmapTexture(layer.apply(bitmap), old)
            TypedTexture(texture, input.type)
        }
    }
}
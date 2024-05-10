package net.zatrit.skins.lib.layer.android

import android.graphics.Bitmap
import net.zatrit.skins.lib.api.Layer
import net.zatrit.skins.lib.data.TypedTexture
import net.zatrit.skins.lib.texture.BitmapTexture

abstract class ImageLayer : Layer<TypedTexture> {
    open fun predicate(input: TypedTexture) = true

    override fun apply(input: TypedTexture): TypedTexture {
        return if (!predicate(input)) {
            input
        } else {
            val old = input.texture
            val bitmap = old.getBitmap()

            val texture = BitmapTexture(apply(bitmap), old)
            TypedTexture(texture, input.type)
        }
    }

    abstract fun apply(input: Bitmap): Bitmap
}
package net.zatrit.skins.lib.layer.android

import android.graphics.Bitmap
import net.zatrit.skins.lib.data.TypedTexture

class ScaleCapeLayer : ImageLayer() {
    override fun apply(input: Bitmap): Bitmap {
        if (input.height * 2 == input.width) {
            return input
        }

        val height = 1 shl -Integer.numberOfLeadingZeros(input.height - 1)
        val width = height * 2

        val buffer = IntArray(width * height)
        input.getPixels(buffer, 0, 0, 0, 0, input.width, input.height)

        return Bitmap.createBitmap(width, height, input.config).apply {
            setPixels(buffer, 0, 0, 0, 0, input.width, input.height)
        }
    }

    override fun predicate(input: TypedTexture): Boolean {
        val metadata = input.texture.metadata
        return metadata == null || !metadata.isAnimated
    }
}
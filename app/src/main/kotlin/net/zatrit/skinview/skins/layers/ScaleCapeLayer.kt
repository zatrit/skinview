package net.zatrit.skinview.skins.layers

import android.graphics.Bitmap
import com.google.common.math.IntMath

class ScaleCapeLayer : BitmapLayer {
    override fun apply(input: Bitmap): Bitmap {
        if (input.height * 2 == input.width) {
            return input
        }

        val height = IntMath.ceilingPowerOfTwo(input.height)
        val width = height * 2

        val buffer = IntArray(width * height)
        input.getPixels(buffer, 0, 0, 0, 0, input.width, input.height)

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(buffer, 0, 0, 0, 0, input.width, input.height)
        }
    }
}
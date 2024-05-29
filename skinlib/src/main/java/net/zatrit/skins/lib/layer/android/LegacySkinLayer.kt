package net.zatrit.skins.lib.layer.android

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

class LegacySkinLayer : ImageLayer() {
    var legacyMask: Bitmap? = null

    override fun apply(input: Bitmap): Bitmap {
        val size = input.width

        if (input.height != 32 || input.height == input.width) return input

        val dest = Bitmap.createBitmap(size, size, input.config)
        val canvas = Canvas(dest)

        val paint = basePaint(canvas, input)
        canvas.drawBitmap(input, 0f, 0f, paint)

        drawMirrored(dest, canvas, 0, 20, 16, 52, 12, 12)
        drawMirrored(dest, canvas, 12, 20, 28, 52, 4, 12)
        drawMirrored(dest, canvas, 4, 16, 20, 48, 4, 4)
        drawMirrored(dest, canvas, 8, 16, 24, 48, 4, 4)
        drawMirrored(dest, canvas, 40, 20, 32, 52, 12, 12)
        drawMirrored(dest, canvas, 52, 20, 44, 52, 4, 12)
        drawMirrored(dest, canvas, 44, 16, 36, 48, 4, 4)
        drawMirrored(dest, canvas, 48, 16, 40, 48, 4, 4)

        return dest
    }

    private fun drawMirrored(
      src: Bitmap, canvas: Canvas, sx: Int, sy: Int, dx: Int, dy: Int, w: Int,
      h: Int) {
        val matrix = Matrix()

        matrix.setValues(
          floatArrayOf(
            -1f, 0f, (dx + w).toFloat(), 0f, 1f, dy.toFloat(), 0f, 0f, 1f
          )
        )

        canvas.drawBitmap(
          Bitmap.createBitmap(src, sx, sy, w, h), matrix, null
        )
    }

    private fun basePaint(canvas: Canvas, input: Bitmap): Paint? {
        return if (Color.alpha(input.getPixel(0, 0)) != 0) {
            val mask = legacyMask ?: return null
            canvas.drawBitmap(mask, 0f, 0f, null)

            Paint().apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
            }
        } else {
            null
        }
    }
}
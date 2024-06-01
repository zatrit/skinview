package zatrit.skinbread.skins

import android.graphics.*
import zatrit.skinbread.Textures
import zatrit.skinbread.gl.model.ModelType

fun drawPreview(entry: Textures): Bitmap {
    val bitmap = Bitmap.createBitmap(355, 296, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    entry.skin?.let { drawSkin(canvas, it, entry.model ?: ModelType.DEFAULT) }
    entry.cape?.let { drawCape(canvas, it) }
    entry.ears?.let { drawEars(canvas, it) }

    return bitmap
}

private fun Bitmap.rect64(
  left: Int, top: Int, width: Int, height: Int, ratio: Float = 1f): Rect {
    val hRate = this.width / 64f
    val vRate = this.height / 64f * ratio
    val left1 = hRate * left
    val top1 = vRate * top
    return Rect(
      left1.toInt(),
      top1.toInt(),
      (left1 + hRate * width).toInt(),
      (top1 + vRate * height).toInt(),
    )
}

private fun Canvas.drawBitmap(bitmap: Bitmap, rect1: Rect, rect2: Rect) =
    drawBitmap(bitmap, rect1, rect2, null)

private fun Canvas.drawBitmap(
  bitmap: Bitmap, sx: Int, sy: Int, w: Int, h: Int, dx: Int, dy: Int,
  scale: Float = 9f, ratio: Float = 1f) {
    drawBitmap(
      bitmap, bitmap.rect64(sx, sy, w, h, ratio = ratio),
      Rect(dx, dy, dx + (w * scale).toInt(), dy + (h * scale).toInt())
    )
}

fun drawSkin(canvas: Canvas, skin: Bitmap, model: ModelType) {
    canvas.drawBitmap(skin, 8, 8, 8, 8, 40, 4)
    canvas.drawBitmap(skin, 20, 20, 8, 12, 40, 76)
    canvas.drawBitmap(skin, 4, 20, 4, 12, 40, 182)
    canvas.drawBitmap(skin, 20, 52, 4, 12, 76, 182)

    // Hand width
    val hw = if (model == ModelType.SLIM) 3 else 4
    canvas.drawBitmap(skin, 44, 20, hw, 12, 40 - hw * 9, 76)
    canvas.drawBitmap(skin, 36, 52, hw, 12, 112, 76)

    // Scale for second layer
    val s2 = 10f
    canvas.drawBitmap(skin, 40, 8, 8, 8, 36, 0, s2)
    canvas.drawBitmap(skin, 20, 36, 8, 12, 36, 72, s2)
    canvas.drawBitmap(skin, 4, 36, 4, 12, 38, 176, s2)
    canvas.drawBitmap(skin, 4, 52, 4, 12, 74, 176, s2)
    canvas.drawBitmap(skin, 44, 36, hw, 12, 38 - hw * 9, 70, s2)
    canvas.drawBitmap(skin, 52, 52, hw, 12, 110, 70, s2)
}

fun drawCape(canvas: Canvas, cape: Bitmap) {
    canvas.drawBitmap(cape, 34, 2, 12, 20, 148, 111, ratio = 2f)
    canvas.drawBitmap(cape, 1, 1, 10, 16, 265, 148, ratio = 2f)
}

fun drawEars(canvas: Canvas, ears: Bitmap) {
    canvas.drawBitmap(ears, Rect(1, 1, 7, 7), Rect(148, 50, 202, 102))
    canvas.drawBitmap(ears, Rect(8, 1, 14, 7), Rect(202, 50, 256, 102))
}
package net.zatrit.skinbread

import android.app.Activity
import android.graphics.*
import android.os.Parcelable
import android.view.*
import android.widget.*
import kotlinx.parcelize.Parcelize
import net.zatrit.skinbread.gl.model.ModelType
import net.zatrit.skins.lib.texture.BitmapTexture


@Parcelize
class TexturesEntry(
    val name: String,
    private val skin: Bitmap? = null,
    private val cape: Bitmap? = null,
    private val ears: Bitmap? = null,
    private val model: ModelType? = null,
) : Parcelable {
    fun asTextures() = Textures(
        skin = skin?.run(::BitmapTexture),
        cape = cape?.run(::BitmapTexture),
        ears = ears?.run(::BitmapTexture),
        model = model,
    )

    val preview: Bitmap?
        get() {
            val skin = skin ?: return null
            assert(skin.width == skin.height)

            val bitmap = Bitmap.createBitmap(
                skin.width / 4, skin.height / 2, Bitmap.Config.ARGB_8888, true
            )
            val canvas = Canvas(bitmap)

            canvas.drawBitmap(skin, 8, 8, 4, 0, 8, 8)

            if (model == ModelType.DEFAULT) {
                canvas.drawBitmap(skin, 36, 54, 12, 8, 4, 12)
                canvas.drawBitmap(skin, 44, 20, 0, 8, 4, 12)
            } else {
                canvas.drawBitmap(skin, 36, 52, 12, 8, 3, 12)
                canvas.drawBitmap(skin, 44, 20, 1, 8, 3, 12)
            }

            canvas.drawBitmap(skin, 20, 20, 4, 8, 8, 12)

            canvas.drawBitmap(skin, 4, 20, 4, 20, 4, 12)
            canvas.drawBitmap(skin, 20, 52, 8, 20, 4, 12)

            canvas.scale(1.1f, 1.1f)

            return bitmap
        }

    private fun Canvas.drawBitmap(
        bitmap: Bitmap, sx: Int, sy: Int, dx: Int, dy: Int, width: Int,
        height: Int) = drawBitmap(
        bitmap, bitmap.rect64(sx, sy, width, height),
        bitmap.rect64(dx, dy, width, height),
    )

    private fun Bitmap.rect64(
        left: Int, top: Int, width: Int, height: Int): Rect {
        val rate = this.width / 64f
        val left1 = rate * left
        val top1 = rate * top
        return Rect(
            left1.toInt(),
            top1.toInt(),
            (left1 + rate * width).toInt(),
            (top1 + rate * height).toInt(),
        )
    }

    private fun Canvas.drawBitmap(bitmap: Bitmap, rect1: Rect, rect2: Rect) =
        drawBitmap(bitmap, rect1, rect2, null)
}

class SkinListAdapter(
    private val context: Activity,
    private val imageView: Int = R.id.imageview_preview,
    private val textView: Int = R.id.textview_name,
    private val entry: Int = R.layout.texture_entry,
) : ArrayAdapter<TexturesEntry>(context, entry) {
    // https://java2blog.com/android-custom-listview-with-images-text-example/
    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater

        return convertView ?: inflater.inflate(entry, null, true).also {
            val entryData = this.getItem(position)!!

            val name = it.findViewById<TextView>(textView)
            val preview = it.findViewById<ImageView>(imageView)

            name.text = entryData.name
            preview.setImageBitmap(entryData.preview)
        }
    }
}
package net.zatrit.skins.lib.texture

import android.graphics.Bitmap
import net.zatrit.skins.lib.api.Texture
import net.zatrit.skins.lib.data.Metadata
import java.io.ByteArrayOutputStream

class BitmapTexture(
    private val bitmap: Bitmap, id: String, metadata: Metadata?) :
    LazyTexture(id, metadata) {

    constructor(bitmap: Bitmap, base: Texture) : this(
        bitmap, base.id, base.metadata
    )

    override fun getBytes(): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    override fun asBitmap() = bitmap
}
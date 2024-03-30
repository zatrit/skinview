package net.zatrit.skins.lib.texture

import android.graphics.Bitmap
import net.zatrit.skins.lib.api.Texture
import net.zatrit.skins.lib.data.Metadata
import java.io.ByteArrayOutputStream

class BitmapTexture(
    private val bitmap: Bitmap, metadata: Metadata? = null) :
    LazyTexture(metadata) {

    constructor(bitmap: Bitmap, base: Texture) : this(bitmap, base.metadata)

    override fun getBytes(): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    override fun asBitmap() = bitmap
}
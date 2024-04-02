package net.zatrit.skins.lib.texture

import android.graphics.Bitmap
import net.zatrit.skins.lib.api.Texture
import net.zatrit.skins.lib.data.Metadata

class BitmapTexture(
    private val bitmap: Bitmap, metadata: Metadata? = null) :
    LazyTexture(metadata) {

    constructor(bitmap: Bitmap, base: Texture) : this(bitmap, base.metadata)

    // It's not necessary to implement this
    override fun getBytes() = TODO()

    override fun asBitmap() = bitmap
}
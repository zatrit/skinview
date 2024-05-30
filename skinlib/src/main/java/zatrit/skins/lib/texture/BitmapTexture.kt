package zatrit.skins.lib.texture

import android.graphics.Bitmap
import zatrit.skins.lib.api.Texture
import zatrit.skins.lib.data.Metadata

/** A wrapper around [Bitmap] to use it as a [Texture]. */
class BitmapTexture(
  private val bitmap: Bitmap, metadata: Metadata? = null) :
  LazyTexture(metadata) {

    constructor(bitmap: Bitmap, base: Texture) : this(bitmap, base.metadata)

    // It's not necessary to implement this
    override fun openStream() = null

    override fun getBitmap() = bitmap
}
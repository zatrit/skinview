package zatrit.skinbread

import android.graphics.Bitmap
import zatrit.skinbread.gl.*
import zatrit.skinbread.gl.model.ModelType
import zatrit.skins.lib.*
import zatrit.skins.lib.api.*

/** A set of textures used to permanently store player textures. */
class Textures(
  var skin: Bitmap? = null,
  var cape: Bitmap? = null,
  var ears: Bitmap? = null,
  var model: ModelType? = null,
) {
    /** @return true if none of the textures is null. */
    fun isComplete() = skin != null && cape != null && ears != null

    /** @return true if all textures are null. */
    fun isEmpty() = skin == null && cape == null && ears == null

    /** Creates an OpenGL texture set with the current textures.
     *
     * @return [GLTextures] with given persistence.
     */
    @GLContext
    fun load(persistent: Boolean = false) = GLTextures(
      skin = skin?.let { GLTexture(it, persistent) },
      cape = cape?.let { GLTexture(it, persistent) },
      ears = ears?.let { GLTexture(it, persistent) },
    )

    /** Fills in empty textures with [input], loading only necessary ones. */
    fun or(
      input: PlayerTextures, skinLayer: Layer<Texture>,
      capeLayer: Layer<Texture>) {
        val skinTexture = input.getTexture(TextureType.SKIN)

        if (this.skin == null) {
            this.model = ModelType.fromName(skinTexture?.metadata?.model)
        }

        this.skin = this.skin ?: skinTexture?.run(skinLayer::tryApply)?.bitmap

        this.cape = this.cape ?: input.getTexture(TextureType.CAPE)
          ?.run(capeLayer::tryApply)?.bitmap

        this.ears = this.ears ?: input.getTexture(TextureType.EARS)?.bitmap
    }

    /** Fills in empty textures with [other]. */
    fun or(other: Textures) {
        if (this.skin == null) this.model = other.model

        this.skin = this.skin ?: other.skin
        this.cape = this.cape ?: other.cape
        this.ears = this.ears ?: other.ears
    }
}
package net.zatrit.skinbread

import android.graphics.Bitmap
import net.zatrit.skinbread.gl.*
import net.zatrit.skinbread.gl.model.ModelType
import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*

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

    fun fillWith(
        input: PlayerTextures, skinLayer: Layer<Texture>,
        capeLayer: Layer<Texture>) {
        val skinTexture = input.getTexture(TextureType.SKIN)

        if (this.skin == null) {
            this.model = ModelType.fromName(skinTexture?.metadata?.model)
        }

        this.skin = this.skin ?: skinTexture?.run(skinLayer::apply)?.bitmap

        this.cape = this.cape ?: input.getTexture(TextureType.CAPE)
            ?.run(capeLayer::apply)?.bitmap

        this.ears = this.ears ?: input.getTexture(TextureType.EARS)?.bitmap
    }

    fun fillWith(other: Textures) {
        if (this.skin == null) {
            this.model = other.model
        }

        this.skin = this.skin ?: other.skin
        this.cape = this.cape ?: other.cape
        this.ears = this.ears ?: other.ears
    }
}

class OrderedTextures(var order: Int, var textures: Textures)
package net.zatrit.skinbread

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.*
import net.zatrit.skinbread.gl.*
import net.zatrit.skinbread.gl.model.ModelType
import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.Layer
import net.zatrit.skins.lib.data.TypedTexture

@Parcelize
class Textures(
    var skin: Bitmap? = null,
    var cape: Bitmap? = null,
    var ears: Bitmap? = null,
    var model: ModelType? = null,
) : Parcelable {
    @IgnoredOnParcel
    val complete = skin != null && cape != null && ears != null

    fun load(persistent: Boolean = false) = GLTextures(
        skin = skin?.let { GLTexture(it, persistent) },
        cape = cape?.let { GLTexture(it, persistent) },
        ears = ears?.let { GLTexture(it, persistent) },
    )

    fun fillWith(
        input: PlayerTextures, skinLayer: Layer<TypedTexture>,
        capeLayer: Layer<TypedTexture>) {
        val skinTexture = input.getTexture(TextureType.SKIN)

        if (this.skin == null) {
            this.model =
                ModelType.fromName(skinTexture?.texture?.metadata?.model)
        }

        this.skin =
            this.skin ?: skinTexture?.let(skinLayer::apply)?.texture?.bitmap

        this.cape = this.cape ?: input.getTexture(TextureType.CAPE)
            ?.run(capeLayer::apply)?.texture?.bitmap

        this.ears =
            this.ears ?: input.getTexture(TextureType.EARS)?.texture?.bitmap
    }
}
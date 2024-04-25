package net.zatrit.skinbread

import android.util.Log
import net.zatrit.skinbread.gl.Texture
import net.zatrit.skinbread.gl.model.ModelType
import net.zatrit.skins.lib.api.Texture as SLTexture

class Textures(
    private var skin: SLTexture? = null,
    private var cape: SLTexture? = null,
    private var ears: SLTexture? = null,
    var model: ModelType? = null,
) {
    val complete = skin != null && cape != null && ears != null

    fun load(persistent: Boolean = false) = GLTextures(
        skin = skin?.let { loadTexture(it, persistent) },
        cape = cape?.let { loadTexture(it, persistent) },
        ears = ears?.let { loadTexture(it, persistent) },
    )

    private fun loadTexture(texture: SLTexture, persistent: Boolean) =
        texture.asBitmap().let {
            val texture1 = Texture(it, persistent)
            it.recycle()
            texture1
        }

    fun fillWith(other: Textures) {
        if (this.skin == null) {
            this.model = other.model
        }

        skin = skin ?: other.skin
        cape = cape ?: other.cape
        ears = ears ?: other.ears
    }
}

class GLTextures(
    var skin: Texture? = null,
    var cape: Texture? = null,
    var ears: Texture? = null,
) {
    fun delete() {
        skin?.delete()
        cape?.delete()
        ears?.delete()
    }

    fun fillWith(other: GLTextures) {
        skin = skin ?: other.skin
        cape = cape ?: other.cape
        ears = ears ?: other.ears
    }

    @DebugOnly
    fun printInfo() = Log.v(TAG, "skin: $skin, cape: $cape, ears: $ears")
}
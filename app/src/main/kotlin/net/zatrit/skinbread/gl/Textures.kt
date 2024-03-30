package net.zatrit.skinbread.gl

import net.zatrit.skins.lib.api.Texture as SLTexture

class Textures(
    private var skin: SLTexture? = null, private var cape: SLTexture? = null,
    private var ears: SLTexture? = null, var model: ModelType? = null) {
    val complete = skin != null && cape != null && ears != null

    fun load() = GLTextures(
        skin = skin?.let { Texture(it) },
        cape = cape?.let { Texture(it) },
        ears = ears?.let { Texture(it) },
    )

    fun or(other: Textures) {
        if (this.skin == null) {
            this.model = other.model
        }

        skin = skin ?: other.skin
        cape = cape ?: other.cape
        ears = ears ?: other.ears
    }
}

class GLTextures(
    val skin: Texture? = null, val cape: Texture? = null,
    val ears: Texture? = null) {
    fun delete() {
        skin?.delete()
        cape?.delete()
        ears?.delete()
    }
}
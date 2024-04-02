package net.zatrit.skinbread.gl

import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import net.zatrit.skinbread.*
import net.zatrit.skins.lib.api.Texture

class Texture(bitmap: Bitmap) {
    private val id = buf { glGenTextures(1, it) }

    init {
        textureInfo(bitmap)

        bind()
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        glGenerateMipmap(GL_TEXTURE_2D)

        bitmap.recycle()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    }

    constructor(texture: Texture) : this(texture.asBitmap())

    fun bind() = glBindTexture(GL_TEXTURE_2D, id.get(0))

    fun delete() = glDeleteTextures(1, id)
}

@DebugOnly
private fun textureInfo(bitmap: Bitmap) = bitmap.run {
    Log.v(TAG, "width: $width, height: $height, byteCount: $byteCount")
}
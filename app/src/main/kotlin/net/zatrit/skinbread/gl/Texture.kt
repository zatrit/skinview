package net.zatrit.skinbread.gl

import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import net.zatrit.skinbread.*
import net.zatrit.skins.lib.api.Texture
import java.nio.IntBuffer

class Texture(bitmap: Bitmap) {
    private val id: Int

    init {
        textureInfo(bitmap)

        val buf = IntBuffer.allocate(1)
        glGenTextures(1, buf)
        id = buf.get()

        bind()
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        glGenerateMipmap(GL_TEXTURE_2D)

        bitmap.recycle()

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    }

    constructor(texture: Texture) : this(texture.asBitmap())

    fun bind() = glBindTexture(GL_TEXTURE_2D, id)

    fun delete() = glDeleteTextures(1, IntBuffer.allocate(1).put(0, id))
}

@DebugOnly
private fun textureInfo(bitmap: Bitmap) = bitmap.run {
    Log.v(TAG, "width: $width, height: $height, byteCount: $byteCount")
}
package net.zatrit.skinbread.gl

import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import net.zatrit.skinbread.*

@DebugOnly
private fun textureInfo(bitmap: Bitmap) = bitmap.run {
    Log.v(TAG, "width: $width, height: $height, byteCount: $byteCount")
}

@GLContext
class GLTexture(bitmap: Bitmap, private val persistent: Boolean) {
    private val id = buf { glGenTextures(1, it) }

    init {
        textureInfo(bitmap)

        bind()
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        glGenerateMipmap(GL_TEXTURE_2D)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    }

    fun bind() = glBindTexture(GL_TEXTURE_2D, id.get(0))

    fun delete() {
        if (!persistent) {
            glDeleteTextures(1, id)
        }
    }

    @DebugOnly
    override fun toString() = "Texture(${id[0]})"
}

@GLContext
class GLTextures(
    var skin: GLTexture? = null,
    var cape: GLTexture? = null,
    var ears: GLTexture? = null,
) {
    fun delete() {
        skin?.delete()
        cape?.delete()
        ears?.delete()
    }

    @DebugOnly
    fun printInfo() = Log.v(TAG, "skin: $skin, cape: $cape, ears: $ears")
}
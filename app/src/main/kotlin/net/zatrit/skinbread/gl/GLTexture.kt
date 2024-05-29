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
class GLTexture(bitmap: Bitmap, private val persistent: Boolean = false) {
    val id = buf { glGenTextures(1, it) }

    @get:DebugOnly
    val info: String
        get() = "GLTexture { id: ${id.get(0)}, persistent: $persistent }"

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
        if (!persistent) glDeleteTextures(1, id)
    }
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

    fun populate(other: GLTextures) {
        this.skin = other.skin ?: this.skin
        this.cape = other.cape ?: this.cape
        this.ears = other.ears ?: this.ears
    }

    fun clone() = GLTextures(skin, cape, ears)

    @DebugOnly
    fun printInfo() = Log.v(
      TAG, "skin: ${skin?.info}, cape: ${cape?.info}, ears: ${ears?.info}"
    )

    @DebugOnly
    fun assertEmpty() = assert(skin == null && cape == null && ears == null)
}
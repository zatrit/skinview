package zatrit.skinbread.gl

import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import zatrit.skinbread.*

/** Prints information about the given [Bitmap] to [Log]. */
@DebugOnly
private fun textureInfo(bitmap: Bitmap) = bitmap.run {
    Log.v(TAG, "width: $width, height: $height, byteCount: $byteCount")
}

/** Wrapper around [OpenGL texture](https://www.khronos.org/opengl/wiki/Texture). */
@GLContext
class GLTexture(
  bitmap: Bitmap,
  /** Marks the texture as persistent, which will cause it to
   * ignore [delete] calls. Allows default textures to be made. */
  private val persistent: Boolean = false,
) {
    /** The OpenGL identifier for the given texture. */
    val id = buf { glGenTextures(1, it) }

    /** The debugging information about this texture. */
    @get:DebugOnly
    val info: String
        get() = "GLTexture { id: ${id.get(0)}, persistent: $persistent }"

    init {
        textureInfo(bitmap)

        bind()
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        glGenerateMipmap(GL_TEXTURE_2D)

        // Disables texture blurring
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    }

    /** Wraps [glBindTexture]. */
    fun bind() = glBindTexture(GL_TEXTURE_2D, id.get(0))

    /** Wraps [glDeleteTextures], but does not guarantee that it will be called. */
    fun delete() {
        if (!persistent) glDeleteTextures(1, id)
    }
}

/** A set of [GLTexture], making it easier to interact with them. */
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

    /** Prints debugging information about textures to [Log]. */
    @DebugOnly
    fun printInfo() = Log.v(
      TAG, "skin: ${skin?.info}, cape: ${cape?.info}, ears: ${ears?.info}"
    )
}
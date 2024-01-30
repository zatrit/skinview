package net.zatrit.skinview.gl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES31.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import net.zatrit.skinview.DebugOnly
import net.zatrit.skinview.TAG
import java.io.InputStream
import java.nio.IntBuffer

class Texture(bitmap: Bitmap) {
    private val id: Int

    init {
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

    constructor(steam: InputStream) : this(BitmapFactory.decodeStream(steam))

    private fun bind() = glBindTexture(GL_TEXTURE_2D, id)

    fun delete() = glDeleteTextures(1, IntBuffer.allocate(1).put(0, id))
}

@DebugOnly
private fun textureInfo(bitmap: Bitmap) {
    Log.i(TAG, "Width: " + bitmap.width)
    Log.i(TAG, "Height: " + bitmap.height)
    Log.i(TAG, "Size: " + bitmap.byteCount)
}
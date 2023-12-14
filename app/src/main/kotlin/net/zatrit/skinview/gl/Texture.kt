package net.zatrit.skinview.gl

import android.graphics.BitmapFactory
import android.opengl.GLES31.*
import android.opengl.GLUtils.texImage2D
import android.util.Log
import net.zatrit.skinview.TAG
import java.io.InputStream
import java.nio.IntBuffer

class Texture(private val id: Int) {
    fun bind() = glBindTexture(GL_TEXTURE_2D, id)

    fun destroy() = glDeleteTextures(1, IntBuffer.allocate(1).put(0, id))
}

fun loadTexture(stream: InputStream): Texture {
    val bitmap = BitmapFactory.decodeStream(stream)
    val buf = IntBuffer.allocate(1)

    Log.i(TAG, "Width: " + bitmap.width)
    Log.i(TAG, "Height: " + bitmap.height)
    Log.i(TAG, "Size: " + bitmap.byteCount)

    glGenTextures(1, buf)
    val texture = Texture(buf[0])

    texture.bind()
    texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
    glGenerateMipmap(GL_TEXTURE_2D)

    bitmap.recycle()

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)

    return texture
}
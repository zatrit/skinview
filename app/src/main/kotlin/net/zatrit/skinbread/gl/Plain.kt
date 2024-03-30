package net.zatrit.skinbread.gl

import android.opengl.GLES30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Plain(x1: Float, z1: Float, x2: Float, z2: Float) {
    private val vao: Int

    init {
        val vertices = FloatBuffer.wrap(
            floatArrayOf(
                x1, z1, // bottom left
                x1, z2, // top left
                x2, z2, // top right
                x2, z1, // bottom right
            )
        )

        val buf = IntBuffer.allocate(1)

        glGenVertexArrays(1, buf)
        vao = buf.get(0)
        glBindVertexArray(vao)

        glGenBuffers(1, buf)
        val vbo = buf.get(0)

        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(
            GL_ARRAY_BUFFER, vertices.capacity() * Float.SIZE_BYTES, vertices,
            GL_STATIC_DRAW
        )
        glVertexAttribPointer(
            0, 2, GL_FLOAT, false, 2 * Float.SIZE_BYTES, 0
        )
        glEnableVertexAttribArray(0)
    }

    fun draw() {
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)
    }
}

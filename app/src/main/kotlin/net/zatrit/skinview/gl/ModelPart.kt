package net.zatrit.skinview.gl

import android.opengl.GLES31.*
import net.zatrit.skinview.DebugOnly
import java.nio.FloatBuffer
import java.nio.IntBuffer

private fun vboData(buf: FloatBuffer, id: Int, index: Int, size: Int) {
    glBindBuffer(GL_ARRAY_BUFFER, id)
    glBufferData(
        GL_ARRAY_BUFFER, buf.capacity() * Float.SIZE_BYTES, buf, GL_STATIC_DRAW
    )
    glVertexAttribPointer(
        index, size, GL_FLOAT, false, size * Float.SIZE_BYTES, 0
    )
    glEnableVertexAttribArray(index)
}

@DebugOnly
private fun sizeChecks(vertices: FloatArray, textureCoords: FloatArray) {
    assert(vertices.size % 3 == 0)
    assert(textureCoords.size % 2 == 0)
    assert(vertices.size / 3 == textureCoords.size / 2)
}

class ModelPart(vertices: FloatArray, textureCoords: FloatArray) {
    private val vao: Int

    private fun bind() = glBindVertexArray(vao)

    fun draw() {
        bind()
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0)
    }

    init {
        sizeChecks(vertices, textureCoords)

        val buf = IntBuffer.allocate(3)
        val verticesBuffer = FloatBuffer.wrap(vertices)
        val textureCoordsBuffer = FloatBuffer.wrap(textureCoords)

        val indices = IntBuffer.allocate(36)
        for (i in 0..5) {
            val j = i * 4
            indices.put(intArrayOf(j, j + 1, j + 2, j + 2, j + 3, j))
        }
        indices.rewind()

        glGenVertexArrays(1, buf)
        vao = buf[0]
        glBindVertexArray(vao)

        glGenBuffers(3, buf)

        vboData(verticesBuffer, buf[0], 0, 3)
        vboData(textureCoordsBuffer, buf[1], 1, 2)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buf[2])
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER, 36 * Int.SIZE_BYTES, indices, GL_STATIC_DRAW
        )
    }
}
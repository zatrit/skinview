package net.zatrit.skinbread.gl

import android.opengl.GLES30.*
import net.zatrit.skinbread.*
import java.nio.FloatBuffer

/**
 * Checks the number of vertices and the number of texture
 * coordinates for validity and consistency with each other. */
@DebugOnly
private fun sizeChecks(vertices: FloatArray, textureCords: FloatArray) {
    assert(vertices.size % 3 == 0)
    assert(textureCords.size % 2 == 0)
    assert(vertices.size / 3 == textureCords.size / 2)
}

/**
 * Implementation of a textured parallelepiped for OpenGL.
 * Created from texture coordinates and points. */
@GLContext
class ModelPart(vertices: FloatArray, textureCords: FloatArray) {
    /**
     * [Vertex array object](https://www.khronos.org/opengl/wiki/Vertex_Specification#Vertex_Array_Object)
     * for parallelepiped. */
    private val vao = genVertexArray()

    init {
        sizeChecks(vertices, textureCords)

        val indices = buf(36) {
            /* Fills the index array, making the parallelepiped
            suitable for drawing with triangles */
            for (i in 0..5) {
                val j = i * 4
                it.put(intArrayOf(j, j + 1, j + 2, j + 2, j + 3, j))
            }
            it.rewind()
        }

        glBindVertexArray(vao)

        val buf = buf(3) { glGenBuffers(3, it) }

        // Saves the index buffer (Element Array Buffer) to the OpenGL buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buf[2])
        glBufferData(
            GL_ELEMENT_ARRAY_BUFFER, 36 * Int.SIZE_BYTES, indices, GL_STATIC_DRAW
        )

        // Creates two arrays: for coordinates and for points on textures
        val verticesBuffer = FloatBuffer.wrap(vertices)
        val textureCordsBuffer = FloatBuffer.wrap(textureCords)

        vboData(verticesBuffer, buf[0], 0, 3)
        vboData(textureCordsBuffer, buf[1], 1, 2)
    }

    fun render() {
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0)
    }
}
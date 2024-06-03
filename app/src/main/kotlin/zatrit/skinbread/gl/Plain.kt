package zatrit.skinbread.gl

import android.opengl.GLES30.*
import zatrit.skinbread.GLContext
import java.nio.FloatBuffer

/** Implementation of a two-dimensional plane along the X and Z axes for OpenGL. */
@GLContext
class Plain(x1: Float, z1: Float, x2: Float, z2: Float) {
    /**
     * [Vertex array object](https://www.khronos.org/opengl/wiki/Vertex_Specification#Vertex_Array_Object)
     * for plane. */
    private val vao = genVertexArray()

    init {
        val vertices = FloatBuffer.wrap(
          floatArrayOf(
            x1, z1, // bottom left
            x1, z2, // top left
            x2, z2, // top right
            x2, z1, // bottom right
          )
        )

        glBindVertexArray(vao)

        val vbo = buf { glGenBuffers(1, it) }.get()
        vboData(vertices, vbo, 0, 2)

        glEnableVertexAttribArray(0)
    }

    fun render() {
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)
    }
}

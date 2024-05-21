package net.zatrit.skinbread.gl

import android.opengl.GLES30.*
import net.zatrit.skinbread.GLContext
import java.nio.*

/** Creates a 4x4 matrix and fills it according to [func]. */
inline fun mat4(func: (FloatArray) -> Unit) = FloatArray(16).also(func)

/** Allocates a buffer with capacity [size] and fills it according to [func]. */
inline fun buf(size: Int = 1, func: (IntBuffer) -> Unit): IntBuffer =
    IntBuffer.allocate(size).also(func)

/** Creates a new [OpenGL vertex array object](https://www.khronos.org/opengl/wiki/Vertex_Specification#Vertex_Array_Object). */
@GLContext
fun genVertexArray() = buf { glGenVertexArrays(1, it) }.get()

/**
 * Automatically writes FloatBuffer values to OpenGL
 * [Vertex Buffer Object](https://www.khronos.org/opengl/wiki/Vertex_Specification#Vertex_Buffer_Object)
 * without unnecessary repetition in code. */
@GLContext
fun vboData(buf: FloatBuffer, id: Int, index: Int, size: Int) {
    glBindBuffer(GL_ARRAY_BUFFER, id)
    glBufferData(
        GL_ARRAY_BUFFER, buf.capacity() * Float.SIZE_BYTES, buf, GL_STATIC_DRAW
    )
    glVertexAttribPointer(
        index, size, GL_FLOAT, false, size * Float.SIZE_BYTES, 0
    )
    glEnableVertexAttribArray(index)
}
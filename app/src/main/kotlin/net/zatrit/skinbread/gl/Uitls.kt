package net.zatrit.skinbread.gl

import android.opengl.GLES30.glGenVertexArrays
import net.zatrit.skinbread.GLContext
import java.nio.IntBuffer

/** Creates a 4x4 matrix and fills it according to [func]. */
inline fun mat4(func: (FloatArray) -> Unit) = FloatArray(16).also(func)

/** Allocates a buffer with capacity [size] and fills it according to [func]. */
inline fun buf(size: Int = 1, func: (IntBuffer) -> Unit): IntBuffer =
    IntBuffer.allocate(size).also(func)

/** Creates a new [OpenGL vertex array object](https://www.khronos.org/opengl/wiki/Vertex_Specification#Vertex_Array_Object). */
@GLContext
fun genVertexArray() = buf { glGenVertexArrays(1, it) }.get()
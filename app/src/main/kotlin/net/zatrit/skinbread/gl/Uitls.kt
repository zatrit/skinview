package net.zatrit.skinbread.gl

import android.opengl.GLES30.glGenVertexArrays
import java.nio.IntBuffer

inline fun mat4(func: (FloatArray) -> Unit) = FloatArray(16).also(func)

inline fun buf(size: Int = 1, func: (IntBuffer) -> Unit): IntBuffer =
    IntBuffer.allocate(size).also(func)

fun genVertexArray() = buf { glGenVertexArrays(1, it) }.get()
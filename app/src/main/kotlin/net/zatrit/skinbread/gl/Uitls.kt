package net.zatrit.skinbread.gl

import android.opengl.GLES30.glGenVertexArrays
import java.nio.IntBuffer
import kotlin.contracts.*

@OptIn(ExperimentalContracts::class)
inline fun mat4(func: (FloatArray) -> Unit): FloatArray {
    contract { callsInPlace(func, InvocationKind.EXACTLY_ONCE) }

    val mat = FloatArray(16)
    func(mat)
    return mat
}

@OptIn(ExperimentalContracts::class)
inline fun buf(size: Int = 1, func: (IntBuffer) -> Unit): IntBuffer {
    contract { callsInPlace(func, InvocationKind.EXACTLY_ONCE) }

    val buf = IntBuffer.allocate(size)
    func(buf)
    return buf
}

fun genVertexArray() = buf { glGenVertexArrays(1, it) }.get()
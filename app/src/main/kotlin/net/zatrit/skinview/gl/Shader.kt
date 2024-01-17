package net.zatrit.skinview.gl

import android.opengl.GLES31.*
import net.zatrit.skinview.DebugOnly
import java.nio.IntBuffer

@DebugOnly
private fun shaderStatus(id: Int) {
    val status = IntBuffer.allocate(1)
    glGetShaderiv(id, GL_COMPILE_STATUS, status)
    assert(status[0] != 0) { glGetShaderInfoLog(id) }
}

@DebugOnly
private fun programStatus(id: Int) {
    val status = IntBuffer.allocate(1)
    glGetProgramiv(id, GL_LINK_STATUS, status)
    assert(status[0] != 0) { glGetProgramInfoLog(id) }
}

fun compileShader(type: Int, source: String): Int {
    val id = glCreateShader(type)

    glShaderSource(id, source)
    glCompileShader(id)
    shaderStatus(id)

    return id
}

class Program(vararg shaders: Int) {
    private val id = glCreateProgram()

    init {
        shaders.forEach { glAttachShader(id, it) }
        glLinkProgram(id)
        shaders.forEach(::glDeleteShader)
        programStatus(id)
    }

    fun use() = glUseProgram(this.id)

    fun uniformLocation(uniform: String): Int =
        glGetUniformLocation(this.id, uniform)
}
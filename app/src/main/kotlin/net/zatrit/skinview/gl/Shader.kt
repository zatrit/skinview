package net.zatrit.skinview.gl

import android.opengl.GLES31.*
import net.zatrit.skinview.DebugOnly
import java.nio.IntBuffer

@DebugOnly
private fun shaderStatus(id: Int) {
    val compileStatus = IntBuffer.allocate(1)
    glGetShaderiv(id, GL_COMPILE_STATUS, compileStatus)
    assert(compileStatus[0] != 0) { glGetShaderInfoLog(id) }
}

@DebugOnly
private fun programStatus(id: Int) {
    val compileStatus = IntBuffer.allocate(1)
    glGetProgramiv(id, GL_LINK_STATUS, compileStatus)
    assert(compileStatus[0] != 0) { glGetProgramInfoLog(id) }
}

fun compileShader(type: Int, source: String): Int {
    val id = glCreateShader(type)
    glShaderSource(id, source)
    glCompileShader(id)
    shaderStatus(id)

    return id
}

@JvmInline
value class Program(private val id: Int) {
    fun use() = glUseProgram(this.id)

    fun uniformLocation(uniform: String): Int =
        glGetUniformLocation(this.id, uniform)
}

fun linkShaders(vararg shaders: Int): Program {
    val program = glCreateProgram()

    shaders.forEach { glAttachShader(program, it) }
    glLinkProgram(program)
    shaders.forEach { glDeleteShader(it) }
    programStatus(program)

    return Program(program)
}
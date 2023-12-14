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

class Shader(type: Int, source: String) {
    val id: Int

    init {
        id = glCreateShader(type)
        glShaderSource(id, source)
        glCompileShader(id)
        shaderStatus(id)
    }
}

class Program(private val id: Int) {
    fun use() = glUseProgram(this.id)

    fun uniformLocation(uniform: String): Int =
        glGetUniformLocation(this.id, uniform)
}

fun compileShader(vararg shaders: Shader): Program {
    val program: Int = glCreateProgram()

    shaders.forEach { glAttachShader(program, it.id) }
    glLinkProgram(program)
    shaders.forEach { glDeleteShader(it.id) }
    programStatus(program)

    return Program(program)
}
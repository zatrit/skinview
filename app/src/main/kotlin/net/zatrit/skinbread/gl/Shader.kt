package net.zatrit.skinbread.gl

import android.opengl.GLES30.*
import net.zatrit.skinbread.DebugOnly

@DebugOnly
private fun shaderStatus(id: Int) {
    val status = buf { glGetShaderiv(id, GL_COMPILE_STATUS, it) }.get()
    assert(status != 0) { glGetShaderInfoLog(id) }
}

@DebugOnly
private fun programStatus(id: Int) {
    val status = buf { glGetProgramiv(id, GL_LINK_STATUS, it) }.get()
    assert(status != 0) { glGetProgramInfoLog(id) }
}

fun compileShader(type: Int, source: String): Int {
    val id = glCreateShader(type)

    glShaderSource(id, source)
    glCompileShader(id)
    shaderStatus(id)

    return id
}

open class Program(shaders: IntArray) {
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

class MVPProgram(vararg shaders: Int) : Program(shaders) {
    val modelHandle = uniformLocation("uModel")
    val viewHandle = uniformLocation("uView")
    val projHandle = uniformLocation("uProj")
}
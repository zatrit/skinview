package net.zatrit.skinbread.gl

import android.opengl.GLES30.*
import net.zatrit.skinbread.*

/** Ensures that the shader was successfully compiled with [glGetShaderiv]. */
@DebugOnly
@GLContext
private fun shaderStatus(id: Int) {
    val status = buf { glGetShaderiv(id, GL_COMPILE_STATUS, it) }.get()
    assert(status != 0) { glGetShaderInfoLog(id) }
}

/** Ensures that the program was successfully linked with [glGetProgramiv]. */
@DebugOnly
@GLContext
private fun programStatus(id: Int) {
    val status = buf { glGetProgramiv(id, GL_LINK_STATUS, it) }.get()
    assert(status != 0) { glGetProgramInfoLog(id) }
}

/** A type-safe wrapper around [glCompileShader]. */
@GLContext
fun compileShader(type: Int, source: String): Int {
    val id = glCreateShader(type)

    glShaderSource(id, source)
    glCompileShader(id)
    shaderStatus(id)

    return id
}

/** A safe wrapper around [OpenGL shader program](https://www.khronos.org/opengl/wiki/Shader). */
@GLContext
open class Program(shaders: IntArray) {
    private val id = glCreateProgram()

    init {
        shaders.forEach { glAttachShader(id, it) }
        glLinkProgram(id)
        shaders.forEach(::glDeleteShader)
        programStatus(id)
    }

    /** Wraps [glUseProgram]. */
    fun use() = glUseProgram(this.id)

    /** Wraps [uniformLocation]. */
    fun uniformLocation(uniform: String): Int =
        glGetUniformLocation(this.id, uniform)
}

/**
 * A shader program that precalculates a [uniform](https://www.khronos.org/opengl/wiki/Uniform_(GLSL))
 * locations for [MVP](https://learnopengl.com/Getting-started/Coordinate-Systems) (model-view-projection)
 * matrices instead of recalculating them each time. */
@GLContext
class MVPProgram(vararg shaders: Int) : Program(shaders) {
    val modelHandle = uniformLocation("uModel")
    val viewHandle = uniformLocation("uView")
    val projHandle = uniformLocation("uProj")
}
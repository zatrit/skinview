package net.zatrit.skinview.gl

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import net.zatrit.skinview.DebugOnly
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@DebugOnly
@OptIn(ExperimentalStdlibApi::class)
private fun checkError() {
    val error = glGetError()
    assert(error == 0) { "OpenGL error: ${error.toHexString()}" }
}

class Renderer : GLSurfaceView.Renderer {
    var modelMatrix = mat4 { setIdentityM(it, 0) }

    private var texture: Texture? = null

    private lateinit var model: PlayerModel
    private lateinit var grid: Plain
    private lateinit var shaders: Array<MVPProgram>

    private lateinit var modelShader: MVPProgram
    private lateinit var gridShader: MVPProgram

    var options = RenderOptions()

    private inline fun allShaders(func: MVPProgram.() -> Unit) =
        shaders.forEach {
            it.use()
            it.func()
        }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glClearColor(0f, 0f, 0f, 1f)

        // Model creation code
        modelShader = MVPProgram(
            compileShader(GL_VERTEX_SHADER, MAIN_VERT),
            compileShader(GL_FRAGMENT_SHADER, MAIN_FRAG),
        ).apply { use() }

        glUniform1i(modelShader.uniformLocation("uTexture"), 0)

        options.shading = true
        model = PlayerModel(options)

        // Grid creation code
        gridShader = MVPProgram(
            compileShader(GL_VERTEX_SHADER, GRID_VERT),
            compileShader(GL_FRAGMENT_SHADER, GRID_FRAG),
        ).apply { use() }

        grid = Plain(-2f, -2f, 2f, 2f)

        // View matrix update
        shaders = arrayOf(modelShader, gridShader)

        val viewMatrix = mat4 {
            setIdentityM(it, 0)
            it[14] = -10f // sets Z offset to -10
        }

        val buf = FloatBuffer.wrap(viewMatrix)
        allShaders {
            glUniformMatrix4fv(viewHandle, 1, false, buf)
        }

        checkError()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        val projMatrix = mat4 { perspectiveM(it, 0, 45f, ratio, 0.1f, 100f) }

        val buf = FloatBuffer.wrap(projMatrix)
        allShaders {
            glUniformMatrix4fv(projHandle, 1, false, buf)
        }

        checkError()
    }

    override fun onDrawFrame(gl: GL10) {
        // Update uShade value if options.shade changed
        if (options.shadingChanged) {
            val shadeInt = if (options.shading) 1 else 0
            modelShader.use()
            glUniform1i(
                modelShader.uniformLocation("uShade"), shadeInt
            )

            options.shadingChanged = false
        }

        // Replace current texture with another if requested
        options.pendingTexture?.let {
            texture?.delete()
            texture = Texture(it)

            options.pendingTexture = null
        }

        val buf = FloatBuffer.wrap(modelMatrix)

        allShaders {
            glUniformMatrix4fv(modelHandle, 1, false, buf)
        }

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        modelShader.use()
        model.draw()

        if (options.showGrid) {
            gridShader.use()
            grid.draw()
        }

        checkError()
    }
}

package net.zatrit.skinview.gl

import android.content.Context
import android.opengl.GLES31.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.perspectiveM
import android.opengl.Matrix.setIdentityM
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

class Renderer(private val context: Context) : GLSurfaceView.Renderer {
    var modelMatrix = mat4 { setIdentityM(it, 0) }

    private lateinit var model: PlayerModel
    private lateinit var grid: Plain
    private lateinit var shaders: Array<Program>

    private lateinit var modelShader: Program
    private lateinit var gridShader: Program

    private inline fun allShaders(func: Program.() -> Unit) = shaders.forEach {
        it.use()
        it.func()
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glClearColor(0f, 0f, 0f, 1.0f)

        // Model creation code
        modelShader = Program(
            compileShader(GL_VERTEX_SHADER, MAIN_VERT),
            compileShader(GL_FRAGMENT_SHADER, MAIN_FRAG),
        ).apply { use() }

        Texture(context.assets.open("zatrit.png"))
        glUniform1i(modelShader.uniformLocation("uTexture"), 0)

        model = PlayerModel(ModelType.SLIM)

        // Grid creation code
        gridShader = Program(
            compileShader(GL_VERTEX_SHADER, GRID_VERT),
            compileShader(GL_FRAGMENT_SHADER, GRID_FRAG),
        ).apply { use() }

        glUniform4f(gridShader.uniformLocation("uColor"), 1f, 1f, 1f, 0.5f)
        glUniform1f(gridShader.uniformLocation("uHeight"), -2f)

        grid = Plain(-2f, -2f, 2f, 2f)

        // View matrix update
        shaders = arrayOf(modelShader, gridShader)

        val viewMatrix = mat4 {
            setIdentityM(it, 0)
            it[14] = -10f // sets Z offset to -10
        }

        val buf = FloatBuffer.wrap(viewMatrix)
        allShaders {
            glUniformMatrix4fv(
                uniformLocation("uView"), 1, false, buf
            )
        }

        checkError()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        val projMatrix = mat4 { perspectiveM(it, 0, 45.0f, ratio, 0.1f, 100f) }

        val buf = FloatBuffer.wrap(projMatrix)
        allShaders {
            glUniformMatrix4fv(
                uniformLocation("uProj"), 1, false, buf
            )
        }

        checkError()
    }

    override fun onDrawFrame(gl: GL10) {
        val buf = FloatBuffer.wrap(modelMatrix)
        allShaders {
            glUniformMatrix4fv(
                uniformLocation("uModel"), 1, false, buf
            )
        }

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        modelShader.use()
        model.draw()

        gridShader.use()
        grid.draw()

        checkError()
    }
}

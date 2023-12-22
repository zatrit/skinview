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
    private var projHandle = 0
    private var modelHandle = 0

    var modelMatrix = mat4 { setIdentityM(it, 0) }
    private lateinit var model: PlayerModel

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glClearColor(0f, 0f, 0f, 1.0f)

        val shader = linkShaders(
            compileShader(GL_VERTEX_SHADER, MAIN_VERT),
            compileShader(GL_FRAGMENT_SHADER, MAIN_FRAG),
        ).apply { use() }

        projHandle = shader.uniformLocation("uProj")
        modelHandle = shader.uniformLocation("uModel")

        val viewMatrix = mat4 {
            setIdentityM(it, 0)
            it[14] = -10f // sets Z offset to -10
        }
        glUniformMatrix4fv(
            shader.uniformLocation("uView"), 1, false,
            FloatBuffer.wrap(viewMatrix)
        )

        model = PlayerModel(ModelType.SLIM)

        loadTexture(context.assets.open("zatrit.png"))
        glUniform1i(shader.uniformLocation("uTexture"), 0)

        checkError()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        val projMatrix = mat4 { perspectiveM(it, 0, 45.0f, ratio, 0.1f, 100f) }
        glUniformMatrix4fv(projHandle, 1, false, FloatBuffer.wrap(projMatrix))

        checkError()
    }

    override fun onDrawFrame(gl: GL10) {
        glUniformMatrix4fv(modelHandle, 1, false, FloatBuffer.wrap(modelMatrix))
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        model.draw()
        checkError()
    }
}

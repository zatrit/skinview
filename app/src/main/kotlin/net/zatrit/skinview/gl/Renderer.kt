package net.zatrit.skinview.gl

import android.content.Context
import android.opengl.GLES31.*
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
    assert(error == 0) { error.toHexString() }
}

class Renderer(private val context: Context) : GLSurfaceView.Renderer {
    private var projHandle: Int = 0
    private var viewHandle: Int = 0
    private var modelHandle: Int = 0

    var modelMatrix: FloatArray = mat4 { setIdentityM(it, 0) }
    private lateinit var model: PlayerModel

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)

        val shader = linkShaders(
            compileShader(GL_VERTEX_SHADER, MAIN_VERT),
            compileShader(GL_FRAGMENT_SHADER, MAIN_FRAG),
        ).apply { use() }

        projHandle = shader.uniformLocation("uProj")
        viewHandle = shader.uniformLocation("uView")
        modelHandle = shader.uniformLocation("uModel")

        model = PlayerModel(ModelType.SLIM)

        loadTexture(context.assets.open("zatrit.png"))
        shader.uniformLocation("uTexture").also {
            glUniform1i(it, 0)
        }

        val viewMatrix = mat4 {
            setIdentityM(it, 0)
            translateM(it, 0, 0f, 0f, -10f)
        }
        glUniformMatrix4fv(viewHandle, 1, false, FloatBuffer.wrap(viewMatrix))
        glUniformMatrix4fv(modelHandle, 1, false, FloatBuffer.wrap(modelMatrix))

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
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        model.draw()
        checkError()
    }
}

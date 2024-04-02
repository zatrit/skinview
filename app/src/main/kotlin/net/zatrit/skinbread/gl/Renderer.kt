package net.zatrit.skinbread.gl

import android.graphics.Color.*
import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.model.*
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
    var viewMatrix = mat4 {
        setIdentityM(it, 0)
        it[14] = -10f // sets Z offset to -10
    }

    private var textures = GLTextures()

    private lateinit var playerModel: PlayerModel
    private lateinit var capeModel: ModelPart
    private lateinit var elytraModel: ElytraModel
    private lateinit var earsModel: EarsModel

    private lateinit var grid: Plain

    private lateinit var shaders: Array<MVPProgram>

    private lateinit var modelShader: MVPProgram
    private lateinit var gridShader: MVPProgram

    lateinit var options: RenderOptions

    private inline fun allShaders(func: MVPProgram.() -> Unit) =
        shaders.forEach {
            it.use()
            it.func()
        }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)

        // Model creation code
        modelShader = MVPProgram(
            compileShader(GL_VERTEX_SHADER, MAIN_VERT),
            compileShader(GL_FRAGMENT_SHADER, MAIN_FRAG),
        ).apply { use() }

        glUniform1i(modelShader.uniformLocation("uTexture"), 0)

        playerModel = PlayerModel()
        capeModel = capeModel()
        elytraModel = ElytraModel()
        earsModel = EarsModel()

        // Grid creation code
        gridShader = MVPProgram(
            compileShader(GL_VERTEX_SHADER, GRID_VERT),
            compileShader(GL_FRAGMENT_SHADER, GRID_FRAG),
        ).apply { use() }

        val buf = FloatBuffer.wrap(identity)
        glUniformMatrix4fv(gridShader.modelHandle, 1, false, buf)

        grid = Plain(-2f, -2f, 2f, 2f)

        // View matrix update
        shaders = arrayOf(modelShader, gridShader)

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
        val shadeInt = if (options.shading) 1 else 0
        with(modelShader) {
            use()
            glUniform1i(uniformLocation("uShade"), shadeInt)

            options.background.let {
                val r = red(it)
                val g = green(it)
                val b = blue(it)
                glClearColor(r, g, b, 1f)
                glUniform4f(uniformLocation("uShadeColor"), r, g, b, 1f)
            }
        }

        // Replace current textures with another if requested
        options.pendingTextures?.run {
            textures.delete()
            textures = load()
            playerModel.modelType = model ?: ModelType.DEFAULT
            options.pendingTextures = null
        }

        val buf = FloatBuffer.wrap(viewMatrix)
        allShaders {
            glUniformMatrix4fv(viewHandle, 1, false, buf)
        }

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        modelShader.use()

        val modelHandle = modelShader.modelHandle
        val identityBuf = FloatBuffer.wrap(identity)
        glUniformMatrix4fv(modelHandle, 1, false, identityBuf)

        textures.skin.takeIf { options.skin }?.run {
            bind()
            playerModel.draw()
        }

        textures.cape.takeIf { options.cape }?.run {
            bind()

            if (options.elytra) {
                elytraModel.drawRotated(modelHandle)
            } else {
                val capeBuf = FloatBuffer.wrap(capeMatrix)
                glUniformMatrix4fv(modelHandle, 1, false, capeBuf)
                capeModel.draw()
            }
        }

        textures.ears.takeIf { options.ears }?.run {
            bind()
            earsModel.drawRotated(modelHandle)
        }

        if (options.grid) {
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR)
            gridShader.use()
            grid.draw()
        }

        checkError()
    }
}

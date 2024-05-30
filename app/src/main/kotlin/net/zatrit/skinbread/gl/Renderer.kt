package net.zatrit.skinbread.gl

import android.graphics.Color.blue
import android.graphics.Color.green
import android.graphics.Color.red
import android.opengl.GLES30.GL_BLEND
import android.opengl.GLES30.GL_COLOR_BUFFER_BIT
import android.opengl.GLES30.GL_DEPTH_BUFFER_BIT
import android.opengl.GLES30.GL_DEPTH_TEST
import android.opengl.GLES30.GL_FRAGMENT_SHADER
import android.opengl.GLES30.GL_ONE_MINUS_DST_COLOR
import android.opengl.GLES30.GL_ONE_MINUS_SRC_ALPHA
import android.opengl.GLES30.GL_ONE_MINUS_SRC_COLOR
import android.opengl.GLES30.GL_SRC_ALPHA
import android.opengl.GLES30.GL_VERTEX_SHADER
import android.opengl.GLES30.glBlendFunc
import android.opengl.GLES30.glClear
import android.opengl.GLES30.glClearColor
import android.opengl.GLES30.glEnable
import android.opengl.GLES30.glGetError
import android.opengl.GLES30.glUniform1i
import android.opengl.GLES30.glUniform4f
import android.opengl.GLES30.glUniformMatrix4fv
import android.opengl.GLES30.glViewport
import android.opengl.GLSurfaceView
import android.opengl.Matrix.perspectiveM
import android.opengl.Matrix.setIdentityM
import net.zatrit.skinbread.DebugOnly
import net.zatrit.skinbread.GLContext
import net.zatrit.skinbread.capeMatrix
import net.zatrit.skinbread.gl.model.EarsModel
import net.zatrit.skinbread.gl.model.ElytraModel
import net.zatrit.skinbread.gl.model.ModelType
import net.zatrit.skinbread.gl.model.PlayerModel
import net.zatrit.skinbread.gl.model.capeModel
import net.zatrit.skinbread.identity
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@DebugOnly
@OptIn(ExperimentalStdlibApi::class)
private fun checkError() {
    val error = glGetError()
    assert(error == 0) { "OpenGL error: ${error.toHexString()}" }
}

@OptIn(GLContext::class)
class Renderer : GLSurfaceView.Renderer {
    var viewMatrix = mat4 {
        setIdentityM(it, 0)
        it[14] = -10f // sets Z offset to -10
    }

    private var textures = GLTextures()
    private var defaultTextures = GLTextures()

    private lateinit var playerModel: PlayerModel
    private lateinit var capeModel: ModelPart
    private lateinit var elytraModel: ElytraModel
    private lateinit var earsModel: EarsModel

    private lateinit var grid: Plain

    private lateinit var shaders: Array<MVPProgram>

    private lateinit var modelShader: MVPProgram
    private lateinit var gridShader: MVPProgram

    lateinit var config: RenderConfig

    @GLContext
    private inline fun allShaders(func: MVPProgram.() -> Unit) =
        shaders.forEach {
            it.use()
            it.func()
        }

    @GLContext
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

    @GLContext
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

    @GLContext
    override fun onDrawFrame(gl: GL10) {
        val shadeInt = if (config.shading) 1 else 0

        with(modelShader) {
            use()
            glUniform1i(uniformLocation("uShade"), shadeInt)

            // Sets the background color and shading
            config.background.let {
                val r = red(it)
                val g = green(it)
                val b = blue(it)
                glClearColor(r, g, b, 1f)
                glUniform4f(uniformLocation("uShadeColor"), r, g, b, 1f)
            }
        }

        config.pendingDefaultTextures?.run {
            defaultTextures.assertEmpty()
            defaultTextures = load(persistent = true)
            defaultTextures.printInfo()

            config.pendingDefaultTextures = null
        }

        if (config.clearTextures) {
            textures.delete()
            textures = defaultTextures.clone()
            playerModel.modelType = ModelType.DEFAULT
            config.clearTextures = false
        }

        while (config.pendingTextures.isNotEmpty()) {
            val textures = config.pendingTextures.poll() ?: continue

            if (textures.skin != null) {
                playerModel.modelType = textures.model ?: ModelType.DEFAULT
            }

            this.textures.populate(textures.load())
            this.textures.printInfo()
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

        textures.skin?.run {
            bind()
            playerModel.render()
        }

        textures.cape?.run {
            bind()

            if (config.elytra) elytraModel.renderRotated(modelHandle)
            else {
                val capeBuf = FloatBuffer.wrap(capeMatrix)
                glUniformMatrix4fv(modelHandle, 1, false, capeBuf)
                capeModel.render()
            }
        }

        textures.ears?.run {
            bind()
            earsModel.renderRotated(modelHandle)
        }

        if (config.grid) {
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR)
            gridShader.use()
            grid.render()
        }

        checkError()
    }
}

package zatrit.skinbread.gl

import android.graphics.Color.*
import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.*
import zatrit.skinbread.*
import zatrit.skinbread.gl.model.*
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/** Asserts that there are no [OpenGL errors](https://www.khronos.org/opengl/wiki/GL_error_codes). */
@DebugOnly
@OptIn(ExperimentalStdlibApi::class)
private fun checkError() {
    val error = glGetError()
    assert(error == 0) { "OpenGL error: ${error.toHexString()}" }
}

/** 3D player model renderer. */
@OptIn(GLContext::class)
class Renderer(private val default: Textures) : GLSurfaceView.Renderer {
    /** A matrix describing the position of the camera. */
    var viewMatrix = mat4 {
        setIdentityM(it, 0)
        it[14] = -10f // sets Z offset to -10
    }

    /** The set of textures used for rendering. */
    private var textures = GLTextures()

    /** Textures used to fill in fields not present in [textures]. */
    private lateinit var defaultTextures: GLTextures

    /** Player model for rendering. */
    private lateinit var playerModel: PlayerModel

    /** Cape model for rendering. */
    private lateinit var capeModel: ModelPart

    /** Elytra model for rendering. */
    private lateinit var elytraModel: ElytraModel

    /** Ear model for rendering. */
    private lateinit var earsModel: EarsModel

    /** A grid drawing underneath the [playerModel]. */
    private lateinit var grid: Plain

    /** An array of all MVP shaders. */
    private lateinit var shaders: Array<MVPProgram>

    /** A shader used to render models. */
    private lateinit var modelShader: MVPProgram

    /** The shader used to render the grid. */
    private lateinit var gridShader: MVPProgram

    /** Config of the [Renderer] to allow interaction with it externally. */
    lateinit var config: RenderConfig

    /** Executes [func] for each shader in [shaders]. */
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

        // Loads default textures and clears resources
        defaultTextures = default.load(persistent = true)
        default.run {
            skin?.recycle()
            cape?.recycle()
            ears?.recycle()
        }

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
        val projMatrix = mat4 { perspectiveM(it, 0, 45f, ratio, 5f, 15f) }

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
        // Fixes blending for transparent skin parts
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        modelShader.use()

        val modelHandle = modelShader.modelHandle

        textures.skin?.run {
            // Resets modelMatrix in order to draw player model
            val identityBuf = FloatBuffer.wrap(identity)
            glUniformMatrix4fv(modelHandle, 1, false, identityBuf)

            bind()
            playerModel.render()
        }

        textures.cape?.run {
            bind()
            if (config.elytra) elytraModel.renderRotated(modelHandle)
            else capeModel.renderRotated(modelHandle, capeMatrix)
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

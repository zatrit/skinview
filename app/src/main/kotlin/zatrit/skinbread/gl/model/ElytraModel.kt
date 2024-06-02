package zatrit.skinbread.gl.model

import android.opengl.GLES20.glUniformMatrix4fv
import zatrit.skinbread.*
import zatrit.skinbread.gl.*
import java.nio.FloatBuffer

@GLContext
class ElytraModel {
    private val wing: ModelPart

    init {
        val box = Box(-0.625f, -1.25f, -0.25f, 0.625f, 1.25f, 0.25f)
        val uv = box.scale(z = 0.5f).uv(22f / 64f, 0f, ratio = 2f)
        wing = ModelPart(box.vertices, uv)
    }

    fun renderRotated(modelHandle: Int) {
        wing.renderRotated(modelHandle, leftWingMatrix)
        wing.renderRotated(modelHandle, rightWingMatrix)
    }
}

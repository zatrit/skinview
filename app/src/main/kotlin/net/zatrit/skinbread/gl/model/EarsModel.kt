package net.zatrit.skinbread.gl.model

import android.opengl.GLES20.glUniformMatrix4fv
import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.*
import java.nio.FloatBuffer

class EarsModel {
    private val ear: ModelPart

    init {
        val side = 0.9375f
        val box = Box(0f, 0f, 0f, side, side, 0.15625f)
        val uv = box.uv(0f, 0f, scale = 4f / 7f / 1.25f, ratio = 2f)
        ear = ModelPart(box.vertices, uv)
    }

    fun drawRotated(modelHandle: Int) {
        val leftBuf = FloatBuffer.wrap(leftEarMatrix)
        glUniformMatrix4fv(modelHandle, 1, false, leftBuf)
        ear.draw()

        val rightBuf = FloatBuffer.wrap(rightEarMatrix)
        glUniformMatrix4fv(modelHandle, 1, false, rightBuf)
        ear.draw()
    }
}

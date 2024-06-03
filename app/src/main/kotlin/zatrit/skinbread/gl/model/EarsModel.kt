package zatrit.skinbread.gl.model

import zatrit.skinbread.*
import zatrit.skinbread.gl.*

/** A model of the player's two ears that uses [leftEarMatrix] and [rightEarMatrix] for rendering. */
@GLContext
class EarsModel {
    private val ear: ModelPart

    init {
        val side = 0.9375f
        val box = Box(0f, 0f, 0f, side, side, 0.15625f)
        val uv = box.uv(0f, 0f, scale = 4f / 7f / 1.25f, ratio = 2f)
        ear = ModelPart(box.vertices, uv)
    }

    /** Draws ears by rotating [modelHandle] in a certain way. */
    fun renderRotated(modelHandle: Int) {
        ear.renderRotated(modelHandle, leftEarMatrix)
        ear.renderRotated(modelHandle, rightEarMatrix)
    }
}

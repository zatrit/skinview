package zatrit.skinbread.gl.model

import zatrit.skinbread.*
import zatrit.skinbread.gl.*

/**
 * A model of the player's elytra that uses [leftWingMatrix]
 * and [rightWingMatrix] for rendering. */
@GLContext
class ElytraModel {
    private val wing: ModelPart

    init {
        val box = Box(-0.625f, -1.25f, -0.25f, 0.625f, 1.25f, 0.25f)
        val uv = box.scale(z = 0.5f).uv(22f / 64f, 0f, ratio = 2f)
        wing = ModelPart(box.vertices, uv)
    }

    /** Draws elytra wings by rotating [modelHandle] in a certain way. */
    fun renderRotated(modelHandle: Int) {
        wing.renderRotated(modelHandle, leftWingMatrix)
        wing.renderRotated(modelHandle, rightWingMatrix)
    }
}

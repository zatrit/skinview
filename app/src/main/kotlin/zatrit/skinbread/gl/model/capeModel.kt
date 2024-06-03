package zatrit.skinbread.gl.model

import zatrit.skinbread.GLContext
import zatrit.skinbread.gl.*

/** @return ModelPart used to render the cloak. */
@GLContext
fun capeModel(): ModelPart {
    val cape = Box(-0.625f, -1f, -0.0625f, 0.625f, 1f, 0.0625f)
    return ModelPart(cape.vertices, cape.uv(0f, 0f, ratio = 2f))
}
package net.zatrit.skinbread.gl.model

import net.zatrit.skinbread.GLContext
import net.zatrit.skinbread.gl.Box
import net.zatrit.skinbread.gl.ModelPart

@GLContext
fun capeModel(): ModelPart {
    val cape = Box(-0.625f, -1f, -0.0625f, 0.625f, 1f, 0.0625f)
    return ModelPart(cape.vertices, cape.uv(0f, 0f, ratio = 2f))
}
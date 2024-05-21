package net.zatrit.skinbread.gl

import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.model.ModelType

class GLTexturePicker {
    private var skinOrder = Int.MAX_VALUE
    private var capeOrder = Int.MAX_VALUE
    private var earsOrder = Int.MAX_VALUE

    fun reset() {
        skinOrder = Int.MAX_VALUE
        capeOrder = Int.MAX_VALUE
        earsOrder = Int.MAX_VALUE
    }

    @GLContext
    fun update(
        textures: GLTextures, ordered: OrderedTextures): ModelType? {
        var model: ModelType? = null
        val input = ordered.textures
        val order = ordered.order

        input.skin?.takeIf { order < skinOrder }?.let {
            skinOrder = order
            textures.skin?.delete()
            textures.skin = GLTexture(it)
            model = input.model ?: ModelType.DEFAULT
        }

        input.cape?.takeIf { order < capeOrder }?.let {
            capeOrder = order
            textures.cape?.delete()
            textures.cape = GLTexture(it)
        }

        input.ears?.takeIf { order < earsOrder }?.let {
            earsOrder = order
            textures.ears?.delete()
            textures.ears = GLTexture(it)
        }

        return model
    }
}
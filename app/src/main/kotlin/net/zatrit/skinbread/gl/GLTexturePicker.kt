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
        textures: GLTextures,
        input: OrderedTextures): ModelType? {
        var model: ModelType? = null

        input.textures.skin?.let(::GLTexture).takeIf { input.order < skinOrder }
            ?.also {
                skinOrder = input.order
                textures.skin?.delete()
                textures.skin = it
                model = input.textures.model ?: ModelType.DEFAULT
            }

        input.textures.cape?.let(::GLTexture).takeIf { input.order < capeOrder }
            ?.also {
                capeOrder = input.order
                textures.cape?.delete()
                textures.cape = it
            }

        input.textures.ears?.let(::GLTexture).takeIf { input.order < earsOrder }
            ?.also {
                earsOrder = input.order
                textures.ears?.delete()
                textures.ears = it
            }

        return model
    }
}
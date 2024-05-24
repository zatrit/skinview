package net.zatrit.skinbread

const val LOCAL = -1

class TexturePicker {
    private var skinOrder = Int.MAX_VALUE
    private var capeOrder = Int.MAX_VALUE
    private var earsOrder = Int.MAX_VALUE

    fun reset() {
        skinOrder = Int.MAX_VALUE
        capeOrder = Int.MAX_VALUE
        earsOrder = Int.MAX_VALUE
    }

    fun update(input: Textures, order: Int): Textures {
        val textures = Textures()

        input.skin?.takeIf { order <= skinOrder }?.let {
            skinOrder = order
            textures.skin = it
            textures.model = input.model
        }

        input.cape?.takeIf { order <= capeOrder }?.let {
            capeOrder = order
            textures.cape = it
        }

        input.ears?.takeIf { order <= earsOrder }?.let {
            earsOrder = order
            textures.ears = it
        }

        return textures
    }
}
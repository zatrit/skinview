package net.zatrit.skinbread

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

const val LOCAL = 0
const val VANILLA = 1

/** A class that automatically takes parts of [Textures] depending on the order. */
@Parcelize
class TexturePicker(
    private var skinOrder: Int = Int.MAX_VALUE,
    private var capeOrder: Int = Int.MAX_VALUE,
    private var earsOrder: Int = Int.MAX_VALUE,
) : Parcelable {
    /**
     * Resets values to [Int.MAX_VALUE] so that all incoming
     * items overwrite existing items. */
    fun reset() {
        skinOrder = Int.MAX_VALUE
        capeOrder = Int.MAX_VALUE
        earsOrder = Int.MAX_VALUE
    }

    /**
     * Creates new [Textures], leaving only those elements from
     * [input] that match the stored order. */
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
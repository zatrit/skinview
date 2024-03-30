package net.zatrit.skins.lib

import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.TypedTexture

/**
 * Basic implementation of the player textures.
 *
 * @see PlayerTextures
 */
open class BasePlayerTextures<T : Texture?>(
    private val map: Map<TextureType, T>) : PlayerTextures {

    override fun hasTexture(type: TextureType) = map.containsKey(type)

    override fun getTexture(
        type: TextureType, layers: List<Layer<TypedTexture>>): TypedTexture? {
        if (!hasTexture(type)) {
            return null
        }

        val texture = map[type]!!
        val typedTexture = TypedTexture(texture, type)

        if (layers.isEmpty()) {
            return typedTexture
        }

        // https://stackoverflow.com/a/44521687/12245612
        val layer = layers.reduceOrNull { layer1, layer2 ->
            layer1.andThen(layer2)
        } ?: layers.first()

        return layer.apply(typedTexture)
    }
}

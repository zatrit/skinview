package zatrit.skins.lib

import zatrit.skins.lib.api.Texture

/**
 * Basic implementation of the player textures.
 */
class PlayerTextures(private val map: Map<TextureType, Texture>) {
    val isEmpty get() = map.isEmpty()

    fun getTexture(type: TextureType) = map[type]
}
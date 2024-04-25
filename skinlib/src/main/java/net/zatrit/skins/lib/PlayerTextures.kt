package net.zatrit.skins.lib

import net.zatrit.skins.lib.api.Texture
import net.zatrit.skins.lib.data.TypedTexture

/**
 * Basic implementation of the player textures.
 */
class PlayerTextures(
    val map: Map<TextureType, Texture>) {

    fun getTexture(type: TextureType): TypedTexture? {
        val texture = map[type] ?: return null
        return TypedTexture(texture, type)
    }
}

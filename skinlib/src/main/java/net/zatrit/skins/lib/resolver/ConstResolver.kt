package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import java.util.EnumMap

class ConstResolver : Resolver {
    val map = EnumMap<TextureType, Texture>(TextureType::class.java)
    private val textures = PlayerTextures(map)

    override fun resolve(profile: Profile) = textures
}
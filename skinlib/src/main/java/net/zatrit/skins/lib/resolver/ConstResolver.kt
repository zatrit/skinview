package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.texture.BytesTexture
import java.util.EnumMap

class ConstResolver : Resolver {
    val map = EnumMap<_, BytesTexture>(TextureType::class.java)
    private val textures = BasePlayerTextures(map)

    override fun resolve(profile: Profile) = textures
}
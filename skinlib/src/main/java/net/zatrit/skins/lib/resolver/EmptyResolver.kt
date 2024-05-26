package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.api.*

class EmptyResolver : Resolver {
    private val textures = PlayerTextures(mapOf())

    override fun resolve(profile: Profile) = textures
}
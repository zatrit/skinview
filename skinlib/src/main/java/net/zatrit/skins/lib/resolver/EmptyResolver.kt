package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.api.*

class EmptyResolver : Resolver {
    override fun resolve(profile: Profile) = throw Exception()
}
package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.api.Resolver

/** Empty implementation of [Resolver] throwing an [Exception] when trying to use it. */
class EmptyResolver : Resolver {
    override fun resolve(profile: Profile) = throw Exception()
}
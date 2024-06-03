package zatrit.skins.lib.resolver

import zatrit.skins.lib.api.*

/** Empty implementation of [Resolver] throwing an [Exception] when trying to use it. */
class EmptyResolver : Resolver {
    override fun resolve(profile: Profile) = throw Exception()
}
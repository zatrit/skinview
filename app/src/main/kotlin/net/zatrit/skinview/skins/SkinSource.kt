package net.zatrit.skinview.skins

import net.zatrit.skins.lib.api.Resolver
import net.zatrit.skins.lib.resolver.*
import net.zatrit.skins.lib.resolver.capes.*
import net.zatrit.skinview.DebugOnly
import net.zatrit.skinview.skins.ResolverType.*

enum class ResolverType {
    FIVE_ZIG,
    LIQUID_BOUNCE,
    METEOR,
    OPTIFINE,
    WURST,
    CONST,
    DIRECT,
    GEYSER,
    MINECRAFT_CAPES,
    MOJANG,
    NAMED_HTTP,
    VALHALLA
}

class SkinSource(
    val name: String, val resolver: Resolver, private val type: ResolverType) {

    init {
        checkType()
    }

    @DebugOnly
    fun checkType() = assert(
        when (type) {
            FIVE_ZIG -> FiveZigResolver::class
            LIQUID_BOUNCE -> LiquidBounceResolver::class
            METEOR -> MeteorResolver::class
            OPTIFINE -> OptifineResolver::class
            WURST -> WurstResolver::class
            CONST -> ConstResolver::class
            DIRECT -> DirectResolver::class
            GEYSER -> GeyserResolver::class
            MINECRAFT_CAPES -> MinecraftCapesResolver::class
            MOJANG -> MojangResolver::class
            NAMED_HTTP -> NamedHTTPResolver::class
            VALHALLA -> ValhallaResolver::class
        }.isInstance(resolver)
    )
}
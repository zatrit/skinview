package net.zatrit.skinbread.skins

import net.zatrit.skins.lib.api.Resolver

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
    private val type: ResolverType, val name: String, val resolver: Resolver)
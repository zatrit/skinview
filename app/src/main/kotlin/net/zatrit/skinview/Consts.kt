package net.zatrit.skinview

import net.zatrit.skins.lib.resolver.*
import net.zatrit.skins.lib.resolver.capes.FiveZigResolver
import net.zatrit.skinview.skins.ResolverType.*
import net.zatrit.skinview.skins.SkinSource

const val TAG = "SkinView"

val defaultSources = arrayOf(
    // ely.by skin system
    SkinSource(
        NAMED_HTTP, "ely.by",
        NamedHTTPResolver("http://skinsystem.ely.by/textures/")
    ),
    // official skin system
    SkinSource(MOJANG, "Mojang", MojangResolver()),
    // 5zig mod capes
    SkinSource(FIVE_ZIG, "5zig", FiveZigResolver())
)
package net.zatrit.skinbread.skins

import net.zatrit.skinbread.R
import net.zatrit.skins.lib.TextureType
import net.zatrit.skins.lib.api.Resolver
import net.zatrit.skins.lib.resolver.DirectResolver
import net.zatrit.skins.lib.resolver.EmptyResolver
import net.zatrit.skins.lib.resolver.GeyserResolver
import net.zatrit.skins.lib.resolver.MinecraftCapesResolver
import net.zatrit.skins.lib.resolver.MojangResolver
import net.zatrit.skins.lib.resolver.NamedHTTPResolver
import net.zatrit.skins.lib.resolver.capes.FiveZigResolver
import net.zatrit.skins.lib.resolver.capes.LiquidBounceResolver
import net.zatrit.skins.lib.resolver.capes.MeteorResolver
import net.zatrit.skins.lib.resolver.capes.OptifineResolver
import net.zatrit.skins.lib.resolver.capes.WurstResolver
import java.util.UUID

class SkinSource(val name: SourceName, val resolver: Resolver) {
    constructor(name: String, resolver: Resolver) : this(
      ConstName(name), resolver
    )

    constructor(id: Int, resolver: Resolver) : this(ResName(id), resolver)
}

val defaultSources = arrayOf(
  // Local
  SkinSource(R.string.source_local, EmptyResolver()),
  // Vanilla (Mojang)
  SkinSource(R.string.source_vanilla, MojangResolver()),

  // 5zig
  SkinSource("5zig", FiveZigResolver()),
  // Cloaks+
  SkinSource(
    "Cloaks+", OptifineResolver("https://server.cloaksplus.com")
  ),
  // Cosmetica
  SkinSource("Cosmetica", object : DirectResolver(TextureType.CAPE) {
      override fun getUrl(
        type: TextureType, id: UUID, name: String, shortId: String) =
          "https://api.cosmetica.cc/get/cloak?username=$name&nothirdparty"
  }),
  // ely.by
  SkinSource(
    "ely.by", NamedHTTPResolver("http://skinsystem.ely.by/textures/")
  ),
  // Geyser
  SkinSource("GeyserMC", GeyserResolver()),
  // LabyMod
  SkinSource("LabyMod", object : DirectResolver(TextureType.CAPE) {
      override fun getUrl(
        type: TextureType, id: UUID, name: String, shortId: String) =
          "https://dl.labymod.net/capes/$id"

  }),
  // LiquidBounce
  SkinSource("LiquidBounce", LiquidBounceResolver()),
  // mantle.gg
  SkinSource("Mantle", OptifineResolver("http://35.190.10.249")),
  // Meteor client
  SkinSource("Meteor", MeteorResolver()),
  // MinecraftCapes
  SkinSource("MinecraftCapes", MinecraftCapesResolver()),
  // Optifine
  SkinSource("Optifine", OptifineResolver("http://s.optifine.net")),
  // TLauncher
  SkinSource(
    "TLauncher", NamedHTTPResolver(
    "https://auth.tlauncher.org/skin/profile/texture/login/"
  )
  ),
  // Wurst client
  SkinSource("Wurst", WurstResolver()),
  // Wynntils
  SkinSource("Wynntils", object : DirectResolver(TextureType.CAPE) {
      override fun getUrl(
        type: TextureType, id: UUID, name: String, shortId: String) =
          "https://athena.wynntils.com/capes/user/$id"
  }),
)
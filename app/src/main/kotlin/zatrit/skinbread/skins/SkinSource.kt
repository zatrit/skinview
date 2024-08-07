package zatrit.skinbread.skins

import zatrit.skinbread.R
import zatrit.skins.lib.api.Resolver
import zatrit.skins.lib.resolver.*
import zatrit.skins.lib.resolver.capes.*
import java.util.UUID

/** A skin source that has [name] and [resolver]. */
class SkinSource(val name: SourceName, val resolver: Resolver) {
    constructor(name: String, resolver: Resolver) : this(
      ConstName(name), resolver
    )

    constructor(id: Int, resolver: Resolver) : this(ResName(id), resolver)
}

/** Default source array used to load skins. */
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
  // ely.by
  SkinSource(
    "ely.by", NamedHTTPResolver("http://skinsystem.ely.by/textures/")
  ),
  // Geyser
  SkinSource("GeyserMC", GeyserResolver()),
  // LabyMod
  SkinSource("LabyMod", object : DirectResolver() {
      override fun getUrl(id: UUID, name: String, shortId: String) =
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
  SkinSource("Wynntils", object : DirectResolver() {
      override fun getUrl(id: UUID, name: String, shortId: String) =
        "https://athena.wynntils.com/capes/user/$id"
  }),
)
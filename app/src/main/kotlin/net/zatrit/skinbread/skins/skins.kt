package net.zatrit.skinbread.skins

import net.zatrit.skinbread.*
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.layer.android.*
import java.util.concurrent.CompletableFuture.supplyAsync

val capeLayer = ScaleCapeLayer()
val skinLayer = LegacySkinLayer()

fun loadTexturesAsync(profile: Profile, source: SkinSource) = supplyAsync {
    try {
        source.resolver.resolve(profile)
    } catch (ex: Exception) {
        ex.printDebug()
        null
    }
}!!

fun mergeTextures(inputs: List<Textures>): Textures {
    val textures = Textures()
    val iterator = inputs.iterator()

    while (iterator.hasNext() && !textures.isComplete()) {
        textures.or(iterator.next())
    }

    return textures
}

fun refillProfile(uuid: String, name: String): Profile {
    val uuid1 = uuid.run(::parseUuid) ?: uuidByName(name)
    val name1 = name.takeIf { name.isNotBlank() } ?: nameByUuid(uuid1!!)

    return SimpleProfile(uuid1!!, name1)
}
package net.zatrit.skinbread.skins

import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.RenderOptions
import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.layer.android.*
import java.util.concurrent.CompletableFuture

val capeLayer = ScaleCapeLayer()
val skinLayer = LegacySkinLayer()

fun loadTextures(profile: Profile, source: SkinSource) = supplyAsync {
    try {
        source.resolver.resolve(profile)
    } catch (ex: Exception) {
        ex.printWithSkinSource(source)
        null
    }
}

fun mergeTextures(inputs: List<PlayerTextures>): Textures {
    val textures = Textures()
    val iterator = inputs.iterator()

    while (iterator.hasNext() && !textures.complete) {
        textures.fillWith(iterator.next(), skinLayer, capeLayer)
    }

    return textures
}

fun loadTexturesAsync(
    name: String, uuid: String,
    options: RenderOptions): CompletableFuture<Unit> {
    return supplyAsync {
        val uuid1 = uuid.run(::parseUuid) ?: uuidByName(name)
        val name1 = name.takeIf { name.isNotBlank() } ?: nameByUuid(uuid1!!)

        SimpleProfile(uuid1!!, name1)
    }.exceptionally {
        it.printStackTrace()
        SimpleProfile(nullUuid, name)
    }.thenApplyAsync { profile ->
        val result = defaultSources.map { loadTextures(profile, it) }
            .map { textures -> textures.get()!! }
        options.pendingTextures = mergeTextures(result)
    }.exceptionally {
        it.printStackTrace()
    }!!
}
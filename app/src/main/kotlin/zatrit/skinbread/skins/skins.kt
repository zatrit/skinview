package zatrit.skinbread.skins

import zatrit.skinbread.*
import zatrit.skins.lib.api.Profile
import zatrit.skins.lib.layer.android.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync

/** Layer used to load the cape. */
val capeLayer = ScaleCapeLayer()

/** The layer used to load the skin. */
val skinLayer = LegacySkinLayer()

/**
 * Asynchronously requests textures from the source,
 * converts them to [Textures] and calls [func]. */
inline fun fetchTexturesAsync(
  profile: Profile, source: SkinSource,
  crossinline func: (Textures?) -> Unit): CompletableFuture<Void> = runAsync {
    val textures = try {
        val response = source.resolver.resolve(profile)
        Textures().apply { or(response, skinLayer, capeLayer) }
    } catch (ex: Exception) {
        ex.printDebug()
        null
    }

    func(textures)
}

/**
 * Assembles the input list of [Textures] into a single set of
 * textures, selecting those with lower order. */
fun mergeTextures(inputs: List<Textures>): Textures {
    val textures = Textures()
    val iterator = inputs.iterator()

    while (iterator.hasNext() && !textures.isComplete()) {
        textures.or(iterator.next())
    }

    return textures
}

/** Loads the contents of empty profile fields. */
fun refillProfile(uuid: String, name: String): Profile {
    val uuid1 = uuid.run(::parseUuid) ?: uuidByName(name)
    val name1 = name.takeIf { name.isNotBlank() } ?: nameByUuid(uuid1!!)

    return SimpleProfile(uuid1!!, name1)
}
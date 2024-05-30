package zatrit.skinbread.skins

import zatrit.skinbread.Textures
import zatrit.skinbread.parseUuid
import zatrit.skinbread.printDebug
import zatrit.skins.lib.PlayerTextures
import zatrit.skins.lib.api.Profile
import zatrit.skins.lib.layer.android.LegacySkinLayer
import zatrit.skins.lib.layer.android.ScaleCapeLayer
import java.util.concurrent.CompletableFuture.runAsync

val capeLayer = ScaleCapeLayer()
val skinLayer = LegacySkinLayer()

inline fun loadTexturesAsync(
  profile: Profile, source: SkinSource,
  crossinline callback: (PlayerTextures?) -> Unit) = runAsync {
    val textures = try {
        source.resolver.resolve(profile)
    } catch (ex: Exception) {
        ex.printDebug()
        null
    }

    callback(textures)
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
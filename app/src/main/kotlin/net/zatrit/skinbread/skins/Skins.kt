package net.zatrit.skinbread.skins

import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.RenderOptions
import net.zatrit.skinbread.gl.model.ModelType
import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.TextureType.*
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.layer.android.*

private val capeLayer = ScaleCapeLayer()
private val skinLayer = LegacySkinLayer()

fun loadTextures(profile: Profile, sources: Array<SkinSource>) = sources.map {
    supplyAsync {
        try {
            it.resolver.resolve(profile)
        } catch (ex: Exception) {
            ex.printWithSkinSource(it)
            null
        }
    }
}.mapNotNull { it.get() }

fun mergeTextures(inputs: List<PlayerTextures>): Textures {
    val textures = Textures()
    val iterator = inputs.iterator()

    while (iterator.hasNext() && !textures.complete) {
        val input = iterator.next()
        val skinTexture = input.getTexture(SKIN)?.run(skinLayer::apply)

        val texture = Textures(
            skin = skinTexture?.texture,
            cape = input.getTexture(CAPE)?.run(capeLayer::apply)?.texture,
            ears = input.getTexture(EARS)?.texture,
            model = skinTexture?.run {
                if (texture?.metadata?.model == "slim") ModelType.SLIM
                else ModelType.DEFAULT
            },
        )

        textures.fillWith(texture)
    }

    return textures
}

fun loadTexturesAsync(name: String, uuid: String, options: RenderOptions) =
    supplyAsync {
        val uuid1 = uuid.run(::parseUuid) ?: uuidByName(name)
        val name1 = name.takeIf { name.isNotBlank() } ?: nameByUuid(uuid1!!)

        SimpleProfile(uuid1!!, name1)
    }.exceptionally {
        it.printStackTrace()
        SimpleProfile(nullUuid, name)
    }.thenApplyAsync {
        val result = loadTextures(it, defaultSources)
        options.pendingTextures = mergeTextures(result)
    }.exceptionally {
        it.printStackTrace()
    }!!
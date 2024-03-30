package net.zatrit.skinbread.skins

import net.zatrit.skins.lib.TextureType.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.TypedTexture
import net.zatrit.skins.lib.layer.android.*
import net.zatrit.skinbread.gl.*
import net.zatrit.skinbread.printWithSkinSource

class Skins {
    private val capeLayers = listOf(
        // A layer that scales cape to look normally
        object : ImageLayer(ScaleCapeLayer()) {
            override fun predicate(input: TypedTexture): Boolean {
                val metadata = input.texture.metadata
                return metadata == null || !metadata.isAnimated
            }
        },
    )

    private val skinLayers = listOf(ImageLayer(LegacySkinLayer()))

    fun loadSkin(profile: Profile, sources: Iterable<SkinSource>) = sources.map {
        try {
            it.resolver.resolve(profile)
        } catch (ex: Exception) {
            ex.printWithSkinSource(it)
            null
        }
    }

    fun mergeTextures(inputs: List<PlayerTextures?>): Textures {
        val textures = Textures()
        val iterator = inputs.filterNotNull().iterator()

        while (iterator.hasNext() && !textures.complete) {
            val input = iterator.next()
            val skinTexture = input.getTexture(SKIN, skinLayers)

            val texture = Textures(
                skin = skinTexture?.texture,
                cape = input.getTexture(CAPE, capeLayers)?.texture,
                ears = input.getTexture(EARS, listOf())?.texture,
                model = skinTexture?.run {
                    if (texture?.metadata?.model == "slim") ModelType.SLIM
                    else ModelType.DEFAULT
                },
            )

            textures.or(texture)
        }

        return textures
    }
}


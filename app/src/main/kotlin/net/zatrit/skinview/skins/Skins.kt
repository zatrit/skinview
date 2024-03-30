package net.zatrit.skinview.skins

import net.zatrit.skins.lib.TextureType
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.TypedTexture
import net.zatrit.skins.lib.layer.android.*
import net.zatrit.skinview.gl.*
import net.zatrit.skinview.printWithSkinSource

class Skins(private val options: RenderOptions) {
    private val layers = listOf(
        // A layer that scales cape to look normally
        object : ImageLayer(ScaleCapeLayer()) {
            override fun predicate(input: TypedTexture): Boolean {
                val metadata = input.texture.metadata
                val cape = input.type == TextureType.CAPE

                if (metadata == null) {
                    return cape
                }

                return cape && !metadata.isAnimated
            }
        },
        // A layer that fixes 64x32 skins
        object : ImageLayer(LegacySkinLayer()) {
            override fun predicate(input: TypedTexture): Boolean =
                input.type == TextureType.SKIN
        })

    fun loadSkin(profile: Profile, sources: Iterable<SkinSource>) = sources.map {
        try {
            it.resolver.resolve(profile)
        } catch (ex: Exception) {
            ex.printWithSkinSource(it)
            null
        }
    }

    fun loadTextures(textures: PlayerTextures) {
        for (type in TextureType.entries) {
            if (!textures.hasTexture(type)) {
                continue
            }

            val texture = textures.getTexture(type, layers)!!.texture
            val bitmap = texture.asBitmap()

            when (type) {
                TextureType.SKIN -> {
                    options.modelType =
                        if (texture.metadata?.model == "slim") ModelType.SLIM
                        else ModelType.DEFAULT

                    options.pendingSkin = bitmap
                }

                TextureType.EARS -> options.pendingEars = bitmap

                TextureType.CAPE -> options.pendingCape = bitmap
            }
        }
    }
}


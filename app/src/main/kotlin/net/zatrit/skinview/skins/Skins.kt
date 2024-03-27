package net.zatrit.skinview.skins

import android.graphics.BitmapFactory
import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.TypedTexture
import net.zatrit.skins.lib.layer.android.*
import net.zatrit.skinview.gl.*
import java.util.UUID

private val NULL_UUID = UUID.nameUUIDFromBytes(ByteArray(16))

class Skins(val options: RenderOptions) {

    val config = Config().apply {
        layers = listOf(
            // A layer that scales cape to look normally
            object : ImageLayer(ScaleCapeLayer()) {
                override fun predicate(input: TypedTexture): Boolean {
                    val metadata = input.texture.metadata;
                    val cape = input.type == TextureType.CAPE;

                    if (metadata == null) {
                        return cape;
                    }

                    return cape && !metadata.isAnimated;
                }
            },
            // A layer that fixes 64x32 skins
            object : ImageLayer(LegacySkinLayer()) {
                override fun predicate(input: TypedTexture): Boolean =
                    input.type == TextureType.SKIN
            })
    }

    inline fun createResolver(func: (Config) -> Resolver) = func(this.config)

    fun loadSkin(profile: SimpleProfile, sources: List<SkinSource>) =
        sources.map { it.resolver.resolve(profile) }

    fun loadTextures(textures: PlayerTextures) {
        for (type in TextureType.entries) {
            if (!textures.hasTexture(type)) {
                continue
            }

            val texture = textures.getTexture(type)!!.texture
            val textureData = texture.bytes
            val bitmap = BitmapFactory.decodeByteArray(
                textureData, 0, textureData.size
            )

            when (type) {
                TextureType.SKIN -> {
                    options.modelType = when (texture.metadata?.model) {
                        "slim" -> ModelType.SLIM
                        else -> ModelType.DEFAULT
                    }

                    options.pendingSkin = bitmap
                }

                TextureType.EARS -> options.pendingEars = bitmap

                TextureType.CAPE -> options.pendingCape = bitmap
            }
        }
    }
}


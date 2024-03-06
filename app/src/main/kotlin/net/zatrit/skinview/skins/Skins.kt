package net.zatrit.skinview.skins

import android.graphics.BitmapFactory
import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.data.TypedTexture
import net.zatrit.skins.lib.resolver.NamedHTTPResolver
import net.zatrit.skinview.gl.*
import net.zatrit.skinview.skins.layers.*
import java.util.UUID

class Skins(options: RenderOptions) {
    private val config = Config().apply {
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

    init {
        val resolver = NamedHTTPResolver(
            config, "http://skinsystem.ely.by/textures/"
        )
        val profile = SimpleProfile(NULL_UUID, "FireMage")
        val texture =
            resolver.resolve(profile).getTexture(TextureType.SKIN)!!.texture
        val textureData = texture.bytes

        options.modelType = when (texture.metadata?.model) {
            "slim" -> ModelType.SLIM
            else -> ModelType.DEFAULT
        }

        options.pendingTexture = BitmapFactory.decodeByteArray(
            textureData, 0, textureData.size
        )
    }
}

private val NULL_UUID = UUID.nameUUIDFromBytes(ByteArray(16))
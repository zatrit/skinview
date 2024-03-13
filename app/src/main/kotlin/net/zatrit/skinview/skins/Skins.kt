package net.zatrit.skinview.skins

import android.graphics.BitmapFactory
import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.data.TypedTexture
import net.zatrit.skins.lib.layer.android.*
import net.zatrit.skins.lib.resolver.MojangResolver
import net.zatrit.skinview.gl.*
import java.util.UUID

private val NULL_UUID = UUID.nameUUIDFromBytes(ByteArray(16))

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
        val resolver = MojangResolver(config)
        val profile = SimpleProfile(NULL_UUID, "Zatrit156")
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
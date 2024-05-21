package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.texture.BytesTexture
import net.zatrit.skins.lib.util.IOUtil
import java.net.URL
import java.util.*

abstract class DirectResolver(private val type: TextureType) : Resolver {
    override fun resolve(profile: Profile): PlayerTextures {
        val textures = EnumMap<TextureType, Texture>(TextureType::class.java)
        textures[type] = downloadTexture(type, profile)

        return PlayerTextures(textures)
    }

    private fun downloadTexture(type: TextureType, profile: Profile): Texture {
        val url = URL(getUrl(type, profile.id, profile.name, profile.shortId))
        val content = IOUtil.download(url)
        return BytesTexture(content, null)
    }

    abstract fun getUrl(
        type: TextureType, id: UUID, name: String, shortId: String): String
}

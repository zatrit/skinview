package net.zatrit.skins.lib.resolver

import net.andreinc.aleph.AlephFormatter
import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.texture.BytesTexture
import net.zatrit.skins.lib.util.IOUtil
import java.net.URL
import java.util.EnumMap

class DirectResolver(
    private val baseUrl: String, private val types: Collection<TextureType>) :
    Resolver {

    override fun requiresUuid() = "{id}" in baseUrl || "{shortId}" in baseUrl

    override fun resolve(profile: Profile): PlayerTextures {
        val textures = EnumMap<TextureType, Texture>(
            TextureType::class.java
        )

        val replaces = mapOf<String, Any>(
            "id" to profile.id,
            "name" to profile.name,
            "shortId" to profile.shortId,
        )

        types.forEach {
            textures[it] = downloadTexture(replaces, it)
        }

        return PlayerTextures(textures)
    }

    private fun downloadTexture(
        replaces: Map<String, Any>, type: TextureType): Texture? {

        val url =
            URL(AlephFormatter.str(baseUrl, replaces).arg("type", type).fmt())
        val content = IOUtil.download(url)

        return if (content != null) BytesTexture(content, null) else null
    }
}

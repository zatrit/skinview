package net.zatrit.skins.lib.resolver

import net.zatrit.skins.lib.*
import net.zatrit.skins.lib.api.*
import net.zatrit.skins.lib.data.Metadata
import net.zatrit.skins.lib.texture.URLTexture
import net.zatrit.skins.lib.util.jsonObject
import java.nio.file.*
import java.util.*

/**
 * Resolver for the local directory, which has a similar format to the skin
 * server, i.e. loads by name from the `textures/` and
 * `metadata/` folders.
 */
class LocalResolver(
    private val config: Config, private val directory: Path) : Resolver {

    override fun resolve(profile: Profile): PlayerTextures {
        val name = profile.getName()
        val textures = EnumMap<TextureType, Texture>(TextureType::class.java)

        val metadataDir = directory.resolve("metadata")
        val texturesDir = directory.resolve("textures")

        for (type in TextureType.entries) {
            var metadata: Metadata? = null
            val typeName = type.toString().lowercase(Locale.getDefault())
            val texturesFile = texturesDir.resolve(typeName).resolve(
                "$name.png"
            ).toFile()

            if (!texturesFile.isFile()) {
                continue
            }

            val url = texturesFile.toURI().toURL().toString()
            val metadataFile = metadataDir.resolve(typeName).resolve(
                "$name.json"
            )

            if (metadataFile.toFile().isFile()) {
                val reader = Files.newInputStream(metadataFile)
                metadata = Metadata().apply { loadJson(reader.jsonObject) }
            }

            textures[type] = URLTexture(url, metadata)
        }

        return BasePlayerTextures(textures, config.layers)
    }
}

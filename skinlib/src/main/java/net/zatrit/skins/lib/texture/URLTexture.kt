package net.zatrit.skins.lib.texture

import net.zatrit.skins.lib.api.Texture
import net.zatrit.skins.lib.data.Metadata
import java.io.InputStream
import java.net.URL

/**
 * A texture that loads its content from a given [URL].
 */
class URLTexture(url: String, private val metadata: Metadata? = null) : Texture {
    private val url = URL(url)

    override fun getMetadata() = metadata
    override fun openStream(): InputStream = url.openStream()
}
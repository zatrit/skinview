package net.zatrit.skins.lib.texture

import com.google.common.io.ByteStreams
import net.zatrit.skins.lib.api.Texture
import net.zatrit.skins.lib.data.Metadata
import java.net.URL

/**
 * A texture that loads its content at a given [URL].
 */
class URLTexture(url: String, private val metadata: Metadata? = null) : Texture {
    private val bytes: ByteArray = URL(url).openStream().use {
        ByteStreams.toByteArray(it)
    }

    override fun getMetadata() = metadata
    override fun getBytes() = bytes
}
package net.zatrit.skins.lib.api;

import net.zatrit.skins.lib.data.Metadata;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * An abstract texture that can be converted to a {@link Byte} array.
 */
public interface Texture {
    /**
     * Texture name used during caching.
     */
    String getId();

    @Nullable Metadata getMetadata();

    /**
     * May contain I/O operations, as usage implies execution in
     * a parallel game thread so that the game does not freeze.
     *
     * @return the texture image as a byte array.
     */
    byte[] getBytes() throws IOException;
}

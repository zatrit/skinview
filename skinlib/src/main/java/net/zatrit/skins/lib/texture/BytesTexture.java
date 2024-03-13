package net.zatrit.skins.lib.texture;

import lombok.Getter;
import net.zatrit.skins.lib.data.Metadata;
import org.jetbrains.annotations.NotNull;

/**
 * A texture wrapping an array of bytes.
 */
@Getter
public class BytesTexture extends LazyTexture {
    private final byte[] bytes;

    public BytesTexture(String id, byte @NotNull [] bytes, Metadata metadata) {
        super(id, metadata);
        this.bytes = bytes;
    }
}

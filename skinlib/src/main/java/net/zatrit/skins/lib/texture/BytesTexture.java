package net.zatrit.skins.lib.texture;

import net.zatrit.skins.lib.data.Metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;

/**
 * A texture wrapping an array of bytes.
 */
@Getter
public class BytesTexture extends LazyTexture {
    private final byte[] bytes;

    public BytesTexture(byte @NotNull [] bytes, @Nullable Metadata metadata) {
        super(metadata);
        this.bytes = bytes;
    }
}

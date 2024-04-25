package net.zatrit.skins.lib.texture;

import net.zatrit.skins.lib.data.Metadata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * A texture wrapping an array of bytes.
 */
@Getter
public class BytesTexture extends LazyTexture {
    private final byte[] bytes;

    public BytesTexture(byte @NotNull [] bytes, Metadata metadata) {
        super(metadata);
        this.bytes = bytes;
    }
}

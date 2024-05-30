package zatrit.skins.lib.texture;

import zatrit.skins.lib.data.Metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    @Override
    public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }
}

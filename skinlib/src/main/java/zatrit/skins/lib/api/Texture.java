package zatrit.skins.lib.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

import zatrit.skins.lib.data.Metadata;

/**
 * An abstract texture that can be converted to a {@link Byte} array.
 */
public interface Texture {
    @Nullable Metadata getMetadata();

    /**
     * May contain I/O operations, as usage implies execution in
     * a parallel game thread so that the game does not freeze.
     *
     * @return the {@link InputStream} that can be used to read texture.
     */
    InputStream openStream() throws IOException;

    /**
     * This function does NOT guarantee that it will return a unique bitmap image each time it is called
     *
     * @return bitmap
     */
    default Bitmap getBitmap() throws IOException {
        return BitmapFactory.decodeStream(openStream());
    }
}

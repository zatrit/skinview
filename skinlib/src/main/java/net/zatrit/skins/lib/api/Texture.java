package net.zatrit.skins.lib.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.zatrit.skins.lib.data.Metadata;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import lombok.val;

/**
 * An abstract texture that can be converted to a {@link Byte} array.
 */
public interface Texture {
    @Nullable Metadata getMetadata();

    /**
     * May contain I/O operations, as usage implies execution in
     * a parallel game thread so that the game does not freeze.
     *
     * @return the texture image as a byte array.
     */
    byte[] getBytes() throws IOException;

    /**
     * This function does NOT guarantee that it will return a unique bitmap image each time it is called
     *
     * @return bitmap
     */
    default Bitmap asBitmap() throws IOException {
        val bytes = getBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}

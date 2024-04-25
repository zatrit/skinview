package net.zatrit.skins.lib.util;

import com.google.common.io.ByteStreams;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
@ApiStatus.Internal
public class IOUtil {
    public static byte @Nullable [] download(@NotNull URL url)
        throws IOException {
        val connection = url.openConnection();

        // An easy way to check that the code means OK (2XX).
        if (connection instanceof HttpURLConnection) {
            val httpConnection = (HttpURLConnection) connection;
            if (httpConnection.getResponseCode() / 100 != 2) {
                return null;
            }
        }

        @Cleanup val stream = connection.getInputStream();
        return ByteStreams.toByteArray(stream);
    }
}

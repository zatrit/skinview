package net.zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Metadata;

/**
 * A texture that loads its content using {@link #getBytes()} method.
 */
@Getter
@AllArgsConstructor
public abstract class LazyTexture implements Texture {
    private final String id;
    private final Metadata metadata;
}

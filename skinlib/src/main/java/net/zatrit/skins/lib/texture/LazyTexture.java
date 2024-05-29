package net.zatrit.skins.lib.texture;

import net.zatrit.skins.lib.api.Texture;
import net.zatrit.skins.lib.data.Metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A texture that loads its content using {@link #openStream()} method.
 */
@Getter
@AllArgsConstructor
public abstract class LazyTexture implements Texture {
    private final Metadata metadata;
}

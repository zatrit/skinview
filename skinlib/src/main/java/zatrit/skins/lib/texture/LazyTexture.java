package zatrit.skins.lib.texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import zatrit.skins.lib.api.Texture;
import zatrit.skins.lib.data.Metadata;

/**
 * A texture that loads its content using {@link #openStream()} method.
 */
@Getter
@AllArgsConstructor
public abstract class LazyTexture implements Texture {
    private final Metadata metadata;
}

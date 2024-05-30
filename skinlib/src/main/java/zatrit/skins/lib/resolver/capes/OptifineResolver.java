package zatrit.skins.lib.resolver.capes;

import zatrit.skins.lib.PlayerTextures;
import zatrit.skins.lib.TextureType;
import zatrit.skins.lib.api.Profile;
import zatrit.skins.lib.api.Resolver;
import zatrit.skins.lib.texture.URLTexture;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;

import lombok.AllArgsConstructor;
import lombok.val;

/**
 * <a href="https://optifine.readthedocs.io/capes.html">Optifine API</a>
 * resolver for skinlib.
 * <p>
 * Does not cache skins, because connecting to API already loads textures.
 */
@AllArgsConstructor
public final class OptifineResolver implements Resolver {
    private final String baseUrl;

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
      throws IOException, NullPointerException {
        val url = this.baseUrl + "/capes/" + profile.getName() + ".png";
        val texture = new URLTexture(url, null);

        /* Since you can't check for the existence/change of a
        texture without fetching that texture, it should not be cached. */
        return new PlayerTextures(Collections.singletonMap(
          TextureType.CAPE,
          texture
        ));
    }
}

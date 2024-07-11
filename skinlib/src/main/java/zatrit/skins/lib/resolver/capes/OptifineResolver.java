package zatrit.skins.lib.resolver.capes;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.val;
import zatrit.skins.lib.PlayerTextures;
import zatrit.skins.lib.api.Profile;
import zatrit.skins.lib.api.Resolver;
import zatrit.skins.lib.texture.URLTexture;

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
        val textures = new PlayerTextures();
        
        textures.setCape(new URLTexture(url, null));

        return textures;
    }
}

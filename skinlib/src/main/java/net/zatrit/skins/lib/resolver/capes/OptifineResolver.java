package net.zatrit.skins.lib.resolver.capes;

import lombok.AllArgsConstructor;
import lombok.val;
import net.zatrit.skins.lib.BasePlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.texture.BytesTexture;
import net.zatrit.skins.lib.util.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

/**
 * <a href="https://optifine.readthedocs.io/capes.html">Optifine API</a>
 * resolver for OpenMCSkins.
 * <p>
 * Does not cache skins, because connecting to API already loads textures.
 */
@AllArgsConstructor
public final class OptifineResolver implements Resolver {
    private final Config config;
    private final String baseUrl;

    @Override
    public boolean requiresUuid() {
        return false;
    }

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
        throws IOException, NullPointerException {
        val url = new URL(this.baseUrl + "/capes/" + profile.getName() + ".png");
        val texture = new BytesTexture(
            url.toString(),
            Objects.requireNonNull(IOUtil.download(url)),
            null
        );

        /* Since you can't check for the existence/change of a
        texture without fetching that texture, it should not be cached. */
        return new BasePlayerTextures<>(Collections.singletonMap(
            TextureType.CAPE,
            texture
        ), this.config.getLayers());
    }
}

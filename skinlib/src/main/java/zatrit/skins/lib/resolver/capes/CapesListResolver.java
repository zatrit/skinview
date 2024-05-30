package zatrit.skins.lib.resolver.capes;

import zatrit.skins.lib.PlayerTextures;
import zatrit.skins.lib.TextureType;
import zatrit.skins.lib.api.Profile;
import zatrit.skins.lib.api.Resolver;
import zatrit.skins.lib.texture.URLTexture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * An abstract implementation of {@link Resolver} that assumes the existence of
 * two {@link Map}s: cape owners and cape names (or cape {@link URL}s).
 */
@RequiredArgsConstructor
public abstract class CapesListResolver implements Resolver {
    protected @Nullable Map<String, String> owners;

    /**
     * @return a map of players and their cloaks.
     */
    protected abstract Map<String, String> fetchList() throws IOException;

    /**
     * @return a string representation of the URL to download the cloak texture.
     */
    protected abstract String getUrl(String capeName);

    /**
     * @return the name of the cape for the given player.
     */
    protected @Nullable String getCapeName(@NotNull Profile profile) {
        return Objects.requireNonNull(this.owners).get(profile.getShortId());
    }

    @Override
    public @NotNull PlayerTextures resolve(@NotNull Profile profile)
      throws Exception {
        synchronized (this) {
            if (this.owners == null) {
                this.owners = this.fetchList();
            }
        }

        val capeName = getCapeName(profile);
        val textures = new EnumMap<TextureType, URLTexture>(TextureType.class);

        if (capeName != null) {
            textures.put(
              TextureType.CAPE,
              new URLTexture(this.getUrl(capeName), null)
            );
        }

        return new PlayerTextures(textures);
    }
}

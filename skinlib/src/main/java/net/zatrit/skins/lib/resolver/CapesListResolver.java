package net.zatrit.skins.lib.resolver;

import net.zatrit.skins.lib.BasePlayerTextures;
import net.zatrit.skins.lib.Config;
import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.PlayerTextures;
import net.zatrit.skins.lib.api.Profile;
import net.zatrit.skins.lib.api.Resolver;
import net.zatrit.skins.lib.texture.URLTexture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public abstract class CapesListResolver implements Resolver {
    protected final Config config;
    protected @Nullable Map<String, String> owners;

    protected abstract Map<String, String> fetchList() throws IOException;

    protected abstract String getUrl(String capeName);

    @Override
    public synchronized void refresh() {
        this.owners = null;
    }

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
            textures.put(TextureType.CAPE,
                         new URLTexture(this.getUrl(capeName), null)
            );
        }

        return new BasePlayerTextures<>(textures, this.config.getLayers());
    }
}

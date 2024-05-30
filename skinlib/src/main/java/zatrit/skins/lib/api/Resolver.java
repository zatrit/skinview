package zatrit.skins.lib.api;

import zatrit.skins.lib.PlayerTextures;

import org.jetbrains.annotations.NotNull;

/**
 * Interface that describes skin resolving mechanism.
 */
public interface Resolver {
    /**
     * @return player-specific texture loader.
     */
    @NotNull PlayerTextures resolve(Profile profile) throws Exception;
}

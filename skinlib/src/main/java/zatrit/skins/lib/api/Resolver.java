package zatrit.skins.lib.api;

import org.jetbrains.annotations.NotNull;

import zatrit.skins.lib.PlayerTextures;

/**
 * Interface that describes skin resolving mechanism.
 */
public interface Resolver {
    /**
     * @return player-specific texture loader.
     */
    @NotNull PlayerTextures resolve(Profile profile) throws Exception;
}

package net.zatrit.skins.lib.api;

import org.jetbrains.annotations.NotNull;

/**
 * Interface that describes skin resolving mechanism.
 */
public interface Resolver {
    /**
     * Used for optimization. If all resolvers don't
     * require UUID, skip UUID refreshing.
     */
    default boolean requiresUuid() {
        return true;
    }

    /**
     * Refresh all currently cached data.
     */
    default void refresh() {}

    /**
     * @return player-specific texture loader.
     */
    @NotNull PlayerTextures resolve(Profile profile) throws Exception;
}

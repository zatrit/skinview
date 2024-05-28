package net.zatrit.skins.lib.api;

import java.util.UUID;

/**
 * An abstract player profile containing
 * the data needed to load skins.
 */
public interface Profile {
    /**
     * @return player UUID for skin resolution.
     */
    UUID getId();

    /**
     * @return player name.
     */
    String getName();

    default String getShortId() {
        return getId().toString().replace("-", "");
    }
}

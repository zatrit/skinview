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
     * @return player UUID without minus characters.
     */
    default String getShortId() {
        return this.getId().toString().replace("-", "");
    }

    /**
     * @return player name.
     */
    String getName();
}

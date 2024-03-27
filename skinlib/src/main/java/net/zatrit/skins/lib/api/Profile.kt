package net.zatrit.skins.lib.api

import java.util.UUID

/**
 * An abstract player profile containing
 * the data needed to load skins.
 */
interface Profile {
    /**
     * @return player UUID for skin resolution.
     */
    val id: UUID

    /**
     * @return player name.
     */
    val name: String

    val shortId: String
        /**
         * @return player UUID without minus characters.
         */
        get() = id.toString().replace("-", "")
}

package zatrit.skinbread.skins

import org.json.*
import zatrit.skinbread.*
import zatrit.skins.lib.api.Profile
import zatrit.skins.lib.util.*
import java.io.IOException
import java.net.URL
import java.util.UUID

/** Basic implementation of [Profile]. */
class SimpleProfile(private val id: UUID, private val name: String) : Profile {
    override fun getId() = id

    override fun getName() = name
}

// Read more about Mojang API here: https://wiki.vg/Mojang_API

/** Gets the [UUID] for the given player name. */
@Throws(IOException::class)
fun uuidByName(name: String): UUID? {
    val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
    val jsonObject = url.openStream().jsonObject

    return parseUuid(jsonObject.getString("id"))
}

/** Gets the player's name by [UUID]. */
@Throws(IOException::class)
fun nameByUuid(uuid: UUID): String {
    val id = uuid.toString().jvmReplace("-", "")
    val url =
      URL("https://sessionserver.mojang.com/session/minecraft/profile/$id")
    val jsonObject = url.openStream().jsonObject

    return jsonObject.getString("name")
}
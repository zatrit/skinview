package net.zatrit.skinbread.skins

import net.zatrit.skinbread.*
import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.util.*
import org.json.*
import java.io.IOException
import java.net.URL
import java.util.UUID

class SimpleProfile(override val id: UUID, override val name: String) : Profile

@Throws(IOException::class)
fun uuidByName(name: String): UUID? {
    val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
    val jsonObject = url.openStream().jsonObject

    return parseUuid(jsonObject.getString("id"))
}

@Throws(IOException::class)
fun nameByUuid(uuid: UUID): String {
    val id = uuid.toString().replace("-", "")
    val url =
        URL("https://sessionserver.mojang.com/session/minecraft/profile/$id")
    val jsonObject = url.openStream().jsonObject

    return jsonObject.getString("name")
}
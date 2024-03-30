package net.zatrit.skinview.skins

import net.zatrit.skins.lib.api.Profile
import net.zatrit.skins.lib.util.*
import java.io.IOException
import java.net.URL
import java.util.UUID

val nullUUID: UUID = UUID.nameUUIDFromBytes(ByteArray(16))

class SimpleProfile(override val id: UUID, override val name: String) : Profile

@Throws(IOException::class)
fun profileByName(name: String, requiresUuid: Boolean = true): Profile {
    val uuid = if (requiresUuid) {
        val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
        val jsonObject = url.openStream().jsonObject

        UUID.fromString(
            "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toPattern()
                .matcher(jsonObject.getString("id")).replaceAll("$1-$2-$3-$4-$5")
        )
    } else {
        nullUUID
    }

    return SimpleProfile(uuid, name)
}

@Throws(IOException::class)
fun profileByUUID(uuid: UUID): Profile {
    val id = uuid.toString().replace("-", "")
    val url =
        URL("https://sessionserver.mojang.com/session/minecraft/profile/$id")
    val jsonObject = url.openStream().jsonObject

    return SimpleProfile(uuid, jsonObject.getString("name"))
}
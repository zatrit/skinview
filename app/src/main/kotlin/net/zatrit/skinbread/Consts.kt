package net.zatrit.skinbread

import android.opengl.Matrix.*
import net.zatrit.skinbread.gl.mat4
import net.zatrit.skinbread.skins.ResolverType.MOJANG
import net.zatrit.skinbread.skins.SkinSource
import net.zatrit.skins.lib.resolver.MojangResolver
import java.util.UUID

const val TAG = "SkinView"

val identity = mat4 { setIdentityM(it, 0) }

val capeModelMatrix = mat4 { setRotateM(it, 0, 30f, 1f, 0f, 0f) }

val defaultSources = arrayOf(
    // official skin system
    SkinSource(MOJANG, "Mojang", MojangResolver()),
)

val nullUUID: UUID = UUID.nameUUIDFromBytes(ByteArray(16))
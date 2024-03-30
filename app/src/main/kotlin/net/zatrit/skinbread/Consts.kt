package net.zatrit.skinbread

import android.opengl.Matrix.*
import net.zatrit.skinbread.gl.mat4
import net.zatrit.skinbread.skins.ResolverType.MOJANG
import net.zatrit.skinbread.skins.SkinSource
import net.zatrit.skins.lib.resolver.MojangResolver
import java.util.UUID

const val TAG = "SkinView"

val identity = mat4 { setIdentityM(it, 0) }

val capeModelMatrix = mat4 {
    setRotateEulerM(it, 0, 10f, 180f, 0f)
    translateM(it, 0, 0f, -0.11f, 0.45f)
}

val leftWingMatrix = mat4 {
    setRotateEulerM(it, 0, 12.5f, 180f, -15f)
    translateM(it, 0, -0.3f, -0.42f, 0.47f)
    scaleM(it, 0, -1f, 1f, -1f)
}

val rightWingMatrix = mat4 {
    setRotateEulerM(it, 0, 12.5f, 180f, 15f)
    translateM(it, 0, 0.3f, -0.42f, 0.43f)
    scaleM(it, 0, 1f, 1f, -1f)
}

val defaultSources = arrayOf(
    // official skin system
    SkinSource(MOJANG, "Mojang", MojangResolver()),
)

val nullUUID: UUID = UUID.nameUUIDFromBytes(ByteArray(16))
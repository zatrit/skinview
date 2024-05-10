// setRotateEulerM is deprecated, but setRotateEulerM2 requires SDK_VERSION >= 34
@file:Suppress("DEPRECATION")

package net.zatrit.skinbread

import android.opengl.Matrix.*
import net.zatrit.skinbread.gl.mat4
import java.util.UUID

const val TAG = "SkinView"

val identity = mat4 { setIdentityM(it, 0) }

val capeMatrix = mat4 {
    setRotateEulerM(it, 0, 10f, 180f, 0f)
    translateM(it, 0, 0f, -0.11f, 0.45f)
}

val rightWingMatrix = mat4 {
    setRotateEulerM(it, 0, 12.5f, 180f, 15f)
    translateM(it, 0, 0.3f, -0.42f, 0.43f)
    scaleM(it, 0, 1f, 1f, -1f)
}

val leftWingMatrix = mat4 {
    setRotateEulerM(it, 0, 12.5f, 180f, -15f)
    translateM(it, 0, -0.3f, -0.42f, 0.47f)
    scaleM(it, 0, -1f, 1f, -1f)
}

private const val EAR_OFFSET = 0.25f

val rightEarMatrix = mat4 {
    setIdentityM(it, 0)
    translateM(it, 0, EAR_OFFSET, 1.75f, 0f)
}

val leftEarMatrix = mat4 {
    rightEarMatrix.copyInto(it)
    translateM(it, 0, -EAR_OFFSET * 2, 0f, 0f)
    scaleM(it, 0, -1f, 1f, 1f)
}

val nullUuid: UUID = UUID.nameUUIDFromBytes(ByteArray(16))
// setRotateEulerM is deprecated, but setRotateEulerM2 requires SDK_VERSION >= 34
@file:Suppress("DEPRECATION")

package net.zatrit.skinbread

import android.opengl.Matrix.*
import android.transition.Slide
import android.view.Gravity
import net.zatrit.skinbread.gl.mat4
import net.zatrit.skinbread.skins.defaultSources
import java.util.UUID

/** Tag used in logs to debug the application. */
const val TAG = "SkinView"

/** Name for global application preferences. */
const val PREFS_NAME = "net.zatrit.skinbread"

/** Identity matrix.
 *
 * When a vector is multiplied by it, the vector remains unchanged.
 * Calculated with [setIdentityM].
 * */
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

/** Ears offset in X with relative to the center of the model. */
private const val EAR_OFFSET = 0.25f

/** The matrix used to render the right ear.
 * Offsets the model in the X axis by [EAR_OFFSET]. */
val rightEarMatrix = mat4 {
    setIdentityM(it, 0)
    translateM(it, 0, EAR_OFFSET, 1.75f, 0f)
}

/** The matrix used to render the left ear.
 * Calculates based on [rightEarMatrix] via mirroring. */
val leftEarMatrix = mat4 {
    rightEarMatrix.copyInto(it)
    translateM(it, 0, -EAR_OFFSET * 2, 0f, 0f)
    scaleM(it, 0, -1f, 1f, 1f)
}

/** Smooth transition between activities. */
val activityTransition = Slide(Gravity.BOTTOM).apply {
    excludeTarget(R.id.btn_fetch, true)
}

/** The default [UUID] used. Created from a [ByteArray] filled with zeros. */
val nullUuid: UUID = UUID.nameUUIDFromBytes(ByteArray(16))
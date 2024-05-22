// setRotateEulerM is deprecated, but setRotateEulerM2 requires SDK_VERSION >= 34
@file:Suppress("DEPRECATION")

package net.zatrit.skinbread

import android.opengl.Matrix.*
import android.transition.Slide
import android.view.Gravity
import net.zatrit.skinbread.gl.mat4
import net.zatrit.skinbread.skins.*
import java.util.UUID
import java.util.concurrent.CompletableFuture

/** Tag used in [android.util.Log]. */
const val TAG = "SkinView"

/** Name for global application [android.content.SharedPreferences]. */
const val PREFS_NAME = "net.zatrit.skinbread"

/** Identity matrix.
 *
 * When a vector is multiplied by it, the vector remains unchanged.
 * Calculated with [setIdentityM].
 * */
val identity = mat4 { setIdentityM(it, 0) }

/** The matrix used to render the cape. */
val capeMatrix = mat4 {
    setRotateEulerM(it, 0, 10f, 180f, 0f)
    translateM(it, 0, 0f, -0.08f, 0.45f)
}

/** Matrix used to visualize the right wing of the elytra. */
val rightWingMatrix = mat4 {
    setRotateEulerM(it, 0, 12.5f, 180f, 15f)
    translateM(it, 0, 0.3f, -0.42f, 0.43f)
    scaleM(it, 0, 1f, 1f, -1f)
}

/** Matrix used to visualize the left wing of the elytra. */
val leftWingMatrix = mat4 {
    setRotateEulerM(it, 0, 12.5f, 180f, -15f)
    translateM(it, 0, -0.3f, -0.42f, 0.47f)
    scaleM(it, 0, -1f, 1f, -1f)
}

/** Ears offset in the X axis relative to the center of the model. */
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
val transition: Slide
    get() = Slide(Gravity.BOTTOM)

/** Transition between activities when both have R.id.btn_fetch */
val transitionWithFetchButton = transition.apply {
    excludeTarget(R.id.btn_fetch, true)
}

/** The default [UUID]. */
val nullUuid: UUID = UUID.randomUUID()

/**
 * Globally stored array of loaded textures.
 *
 * It's impractical to store it locally for an [net.zatrit.skinbread.ui.TexturesActivity],
 * as passing it between activities is very expensive in terms
 * of performance and complicates the code. */
@Volatile
var textures = arrayOfNulls<Textures>(defaultSources.size)

/** Static handler for adding textures */
@Volatile
var texturesHandler: TexturesListener? = null

/**
 * Since skin loading takes place in a static context,
 * a static handler for [android.widget.Toast]'s is required. */
@Volatile
var toastHandler: ((Int) -> Unit)? = null

/** The latest started [CompletableFuture] that downloads skins. */
var loading: CompletableFuture<Unit>? = null
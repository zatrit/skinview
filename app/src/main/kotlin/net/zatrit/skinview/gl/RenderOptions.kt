package net.zatrit.skinview.gl

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.*

@Parcelize
class RenderOptions(
    /** Shade model or not */
    var shading: Boolean = true,
    /** Show grid */
    var showGrid: Boolean = true,
    var modelType: ModelType = ModelType.DEFAULT,
) : Parcelable {
    /** Temporary bitmap values that tells Renderer to set instead of previous texture */
    @IgnoredOnParcel
    var pendingSkin: Bitmap? = null

    @IgnoredOnParcel
    var pendingCape: Bitmap? = null

    @IgnoredOnParcel
    var pendingEars: Bitmap? = null

    @IgnoredOnParcel
    var pendingBackground = 0L
}
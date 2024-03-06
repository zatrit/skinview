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
    /** Temporary bitmap value that tells Renderer to set instead of previous texture */
    @IgnoredOnParcel var pendingTexture: Bitmap? = null,
) : Parcelable
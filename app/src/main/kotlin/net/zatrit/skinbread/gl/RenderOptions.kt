package net.zatrit.skinbread.gl

import android.os.Parcelable
import kotlinx.parcelize.*
import net.zatrit.skinbread.Textures

@Parcelize
class RenderOptions(
    /** Shade model or not */
    var shading: Boolean = true,
    /** Show grid */
    var grid: Boolean = true,
    /** Show elytra instead of cape */
    var elytra: Boolean = false,

    val cape: Boolean = true,
    val skin: Boolean = true,
    val ears: Boolean = true,
) : Parcelable {

    /** Temporary bitmap values that tells Renderer to set instead of previous textures */
    @IgnoredOnParcel
    var pendingTextures: Textures? = null

    @IgnoredOnParcel
    var pendingDefaultTextures: Textures? = null

    @IgnoredOnParcel
    var background = 0L
}
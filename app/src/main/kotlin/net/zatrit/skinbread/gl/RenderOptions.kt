package net.zatrit.skinbread.gl

import android.os.Parcelable
import kotlinx.parcelize.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.defaultSources
import java.util.concurrent.ArrayBlockingQueue

@Parcelize
class RenderOptions(
    /** Shade model or not */
    var shading: Boolean = true,
    /** Show grid */
    var grid: Boolean = true,
    /** Show elytra instead of cape */
    var elytra: Boolean = false,
) : Parcelable {

    /** Temporary bitmap values that tells Renderer to set instead of previous textures */
    @IgnoredOnParcel
    var pendingTextures =
        ArrayBlockingQueue<OrderedTextures>(defaultSources.size)

    @IgnoredOnParcel
    var clearTextures = false

    @IgnoredOnParcel
    var pendingDefaultTextures: Textures? = null

    @IgnoredOnParcel
    var background = 0L
}
package net.zatrit.skinbread.gl

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import net.zatrit.skinbread.Textures
import net.zatrit.skinbread.skins.defaultSources
import java.util.concurrent.ArrayBlockingQueue

/** Configuration for [Renderer] to allow interaction with it from other threads. */
@Parcelize
class RenderConfig(
  /** Defines whether to use shading to render the model. */
  var shading: Boolean = true,

  /** Defines whether to display a grid below the model. */
  var grid: Boolean = true,

  /**
   * Defines whether to display [elytra](https://minecraft.wiki/w/Elytra)
   * instead of [cape](https://minecraft.wiki/w/Cape). */
  var elytra: Boolean = false,
) : Parcelable {

    /** A texture set queue used for optimized transfer of loaded
     * textures to [Renderer.textures]. */
    @IgnoredOnParcel
    var pendingTextures = ArrayBlockingQueue<Textures>(defaultSources.size)

    /** Allows to request texture cleanup by storing true. */
    @IgnoredOnParcel
    var clearTextures = false

    /**
     * A set of textures that will be loaded
     * on the next rendering cycle as [Renderer.defaultTextures]. */
    @IgnoredOnParcel
    var pendingDefaultTextures: Textures? = null

    /**
     * [RGBA](https://en.wikipedia.org/wiki/RGBA_color_model)
     * background color used in rendering. */
    @IgnoredOnParcel
    var background = 0L
}
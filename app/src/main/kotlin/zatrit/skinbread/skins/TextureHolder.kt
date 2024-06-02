package zatrit.skinbread.skins

import zatrit.skinbread.Textures

/** An interface describing a structure that stores texture and can display toast. */
interface TextureHolder {
    /** Adds a set of textures for display. */
    fun addTextures(
      textures: Textures, index: Int, order: Int, name: SourceName)

    /** Sets the array of [textures] to display. */
    fun setTextures(textures: Array<Textures?>)

    /** Displays toast in the UI thread */
    fun showToast(resId: Int)
}
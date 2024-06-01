package zatrit.skinbread.skins

import zatrit.skinbread.Textures

interface TextureHolder {
    fun onTexturesAdded(
      textures: Textures, index: Int, order: Int, name: SourceName)

    fun setTextures(newTextures: Array<Textures?>)

    fun handleToast(resId: Int)
}
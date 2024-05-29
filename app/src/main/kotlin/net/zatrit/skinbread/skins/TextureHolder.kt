package net.zatrit.skinbread.skins

import net.zatrit.skinbread.Textures

interface TextureHolder {
    fun onTexturesAdded(
      textures: Textures, index: Int, order: Int, name: SourceName)

    fun prepareTextureForReuse(index: Int) {}

    fun setTextures(newTextures: Array<Textures?>)
}
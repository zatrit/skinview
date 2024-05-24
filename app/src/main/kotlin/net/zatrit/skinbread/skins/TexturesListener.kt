package net.zatrit.skinbread.skins

import net.zatrit.skinbread.Textures

interface TexturesListener {
    fun onTexturesAdded(
        textures: Textures, index: Int, order: Int, name: SourceName)

    fun setTextures(newTextures: Array<Textures?>)
}
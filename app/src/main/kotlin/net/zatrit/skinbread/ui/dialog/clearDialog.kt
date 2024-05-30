package net.zatrit.skinbread.ui.dialog

import android.app.Dialog
import net.zatrit.skinbread.R
import net.zatrit.skinbread.skins.clearTextures
import net.zatrit.skinbread.textures
import net.zatrit.skinbread.ui.TexturesActivity

@Suppress("ReplaceManualRangeWithIndicesCalls")
fun clearDialog(context: TexturesActivity): Dialog =
    dialogBuilder(context).apply {
        setTitle(R.string.wanna_clear)

        setPositiveButton(android.R.string.ok) { _, _ ->
            textures.fill(null)
            for (i in 0..<textures.size) {
                clearTextures(context, i)
            }
            context.setTextures(textures)
        }

        setNegativeButton(android.R.string.cancel, CancelDialog())
    }.create().applyDialogTheme()
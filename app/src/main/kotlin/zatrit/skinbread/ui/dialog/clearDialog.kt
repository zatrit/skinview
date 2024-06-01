package zatrit.skinbread.ui.dialog

import android.app.Dialog
import zatrit.skinbread.*
import zatrit.skinbread.skins.clearTextures
import zatrit.skinbread.ui.TexturesActivity

/** A dialog that asks the user if all saved textures should be deleted. */
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
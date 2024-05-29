package net.zatrit.skinbread.ui.dialog

import android.app.Dialog
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.clearTexturesAsync
import net.zatrit.skinbread.ui.TexturesActivity

fun clearDialog(context: TexturesActivity): Dialog =
  dialogBuilder(context).apply {
      setTitle(R.string.wanna_clear)

      setPositiveButton(android.R.string.ok) { _, _ ->
          textures.fill(null)
          clearTexturesAsync(context)
          context.setTextures(textures)
      }

      setNegativeButton(android.R.string.cancel, CancelDialog())
  }.create().apply {
      window?.attributes?.windowAnimations = R.style.WindowAnimations
  }
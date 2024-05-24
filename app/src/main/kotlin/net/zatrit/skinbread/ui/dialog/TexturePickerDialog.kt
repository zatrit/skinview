package net.zatrit.skinbread.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import net.zatrit.skinbread.R
import net.zatrit.skins.lib.TextureType

fun texturePickerDialog(context: Context, callback: (TextureType) -> Unit) =
    AlertDialog.Builder(ContextThemeWrapper(context, R.style.OLED_AlertDialog))
        .apply {

        }.create().apply {
            window?.attributes?.windowAnimations = R.style.OLED_AlertDialog
        }
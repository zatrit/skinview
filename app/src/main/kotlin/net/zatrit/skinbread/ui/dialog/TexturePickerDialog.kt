package net.zatrit.skinbread.ui.dialog

import android.app.*
import android.content.Context
import android.view.ContextThemeWrapper
import net.zatrit.skinbread.R
import net.zatrit.skins.lib.TextureType

fun texturePickerDialog(
    context: Context, callback: (TextureType) -> Unit): Dialog =
    AlertDialog.Builder(ContextThemeWrapper(context, R.style.OLED_AlertDialog))
        .apply {
            setView(R.layout.dialog_texture_type)

            setTitle("Выберите тип текстуры")

            setPositiveButton(android.R.string.ok) { dialog, _ ->
            }

            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
        }.create().apply {
            window?.attributes?.windowAnimations = R.style.WindowAnimations
        }
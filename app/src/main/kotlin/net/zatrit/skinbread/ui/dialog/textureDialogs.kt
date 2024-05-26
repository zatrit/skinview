package net.zatrit.skinbread.ui.dialog

import android.app.Dialog
import android.content.Context
import net.zatrit.skinbread.R

inline fun pickerDialog(
    context: Context, array: Int,
    crossinline callback: (Int) -> Unit,
): Dialog = dialogBuilder(context).apply {
    setTitle(R.string.pick_texture_type)

    var selected = 0
    setSingleChoiceItems(array, 0) { _, index ->
        selected = index
    }

    setPositiveButton(android.R.string.ok) { _, _ ->
        callback(selected)
    }

    setNegativeButton(android.R.string.cancel, CancelDialog())
}.create().apply {
    window?.attributes?.windowAnimations = R.style.WindowAnimations
}

inline fun textureTypeDialog(
    context: Context,
    crossinline callback: (Int) -> Unit,
) = pickerDialog(context, R.array.texture_type, callback)

inline fun textureModelDialog(
    context: Context,
    crossinline callback: (Int) -> Unit,
) = pickerDialog(context, R.array.model_type, callback)
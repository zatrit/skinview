package zatrit.skinbread.ui.dialog

import android.app.Dialog
import android.content.Context
import zatrit.skinbread.R

/** A dialog for selecting one of the elements in an XML array. */
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

    // Just close on cancel
    setNegativeButton(android.R.string.cancel, CancelDialog())
}.create().applyDialogTheme()

/**
 * Dialog for selecting the type of texture.
 * Passes to [callback] 0 if skin, 1 if cloak and 2 if ears. */
inline fun textureTypeDialog(
  context: Context,
  crossinline callback: (Int) -> Unit,
) = pickerDialog(context, R.array.texture_type, callback)

/**
 * Dialog for selecting player's model type.
 * Passes to [callback] 0 if default, 1 if slim. */
inline fun textureModelDialog(
  context: Context,
  crossinline callback: (Int) -> Unit,
) = pickerDialog(context, R.array.model_type, callback)
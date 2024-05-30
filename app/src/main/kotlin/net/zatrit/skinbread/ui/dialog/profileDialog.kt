package net.zatrit.skinbread.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.widget.Switch
import net.zatrit.skinbread.R
import net.zatrit.skinbread.edit
import net.zatrit.skinbread.ui.TexturesActivity

inline fun profileDialog(
  context: TexturesActivity,
  crossinline load: (String, String) -> Unit): Dialog {
    val prefs = context.preferences

    return dialogBuilder(context).apply {
        setView(R.layout.dialog_profile)
        setTitle(R.string.input_profile)

        setPositiveButton(android.R.string.ok) { dialog, _ ->
            val alertDialog = dialog as AlertDialog

            val name = alertDialog.getText(R.id.edittext_name)
            val uuid = alertDialog.getText(R.id.edittext_uuid)

            val remember =
                alertDialog.requireViewById<Switch>(R.id.switch_remember)

            if (remember.isChecked) prefs.edit {
                it.putString("profileName", name)
                it.putString("profileId", uuid)
            }

            load(name, uuid)
        }

        setNegativeButton(android.R.string.cancel, CancelDialog())
    }.create().apply {
        setOnShowListener { dialog ->
            if (dialog !is AlertDialog) return@setOnShowListener

            prefs.getString("profileName", null)?.also {
                dialog.setText(R.id.edittext_name, it)
            }

            prefs.getString("profileId", null)?.also {
                dialog.setText(R.id.edittext_uuid, it)
            }
        }
    }.applyDialogTheme()
}
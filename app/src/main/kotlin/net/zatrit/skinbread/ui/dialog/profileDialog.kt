package net.zatrit.skinbread.ui.dialog

import android.app.*
import android.widget.Switch
import net.zatrit.skinbread.R
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

            if (remember.isChecked) {
                val edit = prefs.edit()
                edit.putString("profileName", name)
                edit.putString("profileId", uuid)
                edit.apply()
            }

            load(name, uuid)
        }

        setNegativeButton(android.R.string.cancel, CancelDialog())
    }.create().apply {
        window?.attributes?.windowAnimations = R.style.WindowAnimations

        setOnShowListener { dialog ->
            if (dialog !is AlertDialog) return@setOnShowListener

            prefs.getString("profileName", null)?.also {
                dialog.setText(R.id.edittext_name, it)
            }

            prefs.getString("profileId", null)?.also {
                dialog.setText(R.id.edittext_uuid, it)
            }
        }
    }
}
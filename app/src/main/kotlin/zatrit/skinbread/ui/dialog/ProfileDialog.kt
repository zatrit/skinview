package zatrit.skinbread.ui.dialog

import android.app.*
import android.widget.*
import zatrit.skinbread.*
import zatrit.skinbread.ui.TexturesActivity

/** Dialog for entering player's profile data to load their skins. */
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

            if (name.isBlank() && uuid.isBlank()) {
                Toast.makeText(
                  context, R.string.must_not_be_empty, Toast.LENGTH_SHORT
                ).show()

                return@setPositiveButton
            }

            val remember =
              alertDialog.requireViewById<Switch>(R.id.switch_remember)

            // Saves the profile data if the user requests saving.
            if (remember.isChecked) prefs.edit {
                it.putString("profileName", name)
                it.putString("profileId", uuid)
            }

            load(name, uuid)
        }

        // Just close on cancel
        setNegativeButton(android.R.string.cancel, CancelDialog())

        setNeutralButton(R.string.sources) { _, _ ->
            filterDialog(context).show()
        }
    }.create().apply {
        setOnShowListener { dialog ->
            if (dialog !is AlertDialog) return@setOnShowListener

            // Loads the saved values for the profile.
            prefs.getString("profileName", null)?.let {
                dialog.setText(R.id.edittext_name, it)
            }

            prefs.getString("profileId", null)?.let {
                dialog.setText(R.id.edittext_uuid, it)
            }
        }
    }.applyDialogTheme()
}
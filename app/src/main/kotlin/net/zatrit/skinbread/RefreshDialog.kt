package net.zatrit.skinbread

import android.app.*
import android.view.ContextThemeWrapper
import android.widget.*

const val PROFILE_DIALOG = 1

inline fun profileDialog(
    activity: Activity,
    crossinline load: (String, String, Boolean) -> Unit): Dialog =
    AlertDialog.Builder(ContextThemeWrapper(activity, R.style.OLED_AlertDialog))
        .apply {
            setView(R.layout.profile_input)

            setPositiveButton(android.R.string.ok) { dialog, _ ->
                val alertDialog = dialog as AlertDialog

                val name = alertDialog.requireViewById<EditText>(
                    R.id.edittext_name
                ).text.toString()

                val uuid = alertDialog.requireViewById<EditText>(
                    R.id.edittext_uuid
                ).text.toString()

                val remember =
                    alertDialog.requireViewById<Switch>(R.id.switch_remember)

                load(name, uuid, remember.isChecked)
            }

            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
        }.create().apply {
            window?.attributes?.windowAnimations = R.style.OLED_AlertDialog
        }
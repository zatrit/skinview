package net.zatrit.skinbread.ui

import android.app.*
import android.content.SharedPreferences
import android.view.ContextThemeWrapper
import android.widget.*
import net.zatrit.skinbread.R

fun profileDialog(
    activity: Activity, prefs: SharedPreferences,
    load: (String, String) -> Unit): Dialog =
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

                if (remember.isChecked) {
                    val edit = prefs.edit()
                    edit.putString("profileName", name)
                    edit.putString("profileId", uuid)
                    edit.apply()
                }

                load(name, uuid)
            }

            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
        }.create().apply {
            window?.attributes?.windowAnimations = R.style.OLED_AlertDialog

            setOnShowListener { dialog ->
                if (dialog !is AlertDialog) return@setOnShowListener

                prefs.getString("profileName", null)?.also {
                    dialog.requireViewById<EditText>(R.id.edittext_name)
                        .setText(it)
                }

                prefs.getString("profileId", null)?.also {
                    dialog.requireViewById<EditText>(R.id.edittext_uuid)
                        .setText(it)
                }
            }
        }
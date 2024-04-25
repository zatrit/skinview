package net.zatrit.skinbread

import android.app.*
import android.content.Context
import android.widget.EditText

inline fun refreshDialog(
    context: Context, crossinline load: (String, String) -> Unit): Dialog =
    AlertDialog.Builder(context).apply {
        setTitle("Title")
        setView(R.layout.profile_input)

        setPositiveButton(android.R.string.ok) { dialog, _ ->
            val alertDialog = dialog as AlertDialog
            val name = alertDialog.findViewById<EditText>(
                R.id.edittext_name
            ).text.toString()
            val uuid = alertDialog.findViewById<EditText>(
                R.id.edittext_uuid
            ).text.toString()

            load(name, uuid)
        }

        setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }
    }.create().apply {
        window?.attributes?.windowAnimations = R.style.DialogAnimations
    }
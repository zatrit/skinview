package net.zatrit.skinbread.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import net.zatrit.skinbread.Library
import net.zatrit.skinbread.R


fun licenseDialog(context: Context, library: Library): Dialog =
    dialogBuilder(context).apply {
        setView(R.layout.dialog_license)
        setTitle(
          context.resources.getString(
            R.string.library_by_author, library.name, library.author
          )
        )

        setNeutralButton(R.string.source_code) { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(library.url))
            context.startActivity(intent)
        }

        setPositiveButton(android.R.string.ok, CancelDialog())
    }.create().applyDialogTheme().apply {
        setOnShowListener { dialog ->
            if (dialog !is AlertDialog) return@setOnShowListener

            val licenseText = requireViewById<TextView>(R.id.text_license)
            licenseText.text =
                context.resources.openRawResource(library.license).reader()
                  .readText()
        }
    }
package zatrit.skinbread.ui.dialog

import android.app.*
import android.content.*
import android.net.Uri
import android.widget.TextView
import zatrit.skinbread.*

/** A dialog that displays the text of the license for a particular library. */
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

      // Just close on OK
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
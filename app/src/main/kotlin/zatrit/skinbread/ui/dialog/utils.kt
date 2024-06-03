package zatrit.skinbread.ui.dialog

import android.app.AlertDialog
import android.content.*
import android.view.*
import android.widget.EditText
import zatrit.skinbread.R

/** A [DialogInterface] button click handler that hides a dialog. */
class CancelDialog : DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface, which: Int) = dialog.cancel()
}

/** Creates a new [AlertDialog.Builder] with [R.style.OLED_AlertDialog] theme. */
fun dialogBuilder(context: Context) = AlertDialog.Builder(
  ContextThemeWrapper(context, R.style.OLED_AlertDialog)
)

/** Gets [EditText] via [AlertDialog.requireViewById] and gets the text via [EditText.getText]. */
fun AlertDialog.getText(child: Int) =
    requireViewById<EditText>(child).text.toString()

/** Gets [EditText] via [AlertDialog.requireViewById] and sets the text via [EditText.setText]. */
fun AlertDialog.setText(child: Int, text: String) =
    requireViewById<EditText>(child).setText(text)

/** Sets [WindowManager.LayoutParams.windowAnimations] for [AlertDialog] to fix animations. */
fun AlertDialog.applyDialogTheme() = this.apply {
    window?.attributes?.windowAnimations = R.style.WindowAnimations
}
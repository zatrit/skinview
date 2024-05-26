package net.zatrit.skinbread.ui.dialog

import android.app.AlertDialog
import android.content.*
import android.content.DialogInterface.OnClickListener
import android.view.ContextThemeWrapper
import android.widget.EditText
import net.zatrit.skinbread.R

class CancelDialog : OnClickListener {
    override fun onClick(dialog: DialogInterface, which: Int) = dialog.cancel()
}

fun dialogBuilder(context: Context) = AlertDialog.Builder(
    ContextThemeWrapper(context, R.style.OLED_AlertDialog)
)

fun AlertDialog.getText(child: Int) =
    requireViewById<EditText>(child).text.toString()

fun AlertDialog.setText(child: Int, text: String) =
    requireViewById<EditText>(child).setText(text)
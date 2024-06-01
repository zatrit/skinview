package zatrit.skinbread.ui

import android.app.Dialog
import android.view.View

/** An implementation of [View.OnClickListener] that opens a dialog when clicked. */
open class ShowDialogHandler(private val dialog: Dialog) : View.OnClickListener {
    override fun onClick(v: View?) = dialog.show()
}
package zatrit.skinbread.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import zatrit.skinbread.*

/**
 * Shows dialog only when [loading] is complete,
 * otherwise shows toast [R.string.still_loading]. */
class ShowWhenLoadedHandler(context: Context, dialog: Dialog) :
  ShowDialogHandler(dialog), OnClickListener {
    /** Toast with the [R.string.still_loading] message. */
    private val stillLoadingToast = Toast.makeText(
      context, R.string.still_loading, Toast.LENGTH_SHORT
    )

    override fun onClick(v: View?) =
        if (loading == null || loading?.isDone == true) {
            super.onClick(v)
        } else {
            stillLoadingToast.show()
        }
}
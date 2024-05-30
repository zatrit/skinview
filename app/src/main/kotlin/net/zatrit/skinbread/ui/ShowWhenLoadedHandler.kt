package net.zatrit.skinbread.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import net.zatrit.skinbread.R
import net.zatrit.skinbread.loading

class ShowWhenLoadedHandler(context: Context, dialog: Dialog) :
  ShowDialogHandler(dialog), OnClickListener {
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
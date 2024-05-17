package net.zatrit.skinbread.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import net.zatrit.skinbread.*

class ShowWhenLoadedHandler(
    context: Context,
    private val dialog: Dialog,
) : OnClickListener {
    private val stillLoadingToast = Toast.makeText(
        context, R.string.still_loading, Toast.LENGTH_SHORT
    )

    override fun onClick(v: View?) {
        if (loading == null || loading?.isDone == true) {
            dialog.show()
        } else {
            stillLoadingToast.show()
        }
    }
}
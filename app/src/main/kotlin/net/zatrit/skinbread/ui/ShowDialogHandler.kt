package net.zatrit.skinbread.ui

import android.app.Dialog
import android.view.View

open class ShowDialogHandler(private val dialog: Dialog) : View.OnClickListener {
    override fun onClick(v: View?) = dialog.show()
}
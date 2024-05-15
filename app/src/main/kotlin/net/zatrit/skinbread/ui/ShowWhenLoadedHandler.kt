package net.zatrit.skinbread.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import net.zatrit.skinbread.R
import java.util.concurrent.CompletableFuture

interface HasLoading {
    var loading: CompletableFuture<Unit>?
}

class ShowWhenLoadedHandler<C>(
    private val context: C,
    private val dialog: Dialog,
) : OnClickListener where C : Context, C : HasLoading {
    private val alreadyLoadingToast = Toast.makeText(
        context, R.string.already_loading, Toast.LENGTH_SHORT
    )

    override fun onClick(v: View?) {
        if (context.loading == null || context.loading?.isDone == true) {
            dialog.show()
        } else {
            alreadyLoadingToast.show()
        }
    }
}
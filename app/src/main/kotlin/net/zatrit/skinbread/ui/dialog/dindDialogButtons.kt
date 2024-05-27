package net.zatrit.skinbread.ui.dialog

import android.app.*
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.widget.Button
import net.zatrit.skinbread.R
import net.zatrit.skinbread.ui.*

/** Represents the forth bit, while first two are used for specifying the texture type */
const val GET_TEXTURE_IMAGE = 8

fun TexturesActivity.bindDialogButtons(
    fetchDialog: Dialog, fetch: Int = R.id.btn_fetch,
    local: Int = R.id.btn_local) {
    requireViewById<Button>(fetch).setOnClickListener(
        ShowWhenLoadedHandler(this, fetchDialog)
    )

    val texturePickerDialog = textureTypeDialog(this) { texture ->
        // If the texture type is skin, additionally select the model type
        if (texture == 0) {
            textureModelDialog(this) { openTexture(texture, it) }.show()
        } else openTexture(texture, 0)
    }

    requireViewById<Button>(local).setOnClickListener(
        ShowDialogHandler(texturePickerDialog)
    )
}

private fun Activity.openTexture(texture: Int, model: Int) {
    val intent = Intent().setType("image/*").setAction(ACTION_GET_CONTENT)
    val model4 = model * 4
    startActivityForResult(intent, GET_TEXTURE_IMAGE + texture + model4)
}
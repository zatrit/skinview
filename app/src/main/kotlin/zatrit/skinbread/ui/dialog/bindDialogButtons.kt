package zatrit.skinbread.ui.dialog

import android.app.*
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.widget.Button
import zatrit.skinbread.R
import zatrit.skinbread.ui.*

// 0b00001MTT
// M - model
// T - texture type
const val OPEN_TEXTURE = 8

/** Binds buttons from [R.layout.action_buttons] to common dialog openings. */
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

/** Calls [Activity] to select a file to open. */
private fun Activity.openTexture(texture: Int, model: Int) {
    val intent = Intent().setType("image/*").setAction(ACTION_GET_CONTENT)
    val model4 = model * 4
    startActivityForResult(intent, OPEN_TEXTURE + texture + model4)
}

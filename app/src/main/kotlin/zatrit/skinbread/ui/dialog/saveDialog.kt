package zatrit.skinbread.ui.dialog

import android.app.*
import android.content.Intent
import zatrit.skinbread.*
import java.util.LinkedList

// 0b01IIIITT
// I - index
// T - texture type
const val SAVE_TEXTURE = 128

/** Dialog for saving a certain texture. */
fun saveDialog(context: Activity, index: Int, textures: Textures): Dialog =
  dialogBuilder(context).apply {
      setTitle(R.string.save)

      val items = LinkedList<Pair<Int, Int>>()

      textures.skin?.let { items.add(R.string.texture_skin to 0) }
      textures.cape?.let { items.add(R.string.texture_cape to 1) }
      textures.ears?.let { items.add(R.string.texture_ears to 2) }

      val names = Array(items.size) { context.getString(items[it].first) }

      var selected = 0
      setSingleChoiceItems(names, 0) { _, index ->
          selected = index
      }

      // Just close on cancel
      setNegativeButton(android.R.string.cancel, CancelDialog())

      setPositiveButton(android.R.string.ok) { _, _ ->
          val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).setType("image/png")
            .putExtra(Intent.EXTRA_TITLE, "texture.png")
          context.startActivityForResult(
            intent, SAVE_TEXTURE + index * 4 + items[selected].second
          )

      }
  }.create().applyDialogTheme()

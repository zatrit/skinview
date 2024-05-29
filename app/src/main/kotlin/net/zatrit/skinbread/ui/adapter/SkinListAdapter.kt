package net.zatrit.skinbread.ui.adapter

import android.view.*
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.*
import net.zatrit.skinbread.ui.ToggleSourcesActivity

class NamedEntry(
  val index: Int,
  val name: SourceName,
  var textures: Textures,
  var enabled: Boolean,
) {
    val preview = drawPreview(textures)
}

const val DISABLED_TRANSPARENCY = 0.6f

class SkinListAdapter(
  private val context: ToggleSourcesActivity,
  private val imageView: Int = R.id.img_preview,
  private val sourceSwitch: Int = R.id.switch_source,
  private val entry: Int = R.layout.entry_texture,
) : ArrayAdapter<NamedEntry>(context, entry) {
    private val inflater = context.layoutInflater

    // https://java2blog.com/android-custom-listview-with-images-text-example/
    override fun getView(
      position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(entry, null, true)
        val entry = this.getItem(position)!!

        val switch = view.requireViewById<Switch>(sourceSwitch)
        val image = view.requireViewById<ImageView>(imageView)

        if (convertView == null) {
            view.setOnClickListener { switch.toggle() }
        }

        switch.setOnCheckedChangeListener(null)
        switch.isChecked = entry.enabled
        switch.setOnCheckedChangeListener { _, state ->
            val alpha = if (state) 1f else DISABLED_TRANSPARENCY
            context.arranging.enabled[entry.index] = state
            view.animate().alpha(alpha).start()
            entry.enabled = state
        }

        view.alpha = if (entry.enabled) 1f else DISABLED_TRANSPARENCY
        switch.text = entry.name.getName(context)
        image.setImageBitmap(entry.preview)

        return view
    }

    override fun hasStableIds() = true
}
package zatrit.skinbread.ui.adapter

import android.view.*
import android.widget.*
import zatrit.skinbread.*
import zatrit.skinbread.skins.*
import zatrit.skinbread.ui.*
import zatrit.skinbread.ui.dialog.saveDialog

/** Source list item in [ToggleSourcesActivity] containing loaded textures and additional information. */
class NamedEntry(
  /** The source index in [defaultSources]. */
  val index: Int,
  /** The name of the source from which the [textures] are loaded. */
  val name: SourceName,
  /** Texture set. */
  var textures: Textures,
  /** Determines whether this texture set is enabled for display in [MainActivity]. */
  var enabled: Boolean,
) {
    /** [textures] preview. */
    val preview = drawPreview(textures)
}

/** Transparency for elements that have [NamedEntry.enabled] set to false. */
const val DISABLED_TRANSPARENCY = 0.6f

/** An adapter for [ToggleSourcesActivity] that displays loaded texture sets for sources. */
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

        // Bind this switch to activity click
        if (convertView == null) {
            bindClick(view) { switch.toggle() }
        }

        // Changes the value of the switch to entry.enabled and rebinds it to the current NamedEntry
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

        view.requireViewById<Button>(R.id.btn_save).setOnClickListener(
          ShowDialogHandler(saveDialog(context, entry.index, entry.textures))
        )

        return view
    }

    override fun hasStableIds() = true
}

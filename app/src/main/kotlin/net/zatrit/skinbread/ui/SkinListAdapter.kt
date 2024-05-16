package net.zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.view.*
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.*

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
    var skinSet: SkinSet,
    private val imageView: Int = R.id.img_preview,
    private val sourceSwitch: Int = R.id.switch_source,
    private val entry: Int = R.layout.texture_entry,
) : ArrayAdapter<NamedEntry>(context, entry) {
    // https://java2blog.com/android-custom-listview-with-images-text-example/
    @SuppressLint("ClickableViewAccessibility")
    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val view = convertView ?: inflater.inflate(entry, null, true)
        val entry = this.getItem(position)!!

        val switch = view.requireViewById<Switch>(sourceSwitch)
        val image = view.requireViewById<ImageView>(imageView)

        if (convertView == null) {
            view.setOnClickListener {
                switch.toggle()
            }
        }

        switch.setOnCheckedChangeListener(null)
        switch.isChecked = entry.enabled
        switch.setOnCheckedChangeListener { _, state ->
            val alpha = if (state) 1f else DISABLED_TRANSPARENCY
            skinSet.enabled[entry.index] = state
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
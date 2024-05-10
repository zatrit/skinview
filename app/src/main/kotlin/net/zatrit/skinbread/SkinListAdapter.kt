package net.zatrit.skinbread

import SourceName
import android.annotation.SuppressLint
import android.app.Activity
import android.view.*
import android.widget.*
import net.zatrit.skinbread.skins.drawPreview

class NamedEntry(
    val name: SourceName,
    var textures: Textures,
) {
    var enabled = false
    val preview = drawPreview(textures)
}

class SkinListAdapter(
    private val context: Activity,
    private val imageView: Int = R.id.imageview_preview,
    private val sourceSwitch: Int = R.id.source_switch,
    private val entry: Int = R.layout.texture_entry,
) : ArrayAdapter<NamedEntry>(context, entry) {
    // https://java2blog.com/android-custom-listview-with-images-text-example/
    @SuppressLint("ViewHolder")
    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val view = convertView ?: inflater.inflate(entry, null, true)
        val entry = this.getItem(position)!!

        val switch = view.requireViewById<Switch>(sourceSwitch)
        val previewView = view.requireViewById<ImageView>(imageView)

        switch.isClickable = false
        view.setOnClickListener {
            switch.toggle()
        }

        switch.setOnCheckedChangeListener { _, state ->
            view.alpha = if (state) 1f else 0.75f
            entry.enabled = state
        }

        switch.isChecked = entry.enabled
        view.alpha = if (entry.enabled) 1f else 0.75f

        switch.text = entry.name.getName(context)
        previewView.setImageBitmap(entry.preview)

        return view
    }

    override fun hasStableIds() = true
}
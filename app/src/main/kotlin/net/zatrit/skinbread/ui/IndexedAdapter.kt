package net.zatrit.skinbread.ui

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import net.zatrit.skinbread.R
import kotlin.math.log10

class IndexedAdapter(
    context: Context,
    private val entry: Int = R.layout.rearrange_list_entry,
) : ArrayAdapter<String>(context, entry) {
    private val inflater = LayoutInflater.from(context)

    override fun getView(
        position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(entry, null, true)
        val item = this.getItem(position)!!

        val index = position + 1
        val spanned = SpannableString("$index. $item")
        val idLen = log10(index.toFloat()).toInt()

        spanned.setSpan(
            ForegroundColorSpan(Color.GRAY), 0, idLen + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val textView = view.requireViewById<TextView>(R.id.text_source_name)
        textView.text = spanned

        return view
    }
}
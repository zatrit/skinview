package zatrit.skinbread.ui.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import zatrit.skinbread.R
import kotlin.math.log10

class IndexedAdapter(
  context: Context,
  private val entry: Int = R.layout.entry_rearrange,
  private val sourceName: Int = R.id.text_source_name,
  var hiddenItem: Int? = null,
) : ArrayAdapter<Pair<Int, String>>(context, entry) {
    private val inflater = LayoutInflater.from(context)

    override fun getView(
      position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(entry, null, true)
        val item = this.getItem(position)!!

        val index = item.first + 1
        val spanned = SpannableString("$index. ${item.second}")
        val idLen = log10(index.toFloat()).toInt() + 2

        spanned.setSpan(
          ForegroundColorSpan(Color.GRAY), 0, idLen,
          Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        val textView = view.requireViewById<TextView>(sourceName)
        textView.text = spanned

        view.alpha = if (position == hiddenItem) 0f else 1f

        return view
    }
}
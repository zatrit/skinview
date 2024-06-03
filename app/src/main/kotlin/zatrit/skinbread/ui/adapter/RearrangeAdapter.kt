package zatrit.skinbread.ui.adapter

import android.content.Context
import android.graphics.Color
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import zatrit.skinbread.R
import zatrit.skinbread.skins.Arranging
import zatrit.skinbread.ui.RearrangeActivity

/**
 * An adapter for [RearrangeActivity] containing the source names
 * and their numbers in [Arranging.order]. */
class RearrangeAdapter(
  context: Context,
  private val entry: Int = R.layout.entry_rearrange,
  private val sourceName: Int = R.id.text_source_name,
  /** Index of the element to be hidden. */
  var hiddenItem: Int? = null,
) : ArrayAdapter<Pair<Int, String>>(context, entry) {
    private val inflater = LayoutInflater.from(context)

    override fun getView(
      position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(entry, null, true)
        val item = this.getItem(position)!!

        val index = item.first + 1
        // e.g.: 1. Mantle
        val spanned = SpannableString("$index. ${item.second}")
        val idLen = index.toString().length + 1

        // Changes the color of the index to gray.
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
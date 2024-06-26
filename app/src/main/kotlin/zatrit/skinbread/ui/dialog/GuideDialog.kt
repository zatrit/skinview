package zatrit.skinbread.ui.dialog

import android.app.*
import android.content.Context
import android.text.*
import android.text.style.ImageSpan
import android.widget.TextView
import zatrit.skinbread.R
import zatrit.skins.lib.util.use2

/** A dialog that displays the text of the guide for SkinBread. */
fun guideDialog(context: Context): Dialog = dialogBuilder(context).apply {
    setView(R.layout.dialog_text)
    setTitle(R.string.greeting)

    // Just close on OK
    setPositiveButton(android.R.string.ok, CancelDialog())
}.create().applyDialogTheme().apply {
    setOnShowListener { dialog ->
        if (dialog !is AlertDialog) return@setOnShowListener

        val guideText = requireViewById<TextView>(R.id.text)
        val parts = context.resources.openRawResource(R.raw.guide).use2 {
            it.reader().readText()
        }.split("FETCH", "LOCAL")

        guideText.text = SpannableStringBuilder(parts[0]).apply {
            val height = guideText.lineHeight
            append(
              imageSpan(context, android.R.drawable.stat_sys_download, height)
            )
            append(parts[1])
            append(imageSpan(context, R.drawable.baseline_file_open, height))
            append(parts[2])
        }
    }
}

private fun imageSpan(
  context: Context, id: Int, lineHeight: Int): SpannableString {
    val image = context.resources.getDrawable(id, context.theme)
    val size = (lineHeight / 1.25f).toInt()
    image.setTint(
      context.resources.getColor(android.R.color.darker_gray, context.theme)
    )
    image.setBounds(0, 0, size, size)

    val span = ImageSpan(image, ImageSpan.ALIGN_CENTER)

    val spannable = SpannableString(" ");
    spannable.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannable
}
package zatrit.skinbread.ui.touch

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.*
import android.widget.*
import android.widget.AdapterView.*
import zatrit.skinbread.*
import zatrit.skinbread.ui.adapter.IndexedAdapter

/** Result signifying a change in the ID of the selected item. */
const val MOVE_CHANGED_ID = 0

/** A result signifying a successful touch. */
const val MOVE_OK = 1

/** A result signifying that there is no touch result. */
const val MOVE_NONE = 2

/** Handler for changing the order of elements [sourcesList]. */
class RearrangeHandler(
  context: Activity,
  private val sourcesList: ListView,
  private val adapter: IndexedAdapter,
  private val headerHeight: Int,
) : OnItemLongClickListener {
    /** Determines whether a drag n' drop is currently in progress. */
    private var dragging = false

    /** Drag n' drop destination. */
    var toItem = -1
        private set

    /** Drag n' drop source. */
    var fromItem = -1
        private set

    /** [ImageView] that displays the dragged item. */
    private val fakeItem = context.requireViewById<ImageView>(R.id.img_fake_item)

    override fun onItemLongClick(
      parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        sourcesList.isEnabled = false
        fadeView(sourcesList, 0.7f)

        fakeItem.setImageBitmap(view.drawToBitmap())
        fakeItem.visibility = VISIBLE

        view.alpha = 0f
        adapter.hiddenItem = position - sourcesList.headerViewsCount

        fakeItem.x = view.x
        fakeItem.y = view.y

        dragging = true

        val newId = sourcesList.getItem(position)!!.first
        fromItem = newId
        toItem = newId

        return true
    }

    fun moveTouch(event: MotionEvent): Int {
        // Do nothing if not dragging
        if (!dragging) return MOVE_NONE

        // fakeItem has almost the same height as any item in the list
        val halfHeight = fakeItem.height / 2
        fakeItem.y = event.y - headerHeight + halfHeight / 2

        val ry = event.y - halfHeight

        val position = sourcesList.pointToPosition(event.x.toInt(), ry.toInt())
        if (position == INVALID_POSITION) return MOVE_OK

        // Finds the View ID inside the list, considering the header and scroll
        val itemId = sourcesList.getItemIdAtPosition(
          position
        ) - sourcesList.firstVisiblePosition + sourcesList.headerViewsCount

        // Do nothing if the item is not found
        var id = sourcesList.getItem(position)?.first ?: return MOVE_OK

        val hovered = sourcesList.getChildAt(itemId.toInt())
        val innerPoint = ry - hovered.y
        val insertBeneath = innerPoint > halfHeight

        if (insertBeneath) id += 1

        /* If the move is to another element, changes the ID and returns
        MOVE_CHANGED_ID, otherwise returns MOVE_OK */
        return if (id != toItem) {
            hideInserts()

            hovered?.findViewById<View?>(
              if (insertBeneath) R.id.insert_bottom
              else R.id.insert_top
            )?.visibility = View.VISIBLE

            toItem = id

            MOVE_CHANGED_ID
        } else MOVE_OK
    }

    fun finishTouch() = if (dragging) {
        hideInserts()

        adapter.hiddenItem = null
        showAll()

        sourcesList.isEnabled = true
        fadeView(sourcesList, 1f)

        fakeItem.visibility = INVISIBLE
        dragging = false

        if (fromItem < toItem) toItem -= 1

        true
    } else false

    private fun hideInserts() = forEachChild { _, view ->
        view.findViewById<View>(R.id.insert_top)?.visibility = View.INVISIBLE
        view.findViewById<View>(R.id.insert_bottom)?.visibility = View.INVISIBLE
    }

    private fun showAll() = forEachChild { _, view ->
        if (view.alpha != 1f) view.alpha = 1f
    }

    private inline fun forEachChild(func: (Int, View) -> Unit) {
        for (i in 0..<sourcesList.childCount) {
            val view = sourcesList.getChildAt(i)
            func(i, view)
        }
    }

    private fun fadeView(view: View, alpha: Float) =
        ObjectAnimator.ofFloat(view, "alpha", view.alpha, alpha).apply {
            setDuration(150)
            start()
        }

    @Suppress("UNCHECKED_CAST")
    private fun ListView.getItem(position: Int) =
        getItemAtPosition(position) as Pair<Int, String>?
}
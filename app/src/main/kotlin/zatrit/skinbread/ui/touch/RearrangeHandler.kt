package zatrit.skinbread.ui.touch

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.*
import android.widget.*
import android.widget.AdapterView.*
import zatrit.skinbread.*
import zatrit.skinbread.ui.adapter.RearrangeAdapter

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
  private val adapter: RearrangeAdapter,
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

    /** Hides the selected item, shows [fakeItem] and starts dragging. */
    override fun onItemLongClick(
      parent: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        sourcesList.isEnabled = false
        fadeView(sourcesList, 0.7f)

        // Makes fakeItem look like clicked view
        fakeItem.setImageBitmap(view.drawToBitmap())
        fakeItem.visibility = VISIBLE

        view.alpha = 0f
        adapter.hiddenItem = position - sourcesList.headerViewsCount

        fakeItem.x = view.x
        fakeItem.y = view.y

        dragging = true

        // Sets the source and destination to newId
        val newId = sourcesList.getItem(position)!!.first
        fromItem = newId
        toItem = newId

        return true
    }

    /**
     * Handles the [event] of moving a touch.
     * @return [MOVE_NONE] if there was no touch before, [MOVE_OK] if the touch was processed
     * and [MOVE_CHANGED_ID] if the [toItem] value is changed.
     */
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

    /**
     * Finishes the drag and drop.
     * @return the value of [dragging] before calling this function */
    fun finishTouch() = if (dragging) {
        hideInserts()

        adapter.hiddenItem = null
        showAll()

        // Re-enables sourcesList
        sourcesList.isEnabled = true
        fadeView(sourcesList, 1f)

        fakeItem.visibility = INVISIBLE
        dragging = false

        if (fromItem < toItem) toItem -= 1

        true
    } else false

    /** Hides all items, displaying the insertion point for all children. */
    private fun hideInserts() = forEachChild { _, view ->
        view.findViewById<View>(R.id.insert_top)?.visibility = View.INVISIBLE
        view.findViewById<View>(R.id.insert_bottom)?.visibility = View.INVISIBLE
    }

    /** Sets 100% transparency for all children. */
    private fun showAll() = forEachChild { _, view ->
        if (view.alpha != 1f) view.alpha = 1f
    }

    /** Performs this function for all children. */
    private inline fun forEachChild(func: (Int, View) -> Unit) {
        for (i in 0..<sourcesList.childCount) {
            val view = sourcesList.getChildAt(i)
            func(i, view)
        }
    }

    /** Smoothly changes transparency [view] to [alpha]. */
    private fun fadeView(view: View, alpha: Float) =
      ObjectAnimator.ofFloat(view, "alpha", view.alpha, alpha).apply {
          setDuration(150)
          start()
      }

    /** @return the [ListView] item for the given visual [position]. */
    @Suppress("UNCHECKED_CAST")
    private fun ListView.getItem(position: Int) =
      getItemAtPosition(position) as Pair<Int, String>?
}
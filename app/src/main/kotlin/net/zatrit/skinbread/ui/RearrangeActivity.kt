package net.zatrit.skinbread.ui

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.*
import android.os.*
import android.os.VibrationEffect.createOneShot
import android.view.*
import android.view.MotionEvent.*
import android.view.View.VISIBLE
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.defaultSources

const val ORDER = "order"
const val I_HAVE_ORDER = 157

class RearrangeActivity : Activity() {
    private lateinit var sourcesList: AbsListView
    private lateinit var adapter: IndexedAdapter
    private lateinit var fakeItem: ImageView
    private lateinit var vibrator: Vibrator

    private var dragging = false
    private var lastTouchX = 0f
    private var offsetX = 0f

    private var selectedItem = -1
    private var fromItem = -1

    private lateinit var order: IntArray

    private lateinit var scroller: Scroller
    private var scrollZone = -1
    private var screenHeight = -1

    private var fakeItemFade: ObjectAnimator? = null

    @Suppress("DEPRECATION")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_rearrange)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        order = intent.getIntArrayExtra(ORDER)!!

        window.enterTransition = transition

        fakeItem = requireViewById(R.id.img_fake_item)
        sourcesList = requireViewById(R.id.list_sources)

        adapter = IndexedAdapter(this)
        adapter.setNotifyOnChange(false)
        fillAdapter()
        sourcesList.adapter = adapter

        scroller = Scroller(sourcesList, mainLooper)
        screenHeight = resources.displayMetrics.heightPixels
        scrollZone = screenHeight / 8

        sourcesList.setOnItemLongClickListener { _, item, id, _ ->
            sourcesList.isEnabled = false
            sourcesList.alpha = 0.7f

            fakeItem.setImageBitmap(item.drawToBitmap())
            fadeFakeItem(0.85f)

            fadeView(item, 0f)
            adapter.hiddenItem = id

            fakeItem.x = item.x
            fakeItem.y = item.y

            dragging = true
            offsetX = lastTouchX - item.x

            fromItem = id
            selectedItem = id

            true
        }
    }

    override fun onResume() {
        super.onResume()
        scroller.resume()
    }

    override fun onPause() {
        super.onPause()
        scroller.pause()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            // https://stackoverflow.com/a/46619474/12245612
            ACTION_DOWN -> {
                lastTouchX = event.x
            }

            ACTION_MOVE -> if (dragging) {
                fakeItem.x = event.x - offsetX
                fakeItem.y = event.y - fakeItem.height / 2

                val position = sourcesList.pointToPosition(
                    event.x.toInt(), event.y.toInt()
                )
                var id = sourcesList.getItemIdAtPosition(position).toInt()

                if (id < 0 || position < 0) return false

                scroller.scrollBy = if (event.y < scrollZone) {
                    -20
                } else if (event.y > screenHeight - scrollZone) {
                    20
                } else 0

                // http://www.code4kotlin.com/2014/08/how-to-access-child-views-from-listview.html
                val hovered = sourcesList.getChildAt(
                    position - sourcesList.firstVisiblePosition
                )
                val halfHeight = hovered.height / 2

                val pointInside = event.y - hovered.y
                val insertBeneath = pointInside > halfHeight

                if (insertBeneath) {
                    id += 1
                }

                if (id != selectedItem) {
                    hideInserts()

                    vibrator.vibrate(createOneShot(50, 12))

                    hovered?.findViewById<View?>(
                        if (insertBeneath) R.id.insert_bottom
                        else R.id.insert_top
                    )?.visibility = VISIBLE

                    selectedItem = id
                }
            }

            ACTION_UP, ACTION_CANCEL -> if (dragging) {
                hideInserts()

                adapter.hiddenItem = null
                showAll()

                sourcesList.isEnabled = true
                sourcesList.alpha = 1f

                fadeFakeItem(0f)
                dragging = false

                scroller.scrollBy = 0

                if (fromItem < selectedItem) {
                    selectedItem -= 1
                }

                order.moveItemTo(fromItem, selectedItem)

                adapter.clear()
                fillAdapter()
            }
        }

        return super.dispatchTouchEvent(event)
    }

    override fun finish() {
        setResult(I_HAVE_ORDER, Intent().putExtra(ORDER, order))
        super.finish()
    }

    private fun fillAdapter() {
        adapter.addAll(order.map { defaultSources[it].name.getName(this) })
        adapter.notifyDataSetChanged()
    }

    private fun hideInserts() = forEachChild { _, view ->
        view.findViewById<View>(R.id.insert_top)?.visibility = View.INVISIBLE
        view.findViewById<View>(R.id.insert_bottom)?.visibility = View.INVISIBLE
    }

    private fun showAll() = forEachChild { _, view ->
        if (view.alpha != 1f) fadeView(view, 1f)
    }

    private inline fun forEachChild(func: (Int, View) -> Unit) {
        for (i in 0..<sourcesList.childCount) {
            val view = sourcesList.getChildAt(i)
            func(i, view)
        }
    }

    private fun fadeFakeItem(alpha: Float) {
        fakeItemFade?.cancel()
        fakeItemFade = fadeView(fakeItem, alpha)
    }

    private fun fadeView(view: View, alpha: Float) =
        ObjectAnimator.ofFloat(view, "alpha", view.alpha, alpha).apply {
            setDuration(50)
            start()
        }
}

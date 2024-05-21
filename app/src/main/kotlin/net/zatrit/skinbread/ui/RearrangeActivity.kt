package net.zatrit.skinbread.ui

import android.app.Activity
import android.content.*
import android.os.*
import android.view.*
import android.view.MotionEvent.*
import android.view.View.*
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.defaultSources

const val ORDER = "order"
const val I_HAVE_ORDER = 157

class RearrangeActivity : Activity() {
    private lateinit var sourcesList: AbsListView
    private lateinit var adapter: ArrayAdapter<String>
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

    @Suppress("DEPRECATION")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_rearrange)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        order = intent.getIntArrayExtra(ORDER)!!
        val names = order.map { defaultSources[it].name.getName(this) }

        window.enterTransition = transition

        fakeItem = requireViewById(R.id.img_fake_item)
        sourcesList = requireViewById(R.id.list_sources)
        adapter = ArrayAdapter(
            this, R.layout.rearrange_list_entry, R.id.text_source_name
        )
        adapter.setNotifyOnChange(false)

        scroller = Scroller(sourcesList, mainLooper)

        adapter.addAll(names)
        adapter.notifyDataSetChanged()

        sourcesList.adapter = adapter

        scrollZone = resources.displayMetrics.heightPixels / 8

        sourcesList.setOnItemLongClickListener { _, item, id, _ ->
            sourcesList.isEnabled = false
            sourcesList.alpha = 0.7f

            fakeItem.setImageBitmap(item.drawToBitmap())
            fakeItem.visibility = VISIBLE

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

    private fun hideInserts() {
        for (i in 0..<sourcesList.childCount) {
            val view = sourcesList.getChildAt(i)
            view?.findViewById<View>(R.id.insert_top)?.visibility = INVISIBLE
            view?.findViewById<View>(R.id.insert_bottom)?.visibility = INVISIBLE
        }
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

                if (id < 0 || position < 0) {
                    return false
                }

                scroller.scrollBy = if (event.y < scrollZone) {
                    -20
                } else if (resources.displayMetrics.heightPixels - scrollZone < event.y) {
                    20
                } else {
                    0
                }

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

                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            25, VibrationEffect.EFFECT_TICK
                        )
                    )

                    val insertPoint: View? = if (insertBeneath) {
                        hovered?.findViewById(R.id.insert_bottom)
                    } else {
                        hovered?.findViewById(R.id.insert_top)
                    }

                    insertPoint?.visibility = VISIBLE
                    selectedItem = id
                }
            }

            ACTION_UP, ACTION_CANCEL -> if (dragging) {
                hideInserts()

                sourcesList.isEnabled = true
                sourcesList.alpha = 1f

                fakeItem.visibility = GONE
                dragging = false

                scroller.scrollBy = 0

                val toItem = if (fromItem < selectedItem) {
                    selectedItem - 1
                } else {
                    selectedItem
                }

                order.moveItemTo(fromItem, toItem)

                adapter.clear()
                adapter.addAll(
                    order.map { defaultSources[it].name.getName(this) })
                adapter.notifyDataSetChanged()
            }
        }

        return super.dispatchTouchEvent(event)
    }

    override fun finish() {
        setResult(I_HAVE_ORDER, Intent().putExtra(ORDER, order))
        super.finish()
    }
}

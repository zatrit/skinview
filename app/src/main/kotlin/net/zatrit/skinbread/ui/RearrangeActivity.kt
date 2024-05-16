package net.zatrit.skinbread.ui

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
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

    private var dragging = false
    private var lastTouchX = 0f
    private var offsetX = 0f

    private var selectedItem = -1
    private var fromItem = -1

    private lateinit var order: IntArray

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_rearrange)

        order = intent.getIntArrayExtra(ORDER)!!
        val names = order.map { defaultSources[it].name.getName(this) }

        window.enterTransition = activityTransition

        fakeItem = requireViewById(R.id.img_fake_item)
        sourcesList = requireViewById(R.id.list_sources)
        adapter = ArrayAdapter(
            this, R.layout.rearrange_list_entry, R.id.text_source_name
        )
        adapter.setNotifyOnChange(false)

        adapter.addAll(names)
        adapter.notifyDataSetChanged()

        sourcesList.adapter = adapter

        sourcesList.setOnItemLongClickListener { _, item, id, _ ->
            sourcesList.isEnabled = false
            sourcesList.alpha = 0.6f

            fakeItem.setImageBitmap(item.drawToBitmap())
            fakeItem.visibility = VISIBLE

            fakeItem.x = item.x
            fakeItem.y = item.y

            fakeItem.alpha = 0.9f

            dragging = true
            offsetX = lastTouchX - item.x

            fromItem = id
            selectedItem = id

            true
        }
    }

    private fun View.animateTintColor(from: Int, to: Int) =
        ValueAnimator.ofArgb(from, to).apply {
            repeatCount = 0
            duration = 500
            addUpdateListener {
                backgroundTintList =
                    ColorStateList.valueOf(it.animatedValue as Int)
            }
            start()
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

                val scrollZone = resources.displayMetrics.heightPixels / 8
                if (event.y > resources.displayMetrics.heightPixels - scrollZone) {
                    sourcesList.scrollListBy(10)
                }
                if (event.y < scrollZone) {
                    sourcesList.scrollListBy(-10)
                }

                val id = sourcesList.pointToPosition(
                    event.x.toInt(), event.y.toInt()
                )

                if (id != selectedItem && id != -1) {
                    // http://www.code4kotlin.com/2014/08/how-to-access-child-views-from-listview.html
                    val childId = sourcesList.getItemIdAtPosition(id)
                        .toInt() - sourcesList.firstVisiblePosition

                    val selectedColor = resources.getColor(
                        R.color.text, theme
                    ).let {
                        // https://stackoverflow.com/a/19287102/12245612
                        val factor = 10;
                        factor shl 24 or (it and 0x00ffffff)
                    }

                    val normalColor =
                        resources.getColor(R.color.background, theme)
                    val selected = sourcesList.getChildAt(childId)

                    selected?.animateTintColor(normalColor, selectedColor)

                    selectedItem = id
                }
            }

            ACTION_UP, ACTION_CANCEL -> if (dragging) {
                sourcesList.isEnabled = true
                sourcesList.alpha = 1f

                fakeItem.visibility = GONE
                dragging = false

                val a = order[selectedItem]
                order[selectedItem] = order[fromItem]
                order[fromItem] = a

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
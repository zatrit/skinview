package net.zatrit.skinbread.ui

import android.app.Activity
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.MotionEvent.*
import android.view.View.*
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.defaultSources
import android.os.Vibrator;
import android.os.VibrationEffect

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

    private lateinit var order: MutableList<Int>

    private var scrollZone = -1

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_rearrange)

        vibrator = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        order = intent.getIntArrayExtra(ORDER)!!.toMutableList()
        val names = order.map { defaultSources[it].name.getName(this) }

        window.enterTransition = transition

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

            dragging = true
            offsetX = lastTouchX - item.x

            fromItem = id
            selectedItem = id

            true
        }

        scrollZone = resources.displayMetrics.heightPixels / 8
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

                // http://www.code4kotlin.com/2014/08/how-to-access-child-views-from-listview.html
                val childId = sourcesList.getItemIdAtPosition(position)
                    .toInt() - sourcesList.firstVisiblePosition

                if (childId < 0 || position < 0) {
                    return false
                }

                val hovered = sourcesList.getChildAt(childId)
                val halfHeight = hovered.height / 2

                val pointInside = event.y - hovered.y
                val insertBeneath = pointInside > halfHeight
                val id = if (insertBeneath) {
                    childId + 1
                } else {
                    childId
                }

                if (id != selectedItem) {
                    hideInserts()

                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))

                    val insertPoint: View? = if (insertBeneath) {
                        hovered?.findViewById<View>(R.id.insert_bottom)
                    } else {
                        hovered?.findViewById<View>(R.id.insert_top)
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

                val newPos = if (selectedItem > fromItem) {
                    selectedItem - 1
                } else {
                    selectedItem
                }
                val a = order.removeAt(fromItem)
                order.add(newPos, a)

                adapter.clear()
                adapter.addAll(
                    order.map { defaultSources[it].name.getName(this) })
                adapter.notifyDataSetChanged()
            }
        }

        return super.dispatchTouchEvent(event)
    }

    override fun finish() {
        setResult(I_HAVE_ORDER, Intent().putExtra(ORDER, order.toTypedArray()))
        super.finish()
    }
}

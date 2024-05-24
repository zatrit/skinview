package net.zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.*
import android.view.*
import android.view.MotionEvent.*
import android.view.View.MeasureSpec.UNSPECIFIED
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.defaultSources
import net.zatrit.skinbread.ui.touch.*

const val ORDER = "order"
const val I_HAVE_ORDER = 157

class RearrangeActivity : Activity() {
    private lateinit var sourcesList: ListView
    private lateinit var adapter: IndexedAdapter
    private lateinit var fakeItem: ImageView
    private lateinit var vibrator: Vibrator

    private lateinit var order: IntArray

    private lateinit var scroller: Scroller
    private lateinit var handler: RearrangeHandler

    private var scrollZone = -1
    private var screenHeight = -1

    private var showAll = false

    @SuppressLint("InflateParams")
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
        populateList()
        sourcesList.adapter = adapter

        val header = layoutInflater.inflate(R.layout.rearrange_list_header, null)
        sourcesList.addHeaderView(header)

        header.measure(UNSPECIFIED, UNSPECIFIED)
        handler =
            RearrangeHandler(this, sourcesList, adapter, header.measuredHeight)

        window.run {
            clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            statusBarColor = resources.getColor(R.color.card_background, theme)
        }

        scroller = Scroller(sourcesList, mainLooper)
        screenHeight = resources.displayMetrics.heightPixels
        scrollZone = screenHeight / 8

        val radioShow = header.requireViewById<RadioGroup>(R.id.radio_show)
        radioShow.setOnCheckedChangeListener { _, checkedId ->
            showAll = when (checkedId) {
                R.id.radio_show_all -> true
                R.id.radio_show_loaded -> false
                else -> true
            }

            populateList()
        }

        // If there are no skins downloaded, show all
        if (textures.all { it == null }) radioShow.check(R.id.radio_show_all)

        sourcesList.setOnItemLongClickListener(handler)
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
            ACTION_MOVE -> when (handler.moveTouch(event)) {
                MOVE_OK -> {
                    scroller.scrollBy = if (event.y < scrollZone) {
                        -20
                    } else if (event.y > screenHeight - scrollZone) {
                        20
                    } else 0
                }

                MOVE_CHANGED_ID -> {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, 50))
                }
            }

            ACTION_CANCEL, ACTION_UP -> {
                scroller.scrollBy = 0

                if (handler.finishTouch()) {
                    order.moveItemTo(handler.fromItem, handler.toItem)
                    populateList()
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    override fun finish() {
        setResult(I_HAVE_ORDER, Intent().putExtra(ORDER, order))
        super.finish()
    }


    private fun populateList() {
        adapter.clear()

        order.forEachIndexed { i, j ->
            if (showAll || (textures[j]?.isEmpty() == false)) {
                val name = defaultSources[j].name.getName(this)
                adapter.add(Pair(i, name))
            }
        }

        adapter.notifyDataSetChanged()
    }
}

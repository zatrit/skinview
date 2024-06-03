package zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.os.*
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View.MeasureSpec.UNSPECIFIED
import android.widget.*
import zatrit.skinbread.*
import zatrit.skinbread.skins.defaultSources
import zatrit.skinbread.ui.adapter.RearrangeAdapter
import zatrit.skinbread.ui.touch.*

/** Field name in [Bundle] for order. */
const val ORDER = "order"

/** Result code for a result containing order. */
const val I_HAVE_ORDER = 157

/** Field name in [SharedPreferences] for [RearrangeActivity.showAll]. */
private const val SHOW_ALL = "showAll"

/**
 * An [Activity] that allows you to change the order of sources. Has the option
 * to change the order of the source only among the loaded sources, or among all sources. */
class RearrangeActivity : Activity() {
    /** List of source names. */
    private lateinit var sourcesList: ListView

    /** Adapter for [sourcesList]. */
    private lateinit var adapter: RearrangeAdapter

    /** A vibrator used to respond to changes in the selected item. */
    private lateinit var vibrator: Vibrator

    /** The order of items used to arrange [sourcesList] items. */
    private lateinit var order: IntArray

    private lateinit var scroller: Scroller
    private lateinit var handler: RearrangeHandler

    private var scrollZone = -1
    private var screenHeight = -1

    /** If true, [sourcesList] shows all sources, otherwise only those with loaded textures. */
    private var showAll = false

    @Suppress("DEPRECATION")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        // Loads activity content and shows title bar
        setContentView(R.layout.activity_rearrange)
        enableTitleBar()

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        order = intent.getIntArrayExtra(ORDER)!!

        sourcesList = requireViewById(R.id.list_sources)

        adapter = RearrangeAdapter(this).apply { setNotifyOnChange(false) }
        sourcesList.adapter = adapter
        populateList()

        @SuppressLint("InflateParams")
        // Creates a header for sourcesList without a parent and adds it.
        val header = layoutInflater.inflate(R.layout.header_rearrange_list, null)
        sourcesList.addHeaderView(header)

        // Measures the size of the header to use it to calculate the item ID
        header.measure(UNSPECIFIED, UNSPECIFIED)
        handler =
            RearrangeHandler(this, sourcesList, adapter, header.measuredHeight)
        sourcesList.setOnItemLongClickListener(handler)

        scroller = Scroller(sourcesList, mainLooper)
        screenHeight = resources.displayMetrics.heightPixels
        // Scroll area that is 1/8 of the screen height
        scrollZone = screenHeight / 8

        // Implementation of showAll switching with RadioGroup R.id.radio_show
        val radioShow = header.requireViewById<RadioGroup>(R.id.radio_show)
        radioShow.setOnCheckedChangeListener { _, checkedId ->
            showAll = when (checkedId) {
                R.id.radio_show_loaded -> false
                else -> true
            }

            populateList()
        }

        // If there are no skins downloaded, show all
        showAll = textures.all { it == null || it.isEmpty() }
        // Loads saved showAll value
        showAll = state?.getBoolean(SHOW_ALL) ?: showAll

        if (showAll) radioShow.check(R.id.radio_show_all)
    }

    override fun onResume() {
        super.onResume()
        scroller.resume()
    }

    override fun onPause() {
        super.onPause()
        scroller.pause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SHOW_ALL, showAll)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            // Accesses the handler and passes the result to the vibrator and scroller
            ACTION_MOVE -> when (handler.moveTouch(event)) {

                /* If the touch occurred at a distance less than scrollZone
                from the top or bottom edge, it starts scrolling */
                MOVE_OK -> {
                    scroller.scrollBy = if (event.y < scrollZone) {
                        -20
                    } else if (event.y > screenHeight - scrollZone) {
                        20
                    } else 0
                }

                MOVE_CHANGED_ID -> vibrator.vibrate(
                  VibrationEffect.createOneShot(50, 50)
                )
            }

            /* Moves the item to another location if the handler
            successfully completes the touch and stops the scroll */
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
        // Set an activity result
        setResult(I_HAVE_ORDER, Intent().putExtra(ORDER, order))
        super.finish()
    }

    /** Fills the list with source names, according to [order] and [defaultSources]. */
    private fun populateList() {
        adapter.clear()

        order.forEachIndexed { i, j ->
            // If all sources are to be shown or this source has a loaded texture, adds its name
            if (showAll || (textures[j]?.isEmpty() == false)) {
                val name = defaultSources[j].name.getName(this)
                adapter.add(Pair(i, name))
            }
        }

        adapter.notifyDataSetChanged()
    }
}

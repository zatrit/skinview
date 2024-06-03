package zatrit.skinbread.ui

import android.os.*
import android.widget.AbsListView

private const val DELAY = 10L

/** Scrolls [target] every [DELAY] milliseconds by [scrollBy]. */
class Scroller(private val target: AbsListView, looper: Looper) : Runnable {
    private val handler = Handler(looper)

    /** Scroll value. */
    var scrollBy = 0

    /** Resumes scrolling. */
    fun resume() = handler.post(this)

    /** Pauses scrolling. */
    fun pause() = handler.removeCallbacksAndMessages(0)

    // https://stackoverflow.com/a/27175305/12245612
    override fun run() {
        target.scrollListBy(scrollBy)
        handler.postDelayed(this, 0, DELAY)
    }
}
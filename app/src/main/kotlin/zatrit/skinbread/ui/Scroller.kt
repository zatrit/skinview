package zatrit.skinbread.ui

import android.os.*
import android.widget.AbsListView

class Scroller(private val target: AbsListView, looper: Looper) : Runnable {
    private val handler = Handler(looper)
    var scrollBy = 0

    fun resume() = handler.post(this)

    fun pause() = handler.removeCallbacksAndMessages(0)

    // https://stackoverflow.com/a/27175305/12245612
    override fun run() {
        target.scrollListBy(scrollBy)
        handler.postDelayed(this, 0, 10)
    }
}
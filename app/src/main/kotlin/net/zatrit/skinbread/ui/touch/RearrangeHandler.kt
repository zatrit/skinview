package net.zatrit.skinbread.ui.touch

import android.view.*
import android.view.MotionEvent.ACTION_DOWN
import android.view.View.OnTouchListener

class RearrangeHandler : OnTouchListener {
    var enabled: Boolean = false

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == ACTION_DOWN) {
            v.performClick()
        }

        return true
    }
}

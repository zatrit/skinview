package net.zatrit.skinview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.LayoutParams
import android.widget.Button

class DragHandle(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet), OnTouchListener {
    private var animation: ValueAnimator? = null
    var showInstead: Button? = null

    private val metrics = resources.displayMetrics
    private val step = metrics.heightPixels / 7

    lateinit var target: View

    init {
        setOnTouchListener(this)
    }

    private fun hide() {
        target.visibility = INVISIBLE
        showInstead?.visibility = VISIBLE
    }

    private fun heightAnimator(from: Int, to: Int) =
        ValueAnimator.ofInt(from, to).apply {
            addUpdateListener {
                target.applyLayout<LayoutParams> {
                    height = it.animatedValue as Int
                }
            }
            start()
        }

    fun show() {
        target.visibility = VISIBLE
        showInstead?.visibility = INVISIBLE

        val newHeight = resources.displayMetrics.heightPixels / 2

        animation = heightAnimator(1, newHeight)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                animation?.cancel()
                target.applyLayout<LayoutParams> {
                    height -= event.y.toInt()
                    if (height < step / 3) hide()
                }
            }

            MotionEvent.ACTION_UP -> {
                val height = target.layoutParams.height
                val newHeight = (height / step * step).coerceAtLeast(step)
                    .coerceAtMost(metrics.heightPixels - step)

                animation = heightAnimator(height, newHeight)
            }
        }
        return true
    }
}
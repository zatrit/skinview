package net.zatrit.skinbread

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.Button

class DragHandle(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {
    private var animation: ValueAnimator? = null
    var showInstead: Button? = null

    private val metrics = resources.displayMetrics
    private val step = metrics.heightPixels / 4
    private val initHeight = step * 2

    lateinit var target: View

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

        animation = heightAnimator(1, initHeight)
    }

    private fun hide() {
        target.visibility = INVISIBLE
        showInstead?.visibility = VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                animation?.cancel()
                target.applyLayout<LayoutParams> {
                    height -= event.y.toInt()
                    if (height < step / 2) hide()
                }
            }

            MotionEvent.ACTION_UP -> {
                val height = target.layoutParams.height
                val newHeight = (height / step * step).coerceAtLeast(step)
                    .coerceAtMost(metrics.heightPixels)

                animation = heightAnimator(height, newHeight)
            }
        }

        return true
    }
}
package net.zatrit.skinbread

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.ViewGroup.LayoutParams

class DragHandle(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {
    private var animation: ValueAnimator? = null
    var showInstead: View? = null

    private val metrics = resources.displayMetrics
    private val step = metrics.heightPixels / 8
    private val initHeight = step * 3

    lateinit var target: View

    val isVisible
        get() = target.visibility == VISIBLE

    private fun heightAnimator(from: Int, to: Int, thenHide: View? = null) =
        ValueAnimator.ofInt(from, to).apply {
            addUpdateListener {
                target.applyLayout<LayoutParams> {
                    height = it.animatedValue as Int
                }

                if (it.animatedValue == to) {
                    thenHide?.setTransitionVisibility(GONE)
                }
            }
            start()
        }

    fun show() {
        target.visibility = VISIBLE
        animation = heightAnimator(1, initHeight, showInstead)
    }

    fun hide() {
        animation = heightAnimator(target.height, 1, target)
        showInstead?.visibility = VISIBLE
    }

    private fun hideInternal() {
        target.visibility = GONE
        showInstead?.visibility = VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                animation?.cancel()
                target.applyLayout<LayoutParams> {
                    height = (height - event.y.toInt()).coerceAtLeast(1)
                }
            }

            MotionEvent.ACTION_UP -> {
                if (target.layoutParams.height < step / 2) {
                    hideInternal()
                } else {
                    val height = target.layoutParams.height
                    val newHeight = (height / step * step).coerceAtLeast(step)
                        .coerceAtMost(metrics.heightPixels)

                    animation = heightAnimator(height, newHeight)
                }
            }
        }

        return true
    }
}
package net.zatrit.skinbread.ui.touch

import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import net.zatrit.skinbread.applyLayout

class MenuDragHandler(
  private val target: View, private val showInstead: View? = null,
  resources: Resources) : OnTouchListener {

    private var animation: ValueAnimator? = null

    private val metrics = resources.displayMetrics
    private val step = metrics.heightPixels / 8
    private val initHeight = step * 3

    val isVisible
        get() = target.visibility == View.VISIBLE

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            ACTION_DOWN -> v.performClick()

            ACTION_MOVE -> {
                animation?.cancel()
                target.applyLayout<ViewGroup.LayoutParams> {
                    height = (height - event.y.toInt()).coerceAtLeast(1)
                }
            }

            ACTION_UP -> {
                if (target.layoutParams.height < step / 2) hideInstant()
                else {
                    val height = target.layoutParams.height
                    val newHeight = (height / step * step).coerceAtLeast(step)
                      .coerceAtMost(metrics.heightPixels)

                    animation = heightAnimator(height, newHeight)
                }
            }
        }

        return true
    }

    private fun heightAnimator(from: Int, to: Int, thenHide: View? = null) =
        ValueAnimator.ofInt(from, to).apply {
            addUpdateListener {
                target.applyLayout<ViewGroup.LayoutParams> {
                    height = it.animatedValue as Int
                }

                if (it.animatedValue == to) {
                    thenHide?.setTransitionVisibility(View.GONE)
                }
            }
            start()
        }

    fun show() {
        target.visibility = View.VISIBLE
        animation = heightAnimator(1, initHeight, showInstead)
    }

    fun hide() {
        animation = heightAnimator(target.height, 1, target)
        showInstead?.visibility = View.VISIBLE
    }

    private fun hideInstant() {
        target.visibility = View.GONE
        showInstead?.visibility = View.VISIBLE
    }
}
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
    var target: View? = null
    var showInstead: Button? = null

    private val metrics = resources.displayMetrics
    private val step = metrics.heightPixels / 7

    init {
        setOnTouchListener(this)
    }

    private fun hide() {
        target?.visibility = INVISIBLE
        showInstead?.visibility = VISIBLE
    }

    fun show() {
        target?.visibility = VISIBLE
        showInstead?.visibility = INVISIBLE

        val newHeight = resources.displayMetrics.heightPixels / 2
        animation = ValueAnimator.ofInt(1, newHeight).apply {
            addUpdateListener {
                target?.applyLayout<LayoutParams> {
                    this.height = it.animatedValue as Int
                }
            }
            start()
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (target == null) {
            return false
        }
        val target = target!!

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

                animation = ValueAnimator.ofInt(height, newHeight).apply {
                    addUpdateListener {
                        target.applyLayout<LayoutParams> {
                            this.height = it.animatedValue as Int
                        }
                    }
                    start()
                }
            }
        }
        return true
    }
}
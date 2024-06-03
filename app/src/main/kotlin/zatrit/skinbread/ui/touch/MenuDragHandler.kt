package zatrit.skinbread.ui.touch

import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.*
import android.view.MotionEvent.*
import android.view.View.OnTouchListener
import zatrit.skinbread.*

/** The touch handler for [R.id.drag_handle], responsible for resizing and hiding [target]. */
class MenuDragHandler(
  /** The target [View], the size of which changes from touch. */
  private val target: View,

  /** [View], which is shown when [target] is hidden. */
  private val showInstead: View? = null,

  resources: Resources,
) : OnTouchListener {
    /** [ValueAnimator], used to resize [target]. */
    private var animation: ValueAnimator? = null

    /** The height of the screen used to calculate the values used to hide and move [target]. */
    private val screenHeight = resources.displayMetrics.heightPixels

    /** The step to which the [target] size is rounded after the touch is completed. */
    private val step = screenHeight / 8

    /** The initial height for [target]. */
    private val initHeight = step * 3

    /** @return true if [target] is visible. */
    val isVisible
        get() = target.visibility == View.VISIBLE

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            // Passes the touch to the view
            ACTION_DOWN -> v.performClick()

            // Changes the size of the target depending on the position of the touch
            ACTION_MOVE -> {
                animation?.cancel()
                target.applyLayout<ViewGroup.LayoutParams> {
                    height = (height - event.y.toInt()).coerceAtLeast(1)
                }
            }

            // Smoothly rounds the target size to step * n
            ACTION_UP -> {
                if (target.layoutParams.height < step / 2) hideInstant()
                else {
                    val height = target.layoutParams.height
                    val newHeight = (height / step * step).coerceAtLeast(step)
                      .coerceAtMost(screenHeight)

                    animation = heightAnimator(height, newHeight)
                }
            }
        }

        return true
    }

    /** Creates a new [ValueAnimator] to smoothly resize [target]. */
    private fun heightAnimator(from: Int, to: Int, thenHide: View? = null) =
      ValueAnimator.ofInt(from, to).apply {
          addUpdateListener {
              target.applyLayout<ViewGroup.LayoutParams> {
                  height = it.animatedValue as Int
              }

              /* If thenHide isn't null and animation is
              finished, start the hide animation */
              if (thenHide != null && it.animatedValue == to) {
                  thenHide.setTransitionVisibility(View.GONE)
              }
          }
          start()
      }

    /** Smoothly hides [showInstead] and displays [target]. */
    fun show() {
        target.visibility = View.VISIBLE
        animation = heightAnimator(1, initHeight, showInstead)
    }

    /** Smoothly hides [target] and displays [showInstead]. */
    fun hide() {
        animation = heightAnimator(target.height, 1, target)
        showInstead?.visibility = View.VISIBLE
    }

    /** Instantly shows [showInstead] and hides [target]. */
    private fun hideInstant() {
        target.visibility = View.GONE
        showInstead?.visibility = View.VISIBLE
    }
}
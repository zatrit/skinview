package zatrit.skinbread.ui.touch

import android.opengl.Matrix.rotateM
import android.view.*
import android.view.MotionEvent.*
import zatrit.skinbread.gl.Renderer

/** Handler for view rotation by touch. */
class ModelRotateHandler(
  private val renderer: Renderer, private val units: Int) :
  View.OnTouchListener {
    /** [VelocityTracker], which tracks the movement of the touch for rotation. */
    private val velocityTracker = VelocityTracker.obtain()

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        velocityTracker.addMovement(event)

        when (event.actionMasked) {
            // Passes the touch to the view
            ACTION_DOWN -> v.performClick()

            // Rotates the view if the user moves the touch
            ACTION_MOVE -> velocityTracker?.apply {
                computeCurrentVelocity(units, 10f)

                val pointerId = event.getPointerId(event.actionIndex)
                val dx = getXVelocity(pointerId)
                val dy = getYVelocity(pointerId)

                // https://stackoverflow.com/a/8852416/12245612
                renderer.viewMatrix.let { m ->
                    rotateM(m, 0, dx, 0f, m[5], 0f)
                    rotateM(m, 0, dy, m[0], 0f, m[8])
                }
            }
        }

        return true
    }
}
package net.zatrit.skinbread.ui.touch

import android.opengl.Matrix.rotateM
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.VelocityTracker
import android.view.View
import net.zatrit.skinbread.gl.Renderer

class ModelRotateHandler(
  private val renderer: Renderer, private val density: Int) :
  View.OnTouchListener {
    private val velocityTracker = VelocityTracker.obtain()

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        velocityTracker.addMovement(event)

        when (event.actionMasked) {
            ACTION_DOWN -> v.performClick()

            ACTION_MOVE -> velocityTracker?.apply {
                computeCurrentVelocity(density)

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
package net.zatrit.skinbread

import android.annotation.SuppressLint
import android.content.res.Resources
import android.opengl.Matrix.rotateM
import android.view.*
import net.zatrit.skinbread.gl.Renderer

class ModelRotateHandler(private val renderer: Renderer, resources: Resources) :
    View.OnTouchListener {
    private var velocityTracker: VelocityTracker? = null
    private val density = resources.displayMetrics.density.toInt()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> velocityTracker =
                VelocityTracker.obtain().apply { addMovement(event) }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> velocityTracker?.recycle()

            MotionEvent.ACTION_MOVE -> velocityTracker?.apply {
                addMovement(event)
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
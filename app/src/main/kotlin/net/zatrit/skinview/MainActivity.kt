package net.zatrit.skinview

import android.app.Activity
import android.opengl.GLSurfaceView
import android.opengl.Matrix.rotateM
import android.os.Bundle
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.VelocityTracker
import android.view.View
import net.zatrit.skinview.gl.Renderer

class MainActivity : Activity() {
    private var surface: GLSurfaceView? = null
    private var velocityTracker: VelocityTracker? = null
    private var renderer: Renderer = Renderer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.surface = GLSurfaceView(this).apply {
            setEGLContextClientVersion(3)
            setRenderer(renderer)
            setOnTouchListener(::onSurfaceTouch)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }

        this.setContentView(surface)
    }

    override fun onResume() {
        super.onResume()
        surface?.onResume()
    }

    override fun onPause() {
        super.onPause()
        surface?.onPause()
    }

    private fun onSurfaceTouch(view: View, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            ACTION_DOWN -> velocityTracker =
                VelocityTracker.obtain().apply { addMovement(event) }

            ACTION_UP, ACTION_CANCEL -> velocityTracker?.recycle()

            ACTION_MOVE -> velocityTracker?.apply {
                val pointerId: Int = event.getPointerId(event.actionIndex)
                addMovement(event)
                computeCurrentVelocity(
                    resources.displayMetrics.density.toInt()
                )

                val dx = getXVelocity(pointerId)
                val dy = getYVelocity(pointerId)
                val m = renderer.modelMatrix

                // https://stackoverflow.com/a/8852416/12245612
                rotateM(m, 0, dx, 0f, m[5], 0f)
                rotateM(m, 0, dy, m[0], 0f, m[8])
            }
        }
        return true
    }
}

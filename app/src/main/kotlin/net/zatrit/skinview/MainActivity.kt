package net.zatrit.skinview

import android.app.Activity
import android.opengl.GLSurfaceView
import android.opengl.Matrix.rotateM
import android.os.Bundle
import android.view.*
import android.view.MotionEvent.*
import android.widget.*
import android.widget.RelativeLayout.LEFT_OF
import net.zatrit.skinview.gl.Renderer

class MainActivity : Activity() {
    private lateinit var velocityTracker: VelocityTracker
    private var renderer = Renderer(this)

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        this.setContentView(R.layout.activity_main)

        val surface = findViewById<GLSurfaceView>(R.id.gl_surface).apply {
            setEGLContextClientVersion(3)
            setOnTouchListener(::onSurfaceTouch)
            setRenderer(renderer)
        }

        val menu = findViewById<LinearLayout>(R.id.menu)
        val showButton = findViewById<Button>(R.id.show_button)

        val dragHandle = findViewById<DragHandle?>(R.id.drag_handle)?.apply {
            target = menu
            showInstead = showButton
        }

        showButton.setOnClickListener { dragHandle?.show() }

        renderer.modelMatrix =
            state?.getFloatArray("modelMatrix") ?: renderer.modelMatrix

        // Attaches surface at left of the menu if using landscape mode
        if (display?.rotation!! % 2 == 1) {
            surface.applyLayout<RelativeLayout.LayoutParams> {
                rules[LEFT_OF] = menu.id
            }
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        state.putFloatArray("modelMatrix", renderer.modelMatrix)
    }

    private fun onSurfaceTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            ACTION_DOWN -> velocityTracker =
                VelocityTracker.obtain().apply { addMovement(event) }

            ACTION_UP, ACTION_CANCEL -> velocityTracker.recycle()

            ACTION_MOVE -> velocityTracker.run {
                addMovement(event)
                computeCurrentVelocity(
                    resources.displayMetrics.density.toInt()
                )

                val pointerId = event.getPointerId(event.actionIndex)
                val dx = getXVelocity(pointerId)
                val dy = getYVelocity(pointerId)

                // https://stackoverflow.com/a/8852416/12245612
                renderer.modelMatrix.let { m ->
                    rotateM(m, 0, dx, 0f, m[5], 0f)
                    rotateM(m, 0, dy, m[0], 0f, m[8])
                }
            }
        }

        return true
    }
}

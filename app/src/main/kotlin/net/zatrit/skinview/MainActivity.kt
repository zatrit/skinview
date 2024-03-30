package net.zatrit.skinview

import android.app.Activity
import android.graphics.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.rotateM
import android.os.Bundle
import android.view.*
import android.view.MotionEvent.*
import android.widget.*
import android.widget.RelativeLayout.LEFT_OF
import net.zatrit.skinview.gl.*
import net.zatrit.skinview.skins.*
import kotlin.concurrent.thread


inline fun Activity.bindSwitch(
    id: Int, crossinline onSet: (Boolean) -> Unit, value: Boolean) {
    findViewById<Switch>(id).apply {
        setOnCheckedChangeListener { _, state ->
            onSet(state)
        }
        isChecked = value
    }
}

class MainActivity : Activity() {
    private lateinit var velocityTracker: VelocityTracker
    private var renderer = Renderer()

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        this.setContentView(R.layout.activity_main)

        val surface = findViewById<GLSurfaceView>(R.id.gl_surface).apply {
            setEGLContextClientVersion(3)
            setOnTouchListener(::onSurfaceTouch)
            setRenderer(renderer)
        }

        val menu = findViewById<LinearLayout>(R.id.menu)
        val showButton = findViewById<Button>(R.id.btn_show)

        val dragHandle = findViewById<DragHandle?>(R.id.drag_handle)?.apply {
            target = menu
            showInstead = showButton
        }

        showButton.setOnClickListener { dragHandle?.show() }

        @Suppress("DEPRECATION")
        // Non-deprecated method isn't available on current Android version
        renderer.options =
            state?.getParcelable("renderOptions") ?: RenderOptions()
        renderer.viewMatrix =
            state?.getFloatArray("viewMatrix") ?: renderer.viewMatrix

        renderer.options.let { opts ->
            bindSwitch(R.id.switch_shade, opts::shading::set, opts.shading)
            bindSwitch(R.id.switch_grid, opts::showGrid::set, opts.showGrid)

            opts.pendingSkin = BitmapFactory.decodeStream(
                assets.open("base.png")
            )

            opts.pendingBackground =
                Color.pack(resources.getColor(R.color.background, theme))

            val skins = Skins(opts)

            thread {
                val profile = profileByName("Zatrit156")
                val result = skins.loadSkin(profile, defaultSources.asIterable())
                result.first { it != null }?.let(skins::loadTextures)
            }
        }

        // Attaches surface at left of the menu if using landscape mode
        if (display?.rotation!! % 2 == 1) {
            surface.applyLayout<RelativeLayout.LayoutParams> {
                rules[LEFT_OF] = menu.id
            }
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        state.putFloatArray("viewMatrix", renderer.viewMatrix)
        state.putParcelable("renderOptions", renderer.options)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSurfaceTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            ACTION_DOWN -> velocityTracker =
                VelocityTracker.obtain().apply { addMovement(event) }

            ACTION_UP, ACTION_CANCEL -> velocityTracker.recycle()

            ACTION_MOVE -> with(velocityTracker) {
                addMovement(event)
                computeCurrentVelocity(
                    resources.displayMetrics.density.toInt()
                )

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

package net.zatrit.skinbread

import android.app.*
import android.content.Intent
import android.graphics.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.rotateM
import android.os.Bundle
import android.view.*
import android.view.MotionEvent.*
import android.view.View.GONE
import android.widget.*
import android.widget.RelativeLayout.LEFT_OF
import net.zatrit.skinbread.gl.*
import net.zatrit.skinbread.skins.loadTexturesAsync
import net.zatrit.skins.lib.texture.BitmapTexture
import java.util.concurrent.CompletableFuture


class MainActivity : Activity() {
    private lateinit var velocityTracker: VelocityTracker
    private var renderer = Renderer()
    private var loading: CompletableFuture<Unit>? = null

    private inline fun bindSwitch(
        id: Int, value: Boolean, crossinline onSet: (Boolean) -> Unit) {
        findViewById<Switch>(id).apply {
            setOnCheckedChangeListener { _, state -> onSet(state) }
            isChecked = value
        }
    }

    private inline fun bindButton(id: Int, crossinline onClick: (View) -> Unit) =
        findViewById<Button>(id).setOnClickListener { onClick(it) }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)

        val surface = findViewById<GLSurfaceView>(R.id.gl_surface).apply {
            setEGLContextClientVersion(3)
            setOnTouchListener(::onSurfaceTouch)
            setRenderer(renderer)
        }

        val menu = findViewById<LinearLayout>(R.id.menu)
        val buttons = findViewById<LinearLayout>(R.id.toolbar)
        val dragHandle = findViewById<DragHandle?>(R.id.drag_handle)?.apply {
            target = menu
            showInstead = buttons
        }

        bindButton(R.id.btn_render_options) {
            dragHandle?.show()
        }

        bindButton(R.id.btn_switch) {
            startActivity(
                Intent(this, PickSourceActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        val alreadyLoadingToast = Toast.makeText(
            this, R.string.already_loading, Toast.LENGTH_SHORT
        )
        val refreshDialog = refreshDialog(this) { name, uuid ->
            loading = loadTexturesAsync(name, uuid, renderer.options)
        }

        bindButton(R.id.btn_fetch) {
            if (loading?.isDone == false) {
                alreadyLoadingToast.show()
            } else {
                refreshDialog.show()
            }
        }

        @Suppress("DEPRECATION")
        // Non-deprecated method isn't available on current Android version
        renderer.options =
            state?.getParcelable("renderOptions") ?: RenderOptions()
        renderer.viewMatrix =
            state?.getFloatArray("viewMatrix") ?: renderer.viewMatrix

        renderer.options.run {
            bindSwitch(R.id.switch_shade, shading) { shading = it }
            bindSwitch(R.id.switch_grid, grid) { grid = it }
            bindSwitch(R.id.switch_elytra, elytra) { elytra = it }

            pendingDefaultTextures = Textures(
                skin = BitmapTexture(
                    BitmapFactory.decodeStream(assets.open("base.png"))
                )
            )
            // Reset textures to defaults
            pendingTextures = Textures()

            background =
                Color.pack(resources.getColor(R.color.background, theme))
        }

        // Attaches surface at left of the menu if using landscape mode
        if (display?.rotation!! % 2 == 1) {
            surface.applyLayout<RelativeLayout.LayoutParams> {
                rules[LEFT_OF] = menu.id
            }
            buttons.visibility = GONE
        } else {
            menu.visibility = GONE
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

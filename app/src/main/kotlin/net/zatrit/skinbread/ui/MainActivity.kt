package net.zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.*
import android.widget.RelativeLayout.LEFT_OF
import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.*
import net.zatrit.skinbread.skins.*
import net.zatrit.skinbread.ui.touch.*

class MainActivity : SkinSetActivity() {
    private var dragHandler: MenuDragHandler? = null
    private var renderer = Renderer()

    private var textures = Textures()
        set(value) {
            field = value
            renderer.options.pendingTextures = value
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)

        window.exitTransition = activityTransition

        val surface = requireViewById<GLSurfaceView>(R.id.gl_surface).apply {
            setEGLContextClientVersion(3)
            val density = resources.displayMetrics.density.toInt()
            setOnTouchListener(ModelRotateHandler(renderer, density))
            setRenderer(renderer)
        }

        val menu = requireViewById<LinearLayout>(R.id.menu)
        val buttons = requireViewById<LinearLayout>(R.id.toolbar)

        val refreshDialog = profileDialog(this, preferences) { name, uuid ->
            renderer.options.pendingTextures = Textures()
            reloadTextures(name, uuid, defaultSources)
        }

        bindButton(R.id.btn_list) {
            val intent = Intent(this, ToggleSourcesActivity::class.java).putExtra(
                SKINSET, skinSet
            )

            startActivityForResult(
                intent, 0,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        requireViewById<Button>(R.id.btn_fetch).setOnClickListener(
            ShowWhenLoadedHandler(this, refreshDialog)
        )

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
                skin = BitmapFactory.decodeStream(assets.open("base.png"))
            )

            // Reset textures to defaults
            pendingTextures = Textures()

            background =
                Color.pack(resources.getColor(R.color.background, theme))
        }

        // Attaches surface at left of the menu if using landscape mode
        if (display?.rotation!! % 2 == 1) {
            requireViewById<View>(R.id.btn_render_options).visibility = GONE
            surface.applyLayout<RelativeLayout.LayoutParams> {
                rules[LEFT_OF] = menu.id
            }
        } else {
            menu.visibility = GONE

            dragHandler = MenuDragHandler(menu, buttons, resources)
            requireViewById<ImageView?>(R.id.drag_handle).setOnTouchListener(
                dragHandler
            )

            bindButton(R.id.btn_render_options) {
                dragHandler?.show()
            }
        }

        updateSkinSetFromPrefs()
        state?.let(::updateSkinSetFromBundle)
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        state.putFloatArray("viewMatrix", renderer.viewMatrix)
        state.putParcelable("renderOptions", renderer.options)
    }

    override fun onSkinSetLoad(skinSet: SkinSet) {
        this.textures = mergeTextures(skinSet.order.map {
            skinSet.textures[it].takeIf { _ -> skinSet.enabled[it] }
        }.filterNotNull())
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    // Non-deprecated method isn't available on current Android version
    override fun onBackPressed() {
        if (dragHandler?.isVisible == true) {
            dragHandler?.hide()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == I_HAVE_SKINSET) {
            data?.extras?.let { updateSkinSetFromBundle(it) }
        }
    }
}

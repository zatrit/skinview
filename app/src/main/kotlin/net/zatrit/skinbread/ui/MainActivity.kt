package net.zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View.GONE
import android.widget.*
import android.widget.RelativeLayout.LEFT_OF
import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.*
import net.zatrit.skinbread.skins.*
import net.zatrit.skinbread.ui.dialog.*
import net.zatrit.skinbread.ui.touch.*
import org.json.JSONObject

class MainActivity : TexturesActivity() {
    private lateinit var texturePicker: TexturePicker
    private var dragHandler: MenuDragHandler? = null
    private var renderer = Renderer()

    @Suppress("DEPRECATION")
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)

        val surface = requireViewById<GLSurfaceView>(R.id.gl_surface).apply {
            setEGLContextClientVersion(3)
            val density = resources.displayMetrics.density.toInt()
            setOnTouchListener(ModelRotateHandler(renderer, density))
            setRenderer(renderer)
        }

        val menu = requireViewById<LinearLayout>(R.id.menu)
        val buttons = requireViewById<LinearLayout>(R.id.toolbar)

        texturePicker = state?.getParcelable("texturePicker") ?: TexturePicker()

        bindButton(R.id.btn_list) {
            window.exitTransition = transitionWithFetchButton
            val intent =
              Intent(this, ToggleSourcesActivity::class.java).putExtra(
                ARRANGING, arranging
              )

            startActivityForResult(
              intent, 0,
              ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        bindButton(R.id.btn_info) {
            window.exitTransition = transition
            startActivity(
              Intent(this, LicenseActivity::class.java),
              ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        val fetchDialog = profileDialog(this) { name, uuid ->
            renderer.config.clearTextures = true
            texturePicker.reset()
            // Updates order according to LOCAL to avoid it overlapping with new textures
            textures[LOCAL]?.let {
                texturePicker.update(it, arranging.order.indexOf(LOCAL))
            }
            reloadTextures(name, uuid, defaultSources)
        }

        bindDialogButtons(fetchDialog)

        skinLayer.legacyMask =
          BitmapFactory.decodeStream(assets.open("legacyMask.png"))

        // Non-deprecated method isn't available on current Android version
        renderer.config = state?.getParcelable("renderOptions") ?: RenderConfig()
        renderer.viewMatrix =
          state?.getFloatArray("viewMatrix") ?: renderer.viewMatrix

        renderer.config.run {
            bindSwitch(R.id.switch_shade, shading) { shading = it }
            bindSwitch(R.id.switch_grid, grid) { grid = it }
            bindSwitch(R.id.switch_elytra, elytra) { elytra = it }

            pendingDefaultTextures = Textures(
              skin = BitmapFactory.decodeStream(assets.open("base.png"))
            )

            background =
              Color.pack(resources.getColor(R.color.background, theme))
        }

        val renderOptionsButton =
          requireViewById<Button>(R.id.btn_render_options)

        if (display?.rotation!! % 2 == 1) {
            // Attaches surface at left of the menu if using landscape mode
            surface.applyLayout<RelativeLayout.LayoutParams> {
                rules[LEFT_OF] = menu.id
            }

            /* Hides the display settings button, as the display
            settings are always shown on the screen */
            renderOptionsButton.visibility = GONE

            /* Display the toolbar on top of the menu so that it is visible.
            If you just place the menu on top of the toolbar, it will break the
            animation of the menu appearing on portrait orientation of the screen */
            buttons.elevation = 2f
        } else {
            menu.visibility = GONE

            dragHandler = MenuDragHandler(menu, buttons, resources)
            requireViewById<ImageView?>(R.id.drag_handle).setOnTouchListener(
              dragHandler
            )

            bindButton(renderOptionsButton) {
                dragHandler?.show()
            }
        }

        loadPreferences()
        state?.let(::updateArrangingFromBundle)
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        state.putFloatArray("viewMatrix", renderer.viewMatrix)
        state.putParcelable("renderOptions", renderer.config)
        state.putParcelable("texturePicker", texturePicker)
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

        if (resultCode == I_HAVE_ARRANGING) {
            data?.extras?.let { updateArrangingFromBundle(it) }
        }
    }

    override fun onTexturesAdded(
      textures: Textures, index: Int, order: Int, name: SourceName) {
        if (arranging.enabled[index]) {
            val update = texturePicker.update(textures, order)
            this.renderer.config.pendingTextures.add(update)
        }
    }

    override fun setTextures(newTextures: Array<Textures?>) {
        this.renderer.config.clearTextures = true
        this.renderer.config.pendingTextures.add(
          mergeTextures(arranging.order.map {
              newTextures[it].takeIf { _ -> arranging.enabled[it] }
          }.filterNotNull())
        )
    }

    private fun loadPreferences() {
        loadTexturesAsync(this) {
            textures = it
            texturesHolder?.setTextures(it)
        }

        preferences.getString(ARRANGING, null)?.let {
            try {
                arranging.loadJson(JSONObject(it))
            } catch (ex: Exception) {
                ex.printDebug()
            }
        }
    }
}

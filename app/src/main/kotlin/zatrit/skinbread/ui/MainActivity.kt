package zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View.GONE
import android.widget.*
import android.widget.RelativeLayout.LEFT_OF
import org.json.JSONObject
import zatrit.skinbread.*
import zatrit.skinbread.gl.*
import zatrit.skinbread.skins.*
import zatrit.skinbread.ui.dialog.*
import zatrit.skinbread.ui.touch.*

/**
 * The main [Activity] of the app, containing buttons to jump to
 * others and a 3D renderer of player skins. */
class MainActivity : TexturesActivity() {
    /** [TexturePicker] to pick textures when adding them. */
    private var texturePicker = TexturePicker()

    /** The menu resize handler. */
    private var dragHandler: MenuDragHandler? = null

    /** A renderer of the player's 3D model that is interacted with via [RenderConfig]. */
    private lateinit var renderer: Renderer

    @Suppress("DEPRECATION")
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)

        // Creates a renderer
        val defaultTextures = Textures(
          skin = BitmapFactory.decodeStream(assets.open("base.png"))
        )
        renderer = Renderer(defaultTextures)

        // Surface for rendering a 3D model with renderer
        val surface = requireViewById<GLSurfaceView>(R.id.gl_surface).apply {
            setEGLContextClientVersion(3)
            val density = resources.displayMetrics.density.toInt()
            setOnTouchListener(ModelRotateHandler(renderer, density))
            setRenderer(renderer)
        }

        val menu = requireViewById<LinearLayout>(R.id.menu)
        val buttons = requireViewById<LinearLayout>(R.id.toolbar)

        bindClick(R.id.btn_list) {
            // Changes the transition animation to not hide actionButtons
            window.exitTransition = transitionWithActionButtons
            val intent =
              Intent(this, ToggleSourcesActivity::class.java).putExtra(
                ARRANGING, arranging
              )

            startActivityForResult(
              intent, 0,
              ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        bindClick(R.id.btn_info) {
            // Changes the transition animation to hide all UI elements
            window.exitTransition = transition
            startActivity(
              Intent(this, LicenseActivity::class.java),
              ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        val fetchDialog = profileDialog(this) { name, uuid ->
            // Resets the state to then load new textures
            renderer.config.clearTextures = true
            texturePicker.reset()

            // Updates order according to LOCAL to avoid it overlapping with new textures
            textures[LOCAL].takeIf { arranging.enabled[LOCAL] }?.let {
                texturePicker.update(it, arranging.order.indexOf(LOCAL))
            }
            reloadTextures(name, uuid, defaultSources)
        }

        // Binds action_buttons
        bindDialogButtons(fetchDialog)

        // Loads a mask to remove black background from textures from application assets
        skinLayer.legacyMask =
          BitmapFactory.decodeStream(assets.open("legacyMask.png"))

        // Loads the saved state
        // Non-deprecated method isn't available on current Android version
        texturePicker = state?.getParcelable("texturePicker") ?: texturePicker
        renderer.config = state?.getParcelable("renderOptions") ?: RenderConfig()
        renderer.viewMatrix =
          state?.getFloatArray("viewMatrix") ?: renderer.viewMatrix

        // Loads the initial values for renderer.config and binds the settings buttons to them.
        renderer.config.run {
            bindSwitch(R.id.switch_shade, shading) { shading = it }
            bindSwitch(R.id.switch_grid, grid) { grid = it }
            bindSwitch(R.id.switch_elytra, elytra) { elytra = it }

            /* Gets the background color for the given theme and uses it
            to render the background of the Renderer */
            background =
              Color.pack(resources.getColor(R.color.background, theme))
        }

        val renderOptionsButton =
          requireViewById<Button>(R.id.btn_render_options)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
            // Hides the menu, as it should be opened manually by the user
            menu.visibility = GONE

            // Creates a MenuDragHandler and binds its opening to renderOptionsButton
            dragHandler = MenuDragHandler(menu, buttons, resources)
            requireViewById<ImageView?>(R.id.drag_handle).setOnTouchListener(
              dragHandler
            )

            bindClick(renderOptionsButton) {
                dragHandler?.show()
            }
        }

        // Loads preferences and arranging from state
        loadState()
        state?.let(::updateArrangingFromBundle)
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        // Stores states to be transmitted between activity restarts
        state.putFloatArray("viewMatrix", renderer.viewMatrix)
        state.putParcelable("renderOptions", renderer.config)
        state.putParcelable("texturePicker", texturePicker)
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    // Non-deprecated method isn't available on current Android version
    override fun onBackPressed() {
        // In portrait orientation, if the menu is visible, hides it
        if (dragHandler?.isVisible == true) {
            dragHandler?.hide()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(
      requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If some running activity returns arranging, uses it
        if (resultCode == I_HAVE_ARRANGING) {
            data?.extras?.let { updateArrangingFromBundle(it) }
        }
    }

    override fun addTextures(
      textures: Textures, index: Int, order: Int, name: SourceName) {
        if (arranging.enabled[index]) {
            val update = texturePicker.update(textures, order)
            this.renderer.config.pendingTextures.add(update)
        }
    }

    override fun setTextures(textures: Array<Textures?>) {
        // Resets the state and sets the textures
        this.renderer.config.clearTextures = true
        this.renderer.config.pendingTextures.add(
          mergeTextures(arranging.order.map {
              textures[it].takeIf { _ -> arranging.enabled[it] }
          }.filterNotNull())
        )
    }

    /** Loads saved data such as [textures] and [arranging]. */
    private fun loadState() {
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

        allowedSources = preferences.getInt(ENABLED_SOURCES, -1)
          .toBooleanArray(defaultSources.size - 1)
    }
}

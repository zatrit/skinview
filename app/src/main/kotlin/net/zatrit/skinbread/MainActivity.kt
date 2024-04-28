package net.zatrit.skinbread

import android.app.*
import android.content.*
import android.graphics.*
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.*
import android.widget.RelativeLayout.LEFT_OF
import net.zatrit.skinbread.gl.*
import net.zatrit.skinbread.skins.loadTexturesAsync
import net.zatrit.skins.lib.texture.BitmapTexture
import java.util.concurrent.CompletableFuture

class MainActivity : Activity() {
    private lateinit var preferences: SharedPreferences
    private var dragHandle: DragHandle? = null

    private var renderer = Renderer()
    private var loading: CompletableFuture<Unit>? = null

    private inline fun bindSwitch(
        id: Int, value: Boolean, crossinline onSet: (Boolean) -> Unit) {
        requireViewById<Switch>(id).apply {
            setOnCheckedChangeListener { _, state -> onSet(state) }
            isChecked = value
        }
    }

    private inline fun bindButton(id: Int, crossinline onClick: (View) -> Unit) =
        requireViewById<Button>(id).setOnClickListener { onClick(it) }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)

        val surface = requireViewById<GLSurfaceView>(R.id.gl_surface).apply {
            setEGLContextClientVersion(3)
            setOnTouchListener(ModelRotateHandler(renderer, resources))
            setRenderer(renderer)
        }

        val menu = requireViewById<LinearLayout>(R.id.menu)
        val buttons = requireViewById<LinearLayout>(R.id.toolbar)
        dragHandle = findViewById<DragHandle?>(R.id.drag_handle)?.apply {
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

        preferences = getPreferences(MODE_PRIVATE)
        val alreadyLoadingToast = Toast.makeText(
            this, R.string.already_loading, Toast.LENGTH_SHORT
        )
        val refreshDialog = profileDialog(this) { name, uuid, remember ->
            if (remember) {
                val edit = preferences.edit()
                edit.putString("profileName", name)
                edit.putString("profileId", uuid)
                edit.apply()
            }

            loading = loadTexturesAsync(name, uuid, renderer.options)
        }

        // Set saved field values if present
        refreshDialog.setOnShowListener { dialog ->
            if (dialog !is AlertDialog) return@setOnShowListener

            preferences.getString("profileName", null)?.also {
                dialog.requireViewById<EditText>(R.id.edittext_name).setText(it)
            }

            preferences.getString("profileId", null)?.also {
                dialog.requireViewById<EditText>(R.id.edittext_uuid).setText(it)
            }
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
            buttons.applyLayout<RelativeLayout.LayoutParams> {
                rules[LEFT_OF] = menu.id
            }
        } else {
            menu.visibility = GONE
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        state.putFloatArray("viewMatrix", renderer.viewMatrix)
        state.putParcelable("renderOptions", renderer.options)
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    // Non-deprecated method isn't available on current Android version
    override fun onBackPressed() {
        if (dragHandle?.isVisible == true) {
            dragHandle?.hide()
        } else {
            super.onBackPressed()
        }
    }
}

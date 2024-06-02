package zatrit.skinbread.ui

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import zatrit.skinbread.*
import zatrit.skinbread.gl.model.ModelType
import zatrit.skinbread.skins.*
import zatrit.skins.lib.api.*
import zatrit.skins.lib.data.Metadata
import java.util.concurrent.CompletableFuture.runAsync

/** Field name in [SharedPreferences] and [Bundle] for [Arranging]. */
const val ARRANGING = "arranging"

/** Result code for a result containing [Arranging]. */
const val I_HAVE_ARRANGING = 156

abstract class TexturesActivity : Activity(), TextureHolder {
    lateinit var preferences: SharedPreferences

    /** The [Arranging] used to display textures. */
    var arranging = Arranging(defaultSources.size)

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()

        validateArranging()
        // Sets the global handlers
        texturesHolder = this

        setTextures(textures)
    }

    override fun onPause() {
        saveArranging()
        if (texturesHolder == this) texturesHolder = null

        super.onPause()
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putParcelable(ARRANGING, arranging)
    }

    override fun onActivityResult(
      requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode shr 3 == 1) try {
            val uri = data?.data ?: return

            // Texture that opens [InputStream] via [contentResolver]
            val texture = object : Texture {
                override fun getMetadata(): Metadata? = null
                override fun openStream() = contentResolver.openInputStream(uri)
            }

            val set = textures[LOCAL] ?: Textures()

            // Specifies the texture depending on the type stored in the last two bits of requestCode.
            when (requestCode and 3) {
                1 -> set.cape = capeLayer.tryApply(texture).bitmap
                2 -> set.ears = texture.bitmap
                else -> {
                    // Gets the model from the third bit of resultCode
                    set.model = if (requestCode shr 2 and 1 == 1) {
                        ModelType.SLIM
                    } else ModelType.DEFAULT

                    set.skin = skinLayer.tryApply(texture).bitmap
                }
            }

            // Updates textures and saves them.
            textures[LOCAL] = set
            addTextures(
              set, LOCAL, arranging.order.indexOf(LOCAL),
              defaultSources[LOCAL].name
            )

            runAsync { saveTextures(this, LOCAL, set) }
        } catch (ex: Throwable) {
            globalToast(R.string.open_failed)
            ex.printDebug()
        }
    }

    override fun showToast(resId: Int) = runOnUiThread {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    /** Shows the toast for the global [texturesHolder]. */
    private fun globalToast(message: Int) = texturesHolder?.showToast(message)

    /**
     * Reloads textures, creating a new [Profile] from the given [name] and
     * [uuid] from the given [sources]. */
    fun reloadTextures(name: String, uuid: String, sources: Array<SkinSource>) {
        textures.fill(null, 1)
        setTextures(textures)

        loading = runAsync {
            globalToast(R.string.loading_profile)
            val profile = try {
                refillProfile(uuid, name)
            } catch (ex: Exception) {
                ex.printDebug()
                globalToast(R.string.profile_failed)

                // Creates a new profile with the default UUID and the specified name
                SimpleProfile(nullUuid, name)
            }

            // Removes all textures except local
            for (i in VANILLA..<sources.size) {
                deleteTextures(this, i)
            }

            globalToast(R.string.loading_textures)
            sources.mapIndexed { i, source ->
                fetchTexturesAsync(profile, source) {
                    if (it == null || it.isEmpty) return@fetchTexturesAsync

                    val data = Textures().apply { or(it, skinLayer, capeLayer) }
                    textures[i] = data
                    val order = arranging.order.indexOf(i)
                    texturesHolder?.addTextures(data, i, order, source.name)

                    saveTextures(this, i, data)
                }
            }.forEach { it.join() }

            Log.d(TAG, "Loaded textures")
        }
    }

    /** Saves [arranging] to the [preferences] field called [ARRANGING]. */
    private fun saveArranging() {
        Log.d(TAG, "Saving textures arrangement")
        preferences.edit { it.putString(ARRANGING, arranging.saveJson()) }
    }

    /** Updates [arranging] with data from [Bundle] stored in the [ARRANGING] field. */
    fun updateArrangingFromBundle(bundle: Bundle) {
        @Suppress("DEPRECATION")
        // Non-deprecated method isn't available on current Android version
        arranging = bundle.getParcelable(ARRANGING) ?: arranging
    }

    /**
     * Checks the size of the fields in [arranging] against the size of
     * [defaultSources]. If they do not match, recreates [arranging] with
     * the correct size and saves it. */
    private fun validateArranging() {
        val size = defaultSources.size // Needed size
        if (arranging.enabled.size != size || arranging.order.size != size) {
            arranging = Arranging(size)

            globalToast(R.string.invalid_save)
            saveArranging()
        }
    }
}

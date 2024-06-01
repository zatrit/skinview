package zatrit.skinbread.ui

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import zatrit.skinbread.*
import zatrit.skinbread.gl.model.ModelType
import zatrit.skinbread.skins.*
import zatrit.skins.lib.api.Texture
import zatrit.skins.lib.data.Metadata
import java.util.concurrent.CompletableFuture.runAsync

const val ARRANGING = "arranging"
const val I_HAVE_ARRANGING = 156

abstract class TexturesActivity : Activity(), TextureHolder {
    lateinit var preferences: SharedPreferences
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

            val texture = object : Texture {
                override fun getMetadata(): Metadata? = null
                override fun openStream() = contentResolver.openInputStream(uri)
            }

            val set = textures[LOCAL] ?: Textures()

            when (requestCode and 3) {
                1 -> set.cape = capeLayer.tryApply(texture).bitmap
                2 -> set.ears = texture.bitmap
                else -> {
                    set.model = if (requestCode shr 2 and 1 == 1) {
                        ModelType.SLIM
                    } else ModelType.DEFAULT

                    set.skin = skinLayer.tryApply(texture).bitmap
                }
            }

            onTexturesAdded(
              set, LOCAL, arranging.order.indexOf(LOCAL),
              defaultSources[LOCAL].name
            )

            textures[LOCAL] = set
            runAsync { saveTextures(this, LOCAL, set) }
        } catch (ex: Exception) {
            Toast.makeText(this, R.string.open_failed, Toast.LENGTH_SHORT).show()
            ex.printDebug()
        }
    }

    override fun handleToast(resId: Int) = runOnUiThread {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: Int) = texturesHolder?.handleToast(message)

    fun reloadTextures(name: String, uuid: String, sources: Array<SkinSource>) {
        textures.fill(null, 1)
        setTextures(textures)

        loading = runAsync {
            showToast(R.string.loading_profile)
            val profile = try {
                refillProfile(uuid, name)
            } catch (ex: Exception) {
                ex.printDebug()
                showToast(R.string.profile_failed)

                SimpleProfile(nullUuid, name)
            }

            for (i in VANILLA..<sources.size) {
                clearTextures(this, i)
            }

            showToast(R.string.loading_textures)
            sources.mapIndexed { i, source ->
                loadTexturesAsync(profile, source) {
                    if (it == null || it.isEmpty) return@loadTexturesAsync

                    val data = Textures().apply { or(it, skinLayer, capeLayer) }
                    textures[i] = data
                    val order = arranging.order.indexOf(i)
                    texturesHolder?.onTexturesAdded(data, i, order, source.name)

                    saveTextures(this, i, data)
                }
            }.forEach { it.join() }

            Log.d(TAG, "Loaded textures")
        }
    }

    private fun saveArranging() {
        Log.d(TAG, "Saving textures arrangement")
        preferences.edit { it.putString(ARRANGING, arranging.saveJson()) }
    }

    fun updateArrangingFromBundle(bundle: Bundle) {
        @Suppress("DEPRECATION")
        // Non-deprecated method isn't available on current Android version
        arranging = bundle.getParcelable(ARRANGING) ?: arranging
    }

    private fun validateArranging() {
        val size = defaultSources.size
        if (arranging.enabled.size != size || arranging.order.size != size) {
            arranging = Arranging(size)

            Toast.makeText(this, R.string.invalid_save, Toast.LENGTH_SHORT)
              .show()

            saveArranging()
        }
    }
}

package zatrit.skinbread.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import zatrit.skinbread.LOCAL
import zatrit.skinbread.PREFS_NAME
import zatrit.skinbread.R
import zatrit.skinbread.TAG
import zatrit.skinbread.Textures
import zatrit.skinbread.VANILLA
import zatrit.skinbread.edit
import zatrit.skinbread.gl.model.ModelType
import zatrit.skinbread.loading
import zatrit.skinbread.nullUuid
import zatrit.skinbread.printDebug
import zatrit.skinbread.skins.Arranging
import zatrit.skinbread.skins.SimpleProfile
import zatrit.skinbread.skins.SkinSource
import zatrit.skinbread.skins.TextureHolder
import zatrit.skinbread.skins.capeLayer
import zatrit.skinbread.skins.clearTextures
import zatrit.skinbread.skins.defaultSources
import zatrit.skinbread.skins.loadTexturesAsync
import zatrit.skinbread.skins.refillProfile
import zatrit.skinbread.skins.saveTextures
import zatrit.skinbread.skins.skinLayer
import zatrit.skinbread.textures
import zatrit.skinbread.texturesHolder
import zatrit.skinbread.toastHandler
import zatrit.skinbread.tryApply
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
        toastHandler = {
            runOnUiThread {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        texturesHolder = this

        setTextures(textures)
    }

    override fun onPause() {
        saveArranging()

        if (toastHandler == this) toastHandler = null
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

    private fun showToast(message: Int) = toastHandler?.invoke(message)

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

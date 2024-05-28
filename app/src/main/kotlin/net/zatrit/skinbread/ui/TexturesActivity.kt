package net.zatrit.skinbread.ui

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import net.zatrit.skinbread.*
import net.zatrit.skinbread.gl.model.ModelType
import net.zatrit.skinbread.skins.*
import net.zatrit.skins.lib.texture.BytesTexture
import java.util.concurrent.CompletableFuture.supplyAsync

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

            val texture = contentResolver.openInputStream(uri)!!.use {
                BytesTexture(it.readBytes(), null)
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
            clearTexturesAsync(this, intArrayOf(0))
            saveTexturesAsync(this, arrayOf(set))
        } catch (ex: Exception) {
            Toast.makeText(this, R.string.open_failed, Toast.LENGTH_SHORT).show()
            ex.printDebug()
        }
    }

    private fun showToast(message: Int) = toastHandler?.invoke(message)

    fun reloadTextures(name: String, uuid: String, sources: Array<SkinSource>) {
        textures.fill(null, 1)
        setTextures(textures)

        loading = supplyAsync {
            showToast(R.string.loading_profile)
            try {
                refillProfile(uuid, name)
            } catch (ex: Exception) {
                ex.printDebug()
                showToast(R.string.profile_failed)

                SimpleProfile(nullUuid, name)
            }
        }.thenApplyAsync { profile ->
            showToast(R.string.loading_textures)

            sources.mapIndexed { i, source ->
                val future = loadTexturesAsync(profile, source)
                future.thenApply {
                    if (it == null || it.isEmpty) return@thenApply

                    val data = Textures().apply {
                        or(it, skinLayer, capeLayer)
                    }
                    textures[i] = data
                    val order = arranging.order.indexOf(i)
                    texturesHolder?.onTexturesAdded(data, i, order, source.name)
                }
            }.forEach { it.join() }

            clearTexturesAsync(this, textures.indicesArray)
            saveTexturesAsync(this, textures)
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
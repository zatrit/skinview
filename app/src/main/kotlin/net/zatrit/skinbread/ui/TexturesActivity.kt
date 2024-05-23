package net.zatrit.skinbread.ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.*
import java.util.concurrent.CompletableFuture.supplyAsync

const val ARRANGING = "arranging"
const val I_HAVE_ARRANGING = 156

abstract class TexturesActivity : Activity(), TexturesListener {
    lateinit var preferences: SharedPreferences

    var arranging = Arranging(defaultSources.size)

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()

        // Sets the global handlers
        toastHandler = {
            runOnUiThread {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        texturesHandler = this

        setTextures(textures)
    }

    override fun onPause() {
        if (toastHandler == this) toastHandler = null
        if (texturesHandler == this) texturesHandler = null

        Log.d(TAG, "Saving textures arrangement")
        val edit = preferences.edit()
        edit.putString(ARRANGING, arranging.saveJson())
        edit.apply()

        super.onPause()
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putParcelable(ARRANGING, arranging)
    }

    private fun showToast(message: Int) = toastHandler?.invoke(message)

    fun reloadTextures(name: String, uuid: String, sources: Array<SkinSource>) {
        textures.fill(null)
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
                val future = loadTextures(profile, source)
                future.thenApply {
                    if (it == null || it.isEmpty) return@thenApply

                    val data = Textures().apply {
                        fillWith(it, skinLayer, capeLayer)
                    }
                    textures[i] = data
                    val order = arranging.order.indexOf(i)
                    onTexturesAdded(data, i, order, source)
                }
            }.forEach { it.join() }
        }.whenComplete { _, _ -> onTexturesLoaded() }
    }

    open fun onTexturesLoaded() {
        val edit = preferences.edit()
        saveTextures(edit, textures)
        edit.apply()
    }

    fun updateArrangingFromBundle(bundle: Bundle) {
        @Suppress("DEPRECATION")
        // Non-deprecated method isn't available on current Android version
        arranging = bundle.getParcelable(ARRANGING) ?: arranging
    }
}
package net.zatrit.skinbread.ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.*
import net.zatrit.skins.lib.PlayerTextures
import net.zatrit.skins.lib.api.Profile
import org.json.JSONObject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync

const val SKINSET = "skinSet"
const val I_HAVE_SKINSET = 156

abstract class SkinSetActivity : Activity(), HasLoading {
    lateinit var preferences: SharedPreferences

    @Volatile
    var skinSet = SkinSet(defaultSources.size)
    override var loading: CompletableFuture<Unit>? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    private fun showToast(message: Int) = runOnUiThread {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun reloadTextures(name: String, uuid: String, sources: Array<SkinSource>) {
        skinSet.textures.fill(null)
        supplyAsync {
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
                fetch(profile, sources) { i, texture ->
                    skinSet.textures[i] = Textures().apply {
                        fillWith(texture, skinLayer, capeLayer)
                    }
                    runOnUiThread {
                        onSkinSetLoad(skinSet)
                    }
                }.join()
            }
        }.printErrorOnFail()
    }

    private inline fun fetch(
        profile: Profile, sources: Array<SkinSource>,
        crossinline callback: (Int, PlayerTextures) -> Unit): CompletableFuture<Unit> {
        val futures = loadTexturesAll(profile, sources).mapIndexed { i, it ->
            it.thenApply {
                if (it == null || it.isEmpty) {
                    return@thenApply
                }

                callback(i, it)
            }
        }.toTypedArray()

        return CompletableFuture.allOf(*futures).thenApply { }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putParcelable(SKINSET, skinSet)
    }

    private fun saveSkinSet() {
        Log.v(TAG, "Saving skins data")
        val edit = preferences.edit()
        edit.putString(SKINSET, skinSet.saveJson())
        edit.apply()
    }

    abstract fun onSkinSetLoad(skinSet: SkinSet)

    override fun finish() {
        saveSkinSet()
        super.finish()
    }

    fun updateSkinSetFromPrefs() {
        preferences.getString(SKINSET, null)?.let {
            try {
                skinSet.loadJson(JSONObject(it))
                onSkinSetLoad(skinSet)
            } catch (ex: Exception) {
                ex.printDebug()
            }
        }
    }

    fun updateSkinSetFromBundle(bundle: Bundle) {
        @Suppress("DEPRECATION")
        // Non-deprecated method isn't available on current Android version
        skinSet = bundle.getParcelable<SkinSet>(SKINSET)?.also {
            onSkinSetLoad(it)
        } ?: skinSet
    }
}
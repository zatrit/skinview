package net.zatrit.skinbread.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.*

class PickSourceActivity : SkinSetActivity() {
    private lateinit var sourcesList: AbsListView
    private lateinit var adapter: SkinListAdapter

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        this.setContentView(R.layout.acitivty_pick_source)

        window.enterTransition = activityTransition

        sourcesList = requireViewById(R.id.list_sources)
        adapter = SkinListAdapter(this, skinSet).apply {
            setNotifyOnChange(false)
        }

        val dialog = profileDialog(this, preferences) { name, uuid ->
            adapter.clear()
            reloadTextures(name, uuid, defaultSources)
        }

        requireViewById<Button>(R.id.btn_fetch).setOnClickListener(
            ShowWhenLoadedHandler(this, dialog)
        )

        sourcesList.adapter = adapter

        updateSkinSetFromPrefs()
        state?.let(::updateSkinSetFromBundle)
        intent.extras?.let(::updateSkinSetFromBundle)
    }

    override fun onSkinSetLoad(skinSet: SkinSet) {
        adapter.skinSet = skinSet
        adapter.clear()

        skinSet.order.zip(defaultSources).forEach { pair ->
            val (i, source) = pair
            val textures = skinSet.textures[i] ?: return@forEach

            if (textures.isEmpty) {
                return@forEach
            }

            val entry = NamedEntry(
                index = i,
                name = source.name,
                textures = textures,
                enabled = skinSet.enabled[i],
            )

            adapter.add(entry)
        }

        adapter.notifyDataSetChanged()
    }

    override fun finish() {
        val intent = Intent().putExtra(SKINSET, skinSet)
        setResult(I_HAVE_SKINSET, intent)

        super.finish()
    }
}

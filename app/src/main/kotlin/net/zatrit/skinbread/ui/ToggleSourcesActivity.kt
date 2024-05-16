package net.zatrit.skinbread.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View.*
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.*

class ToggleSourcesActivity : SkinSetActivity() {
    private lateinit var sourcesList: AbsListView
    private lateinit var adapter: SkinListAdapter
    private lateinit var noSkins: TextView

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        this.setContentView(R.layout.acitivty_toggle_sources)

        window.enterTransition = activityTransition
        window.exitTransition = activityTransition

        sourcesList = requireViewById(R.id.list_sources)
        adapter = SkinListAdapter(this, skinSet).apply {
            setNotifyOnChange(false)
        }
        noSkins = requireViewById(R.id.text_no_skins)

        val dialog = profileDialog(this, preferences) { name, uuid ->
            adapter.clear()
            reloadTextures(name, uuid, defaultSources)
        }

        requireViewById<Button>(R.id.btn_fetch).setOnClickListener(
            ShowWhenLoadedHandler(this, dialog)
        )

        bindButton(R.id.btn_rearrange) {
            val intent = Intent(this, RearrangeActivity::class.java)
            intent.putExtra(ORDER, skinSet.order)

            startActivityForResult(
                intent, 0,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        sourcesList.adapter = adapter

        updateSkinSetFromPrefs()
        state?.let(::updateSkinSetFromBundle)
        intent.extras?.let(::updateSkinSetFromBundle)
    }

    override fun onSkinSetLoad(skinSet: SkinSet) {
        adapter.skinSet = skinSet
        adapter.clear()

        skinSet.textures.mapIndexed { i, textures ->
            if (textures == null || textures.isEmpty) {
                return@mapIndexed
            }

            val source = defaultSources[i]
            val entry = NamedEntry(
                index = i,
                name = source.name,
                textures = textures,
                enabled = skinSet.enabled[i],
            )

            adapter.add(entry)
        }

        adapter.sort(Comparator.comparingInt {
            skinSet.order.indexOf(it.index)
        })

        if (adapter.isEmpty) {
            sourcesList.visibility = INVISIBLE
            noSkins.visibility = VISIBLE
        } else {
            sourcesList.visibility = VISIBLE
            noSkins.visibility = INVISIBLE
        }

        adapter.notifyDataSetChanged()
    }

    override fun finish() {
        val intent = Intent().putExtra(SKINSET, skinSet)
        setResult(I_HAVE_SKINSET, intent)

        super.finish()
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == I_HAVE_ORDER) {
            skinSet = SkinSet(
                skinSet.size, skinSet.enabled, data.getIntArrayExtra(ORDER)!!,
                skinSet.textures
            )
            onSkinSetLoad(skinSet)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}

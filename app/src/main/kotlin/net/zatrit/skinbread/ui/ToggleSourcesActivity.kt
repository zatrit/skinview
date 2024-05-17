package net.zatrit.skinbread.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.skins.*

class ToggleSourcesActivity : TexturesActivity() {
    private lateinit var sourcesList: AbsListView
    private lateinit var adapter: SkinListAdapter
    private lateinit var noSkins: TextView

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        this.setContentView(R.layout.acitivty_toggle_sources)

        window.enterTransition = transitionWithFetchButton
        window.exitTransition = transition

        sourcesList = requireViewById(R.id.list_sources)
        adapter = SkinListAdapter(this).apply {
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
            intent.putExtra(ORDER, textureProps.order)

            startActivityForResult(
                intent, 0,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        sourcesList.adapter = adapter

        intent.extras?.let(::updatePropsFromBundle)
        state?.let(::updatePropsFromBundle)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == I_HAVE_ORDER) {
            textureProps = TextureProps(
                textureProps.size, textureProps.enabled,
                data.getIntArrayExtra(ORDER)!!
            )
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        val intent = Intent().putExtra(TEXTURE_PROPS, textureProps)
        setResult(I_HAVE_PROPS, intent)

        super.finish()
    }

    override fun setTextures(newTextures: Array<Textures?>) {
        adapter.clear()

        newTextures.mapIndexed { i, textures ->
            if (textures == null || textures.isEmpty) {
                return@mapIndexed
            }

            val source = defaultSources[i]
            val entry = NamedEntry(
                index = i,
                name = source.name,
                textures = textures,
                enabled = textureProps.enabled[i],
            )

            adapter.add(entry)
        }

        sortAdapter()
        adapter.notifyDataSetChanged()

        updateListVisibility()
    }

    override fun onTexturesAdded(
        textures: Textures, index: Int, order: Int, source: SkinSource) {
        val entry = NamedEntry(
            index = index,
            name = source.name,
            textures = textures,
            enabled = textureProps.enabled[index],
        )

        adapter.add(entry)
        sortAdapter()
        adapter.notifyDataSetChanged()

        updateListVisibility()
    }

    private fun updateListVisibility() {
        if (adapter.isEmpty) {
            sourcesList.visibility = View.GONE
            noSkins.visibility = View.VISIBLE
        } else {
            sourcesList.visibility = View.VISIBLE
            noSkins.visibility = View.GONE
        }
    }

    private fun sortAdapter() = adapter.sort(Comparator.comparingInt {
        textureProps.order.indexOf(it.index)
    })
}
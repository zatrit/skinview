package net.zatrit.skinbread.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.TextView
import net.zatrit.skinbread.R
import net.zatrit.skinbread.Textures
import net.zatrit.skinbread.bindButton
import net.zatrit.skinbread.skins.SourceName
import net.zatrit.skinbread.skins.defaultSources
import net.zatrit.skinbread.transition
import net.zatrit.skinbread.transitionWithFetchButton
import net.zatrit.skinbread.ui.dialog.bindDialogButtons
import net.zatrit.skinbread.ui.dialog.clearDialog
import net.zatrit.skinbread.ui.dialog.profileDialog

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

        val fetchDialog = profileDialog(this) { name, uuid ->
            adapter.clear()
            reloadTextures(name, uuid, defaultSources)
        }

        bindDialogButtons(fetchDialog)

        bindButton(R.id.btn_rearrange) {
            val intent = Intent(this, RearrangeActivity::class.java)
            intent.putExtra(ORDER, arranging.order)

            startActivityForResult(
              intent, 0,
              ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        bindButton(R.id.btn_clear) { clearDialog(this).show() }

        sourcesList.adapter = adapter

        intent.extras?.run(::updateArrangingFromBundle)
        state?.run(::updateArrangingFromBundle)
    }

    override fun onActivityResult(
      requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == I_HAVE_ORDER) {
            arranging.order = data?.getIntArrayExtra(ORDER)!!
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        val intent = Intent().putExtra(ARRANGING, arranging)
        setResult(I_HAVE_ARRANGING, intent)

        super.finish()
    }

    override fun setTextures(newTextures: Array<Textures?>) = runOnUiThread {
        adapter.clear()

        newTextures.mapIndexed { i, textures ->
            if (textures == null || textures.isEmpty()) return@mapIndexed

            val source = defaultSources[i]
            val entry = NamedEntry(
              index = i,
              name = source.name,
              textures = textures,
              enabled = arranging.enabled[i],
            )

            adapter.add(entry)
        }

        sortAdapter()
        adapter.notifyDataSetChanged()

        updateListVisibility()
    }

    override fun onTexturesAdded(
      textures: Textures, index: Int, order: Int, name: SourceName) {
        val entry = NamedEntry(
          index = index,
          name = name,
          textures = textures,
          enabled = arranging.enabled[index],
        )

        runOnUiThread {
            adapter.add(entry)
            sortAdapter()
            adapter.notifyDataSetChanged()

            updateListVisibility()
        }
    }

    override fun prepareTextureForReuse(index: Int) {
        super.prepareTextureForReuse(index)

        for (i in 0..<adapter.count) {
            val item = adapter.getItem(i)
            if (item?.index == 0) {
                adapter.remove(item)
                break
            }
        }
    }

    private fun updateListVisibility() = if (adapter.isEmpty) {
        sourcesList.visibility = View.GONE
        noSkins.visibility = View.VISIBLE
    } else {
        sourcesList.visibility = View.VISIBLE
        noSkins.visibility = View.GONE
    }

    private fun sortAdapter() = adapter.sort(Comparator.comparingInt {
        arranging.order.indexOf(it.index)
    })
}

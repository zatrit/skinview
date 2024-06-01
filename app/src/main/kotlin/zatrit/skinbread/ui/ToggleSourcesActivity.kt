package zatrit.skinbread.ui

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import zatrit.skinbread.*
import zatrit.skinbread.skins.*
import zatrit.skinbread.ui.adapter.*
import zatrit.skinbread.ui.dialog.*
import zatrit.skins.lib.util.use2

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

        findViewById<Button>(R.id.btn_clear).setOnClickListener(
          ShowDialogHandler(clearDialog(this))
        )

        sourcesList.adapter = adapter

        intent.extras?.run(::updateArrangingFromBundle)
        state?.run(::updateArrangingFromBundle)
    }

    override fun onActivityResult(
      requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == I_HAVE_ORDER) {
            arranging.order = data?.getIntArrayExtra(ORDER)!!
        } else if (requestCode shr 7 == 1) {
            val uri = data?.data ?: return
            val type = requestCode and 3 // only 2 right bits
            val index =
                requestCode shr 2 and 15 // only 4 right bits of value shifted by 2 bits
            Log.d(TAG, "$type $index")

            val textures = textures[index] ?: return
            val texture = when (type) {
                1 -> textures.cape
                2 -> textures.ears
                else -> textures.skin
            }

            contentResolver.openOutputStream(uri)?.use2 {
                texture?.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } else super.onActivityResult(requestCode, resultCode, data)
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

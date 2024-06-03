package zatrit.skinbread.ui

import android.app.*
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

/** [Activity] containing a list of downloaded skins, allowing you to switch sources. */
class ToggleSourcesActivity : TexturesActivity() {
    /** List of skins, the main interface element in this activity. */
    private lateinit var sourcesList: AbsListView

    /** Adapter for [sourcesList]. */
    private lateinit var adapter: SkinListAdapter

    /** Text displayed in the absence of any loaded textures. */
    private lateinit var noSkins: TextView

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        this.setContentView(R.layout.acitivty_toggle_sources)

        // Sets the transition animations
        window.enterTransition = transitionWithActionButtons
        window.exitTransition = transition

        sourcesList = requireViewById(R.id.list_sources)
        adapter = SkinListAdapter(this).apply { setNotifyOnChange(false) }
        noSkins = requireViewById(R.id.text_no_skins)

        val fetchDialog = profileDialog(this) { name, uuid ->
            adapter.clear()
            reloadTextures(name, uuid, defaultSources)
        }

        // Binds action_buttons
        bindDialogButtons(fetchDialog)

        // Binds the opening of RearrangeActivity to btn_rearrange
        bindClick(R.id.btn_rearrange) {
            val intent = Intent(this, RearrangeActivity::class.java)
            intent.putExtra(ORDER, arranging.order)

            startActivityForResult(
              intent, 0,
              ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        // Opens the clear dialog when btn_clear is clicked
        requireViewById<Button>(R.id.btn_clear).setOnClickListener(
          ShowDialogHandler(clearDialog(this))
        )

        sourcesList.adapter = adapter

        // Loads the saved and passed state
        intent.extras?.run(::updateArrangingFromBundle)
        state?.run(::updateArrangingFromBundle)
    }

    override fun onActivityResult(
      requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == I_HAVE_ORDER) {
            arranging.order = data?.getIntArrayExtra(ORDER)!!
        } else if (requestCode shr 7 == 1) { // Result of the texture saving menu
            val uri = data?.data ?: return
            val type = requestCode and 3 // only 2 right bits
            val index =
              requestCode shr 2 and 15 // only 4 right bits of value shifted by 2 bits
            Log.d(TAG, "$type $index")

            // If there's no textures then return
            val textures = textures[index] ?: return
            val texture = when (type) {
                1 -> textures.cape
                2 -> textures.ears
                else -> textures.skin
            } ?: return

            contentResolver.openOutputStream(uri)?.use2 {
                texture.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        // Set an activity result
        val intent = Intent().putExtra(ARRANGING, arranging)
        setResult(I_HAVE_ARRANGING, intent)

        super.finish()
    }

    /** Populates [sourcesList] with the received [textures]. */
    // Working with sourcesList from other threads requires the use of runOnUiThread
    override fun setTextures(textures: Array<Textures?>) = runOnUiThread {
        adapter.clear()

        textures.mapIndexed { i, set ->
            if (set == null || set.isEmpty()) return@mapIndexed

            val source = defaultSources[i]
            val entry = NamedEntry(
              index = i,
              name = source.name,
              textures = set,
              enabled = arranging.enabled[i],
            )

            adapter.add(entry)
        }

        sortAdapter()
        adapter.notifyDataSetChanged()

        updateListVisibility()
    }

    /** Adds [textures] to [sourcesList] and sorts it so it's in the correct place. */
    override fun addTextures(
      textures: Textures, index: Int, order: Int, name: SourceName) {
        val entry = NamedEntry(
          index = index,
          name = name,
          textures = textures,
          enabled = arranging.enabled[index],
        )

        // Working with sourcesList from other threads requires the use of runOnUiThread
        runOnUiThread {
            adapter.add(entry)
            sortAdapter()
            adapter.notifyDataSetChanged()

            updateListVisibility()
        }
    }

    /** Updates the visibility of [sourcesList] and [noSkins], displaying [noSkins] if
     * there are no textures, otherwise [sourcesList]. */
    private fun updateListVisibility() = if (adapter.isEmpty) {
        sourcesList.visibility = View.GONE
        noSkins.visibility = View.VISIBLE
    } else {
        sourcesList.visibility = View.VISIBLE
        noSkins.visibility = View.GONE
    }

    /** Sorts the contents of [adapter] according to [Arranging.order]. */
    private fun sortAdapter() = adapter.sort(Comparator.comparingInt {
        arranging.order.indexOf(it.index)
    })
}

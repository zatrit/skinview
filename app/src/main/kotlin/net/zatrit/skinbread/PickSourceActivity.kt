package net.zatrit.skinbread

import android.app.Activity
import android.os.Bundle
import android.widget.ListView
import net.zatrit.skinbread.skins.*
import java.util.UUID

class PickSourceActivity : Activity() {
    private lateinit var sourcesList: ListView
    private lateinit var adapter: SkinListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.acitivty_pick_source)

        sourcesList = requireViewById(R.id.list_sources)
        adapter = SkinListAdapter(this)

        val profile = SimpleProfile(
            UUID.fromString("4476c041-4c72-42cd-a2aa-03696afcdfd8"), "Zatrit156"
        )

        for (source in defaultSources) {
            loadTextures(profile, source).whenComplete { it, error ->
                if (error != null) {
                    error.printWithSkinSource(source)
                    return@whenComplete
                }

                if (it == null || it.isEmpty) {
                    return@whenComplete
                }

                val textures = Textures()
                textures.fillWith(it, skinLayer, capeLayer)

                val entry = NamedEntry(
                    name = source.name,
                    textures = textures,
                )

                runOnUiThread { adapter.add(entry) }
            }
        }

        sourcesList.adapter = adapter
    }
}

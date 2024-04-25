package net.zatrit.skinbread

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.GridView


class PickSourceActivity : Activity() {
    private lateinit var sourcesGrid: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.acitivty_pick_source)

        sourcesGrid = findViewById(R.id.grid_sources)

        val adapter = SkinListAdapter(this)

        adapter.add(
            TexturesEntry(
                "Zatrit156",
                skin = BitmapFactory.decodeStream(assets.open("base.png"))
            )
        )

        sourcesGrid.adapter = adapter
    }
}

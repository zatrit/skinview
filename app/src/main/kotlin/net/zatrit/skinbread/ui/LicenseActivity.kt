package net.zatrit.skinbread.ui

import android.app.Activity
import android.os.Bundle
import android.widget.*
import net.zatrit.skinbread.*

class LicenseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_license)

        val list = findViewById<ListView>(R.id.list_libraries)
        val adapter =
          ArrayAdapter<String>(this, R.layout.entry_library, R.id.text_library)

        for (library in ossLibraries) {
            adapter.add(library.name)
        }

        list.setOnItemClickListener { _, _, position, _ ->

        }
    }
}
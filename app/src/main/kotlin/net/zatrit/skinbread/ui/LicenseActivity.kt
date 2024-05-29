package net.zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.*
import net.zatrit.skinbread.*
import net.zatrit.skinbread.ui.dialog.licenseDialog

class LicenseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_license)

        val list = findViewById<ListView>(R.id.list_libraries)
        val adapter = ArrayAdapter<Library>(this, R.layout.entry_library, R.id.text_library_name)

        for (library in ossLibraries) {
            adapter.add(library)
        }

        list.setOnItemClickListener { _, _, position, _ ->
            licenseDialog(this, adapter.getItem(position - list.headerViewsCount)!!).show()
        }

        list.adapter = adapter

        @SuppressLint("InflateParams") val header = layoutInflater.inflate(R.layout.header_licenses, null)
        list.addHeaderView(header)

        enableTitleBar()
    }
}
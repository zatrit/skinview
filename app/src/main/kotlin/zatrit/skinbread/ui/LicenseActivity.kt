package zatrit.skinbread.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.*
import zatrit.skinbread.*
import zatrit.skinbread.ui.dialog.guideDialog

/** An [Activity] that displays the licenses of libraries located in [ossLibraries]. */
class LicenseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Loads activity content and shows title bar
        setContentView(R.layout.activity_license)
        enableTitleBar()

        val list = requireViewById<ListView>(R.id.list_libraries)
        val adapter = ArrayAdapter<Library>(
          this, R.layout.entry_library, R.id.text_library_name
        )

        for (library in ossLibraries) {
            adapter.add(library)
        }

        list.setOnItemClickListener { _, _, position, _ ->
            // Opens a dialog with the text of the library license
            guideDialog(
              this, adapter.getItem(position - list.headerViewsCount)!!
            ).show()
        }

        list.adapter = adapter

        @SuppressLint("InflateParams")
        // Creates a header for sourcesList without a parent and adds it.
        val header = layoutInflater.inflate(R.layout.header_licenses, null)
        list.addHeaderView(header)
    }
}
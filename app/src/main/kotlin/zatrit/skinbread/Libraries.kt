package zatrit.skinbread

/** A representation of the library used in the project. */
class Library(
  val name: String, val author: String, val url: String,
  val license: Int = R.raw.apache_license) {
    override fun toString() = name
}

const val GITHUB = "https://github.com"

/** An array of [OSS](https://en.wikipedia.org/wiki/Open-source_software)
 * libraries used in the project. */
val ossLibraries = arrayOf(
  Library("SkinBread", "zatrit", "$GITHUB/zatrit/skinview"),
  Library("Material Icons", "Google", "$GITHUB/material-design-icons"),
  Library("Kotlin Standard Library", "JetBrains", "$GITHUB/JetBrains/kotlin"),
  Library("Guava", "Google", "$GITHUB/google/guava"),
)
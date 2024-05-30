package net.zatrit.skinbread

/** A representation of the library used in the project. */
class Library(
  val name: String, val author: String, val url: String,
  val license: Int = R.raw.apache_license) {
    override fun toString() = name
}

/** An array of [OSS](https://en.wikipedia.org/wiki/Open-source_software)
 * libraries used in the project. */
val ossLibraries = arrayOf(
  Library("SkinBread", "zatrit", "https://github.com/zatrit/skinview"),
  Library(
    "Material Icons", "Google", "https://github.com/google/material-design-icons"
  ),
  Library(
    "Kotlin Standard Library", "JetBrains", "https://github.com/JetBrains/kotlin"
  ),
  Library("Guava", "Google", "https://github.com/google/guava"),
)
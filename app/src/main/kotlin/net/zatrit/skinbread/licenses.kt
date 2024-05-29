package net.zatrit.skinbread

/** A representation of the library used in the project. */
class Library(
  val name: String, val url: String, val license: Int = R.raw.apache_license) {
    override fun toString() = name
}

/** An array of [OSS](https://en.wikipedia.org/wiki/Open-source_software)
 * libraries used in the project. */
val ossLibraries = arrayOf(
  Library("SkinBread", "https://github.com/zatrit/skinview"),
  Library("Material Icons", "https://github.com/google/material-design-icons"),
  Library("Kotlin Standard Library", "https://github.com/JetBrains/kotlin"),
  Library("Guava", "https://github.com/google/guava"),
)
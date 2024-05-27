package net.zatrit.skinbread

class Library(
    val name: String, val url: String, val license: Int = R.raw.apache_license)

val ossLibraries = arrayOf(
    Library("Material Icons", "https://github.com/google/material-design-icons"),
    Library("Kotlin Standard Library", "https://github.com/JetBrains/kotlin"),
    Library("Guava", "https://github.com/google/guava"),
)
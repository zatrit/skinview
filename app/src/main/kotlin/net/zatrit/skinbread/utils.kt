// Utility functions to use in a different parts of this project.
package net.zatrit.skinbread

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Switch
import net.zatrit.skins.lib.api.Layer
import java.util.UUID

/**
 * Statically converts the layout parameters for [View] and applies them after executing [func].
 * Simplifies the code for changing the layout.
 * */
inline fun <reified L : ViewGroup.LayoutParams> View.applyLayout(
  func: L.() -> Unit) {
    val params = layoutParams as L
    params.func()
    layoutParams = params
}

/** A pattern for parsing a short [UUID] notation that some developers use. */
private val uuidPattern = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toPattern()

/** Parses different [UUID] notations.
 *
 * @return [UUID] if successfully, null otherwise.
 * */
fun parseUuid(string: String): UUID? = try {
    UUID.fromString(string)
} catch (ex1: Exception) {
    try {
        val matcher = uuidPattern.matcher(string)
        if (matcher.matches()) UUID.fromString(
          matcher.replaceAll("$1-$2-$3-$4-$5")
        )
        else null
    } catch (ex2: Exception) {
        ex1.printDebug()
        ex2.printDebug()
        null
    }
}

/** Draws [View] to a new [Bitmap]. */
fun View.drawToBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

/** Prints stacktrace exceptions only in debug builds. */
@DebugOnly
fun Throwable.printDebug() = this.printStackTrace()

/** Short notation for binding [func] to [Switch]. */
inline fun Activity.bindSwitch(
  id: Int, value: Boolean, crossinline func: (Boolean) -> Unit) {
    requireViewById<Switch>(id).apply {
        setOnCheckedChangeListener { _, state -> func(state) }
        isChecked = value
    }
}

/** Short notation for binding [func] to [Button]. */
inline fun bindButton(button: Button, crossinline func: (View) -> Unit) =
    button.setOnClickListener { func(it) }

/** Short notation for binding [func] to [Button] by ID. */
inline fun Activity.bindButton(id: Int, crossinline func: (View) -> Unit) =
    bindButton(requireViewById(id), func)

/** Moves the [IntArray] element, shifting the other elements. */
fun IntArray.moveItemTo(from: Int, to: Int) {
    Log.d(TAG, "$from => $to")

    val a = get(from)

    if (from < to) {
        System.arraycopy(this, from + 1, this, from, to - from)
    } else if (from > to) {
        System.arraycopy(this, to, this, to + 1, from - to)
    }

    this[to] = a
}

/** Tries to apply the layer, otherwise prints an exception and returns the original [value]. */
fun <T> Layer<T>.tryApply(value: T): T = try {
    apply(value)
} catch (ex: Exception) {
    ex.printDebug()
    value
}

/** Simple wrapper around [SharedPreferences.edit]. */
inline fun SharedPreferences.edit(func: (SharedPreferences.Editor) -> Unit) {
    val edit = edit()
    func(edit)
    edit.apply()
}

/**
 * [String.replace] implementation based on the Java implementation,
 * since the Kotlin implementation uses [IntRange], which can cause the
 * .dex to increase by 1 KiB. */
@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun String.jvmReplace(from: String, to: String): String =
    (this as java.lang.String).replace(from, to)

/** Enables title bar for [Activity] that doesn't have it by default. */
@Suppress("DEPRECATION")
fun Activity.enableTitleBar() = window.run {
    clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    statusBarColor = resources.getColor(R.color.card_background, theme)
}
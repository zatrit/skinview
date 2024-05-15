package net.zatrit.skinbread

import android.util.Log
import android.view.*
import net.zatrit.skinbread.skins.SkinSource
import java.util.UUID
import java.util.concurrent.CompletableFuture


inline fun <reified L : ViewGroup.LayoutParams> View.applyLayout(
    func: L.() -> Unit) {
    val params = layoutParams as L
    params.func()
    layoutParams = params
}

@DebugOnly
fun Throwable.printWithSkinSource(source: SkinSource) {
    Log.e(TAG, "$source / $message")
    printStackTrace()
}

@DebugOnly
fun <T> CompletableFuture<T>.printErrorOnFail() {
    exceptionally {
        it.printStackTrace()
        null
    }
}

private val uuidPattern = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toPattern()

fun parseUuid(string: String): UUID? = try {
    UUID.fromString(string)
} catch (ex: Exception) {
    ex.printDebug()
    try {
        val matcher = uuidPattern.matcher(string)
        if (matcher.matches()) UUID.fromString(
            matcher.replaceAll("$1-$2-$3-$4-$5")
        )
        else null
    } catch (ex: Exception) {
        ex.printDebug()
        null
    }
}

@DebugOnly
fun Throwable.printDebug() = this.printStackTrace()
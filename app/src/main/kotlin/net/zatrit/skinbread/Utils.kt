package net.zatrit.skinbread

import android.util.Log
import android.view.*
import net.zatrit.skinbread.skins.SkinSource
import java.util.concurrent.CompletableFuture

inline fun <reified L : ViewGroup.LayoutParams> View.applyLayout(
    func: L.() -> Unit) {
    val params = layoutParams as L
    params.func()
    layoutParams = params
}

@DebugOnly
fun Throwable.printWithSkinSource(source: SkinSource) {
    Log.e(TAG, "${source.name}: ${javaClass.name} / $message")
    printStackTrace()
}

inline fun <T> supplyAsync(crossinline func: () -> T): CompletableFuture<T> =
    CompletableFuture.supplyAsync { func() }

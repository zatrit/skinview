package net.zatrit.skinview

import android.util.Log
import android.view.*
import net.zatrit.skinview.skins.SkinSource
import kotlin.reflect.KMutableProperty0

inline fun <reified L : ViewGroup.LayoutParams> View.applyLayout(
    func: L.() -> Unit) {
    val params = layoutParams as L
    params.func()
    layoutParams = params
}

inline fun <V> KMutableProperty0<V?>.takeAnd(func: (V) -> Unit) {
    val value = this.get()
    if (this.get() != null) {
        func(value!!)
        this.set(null)
    }
}

@DebugOnly
fun Exception.printWithSkinSource(source: SkinSource) =
    Log.e(TAG, "${source.name}: ${javaClass.name} / $message")
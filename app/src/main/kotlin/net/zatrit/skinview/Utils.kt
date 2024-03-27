package net.zatrit.skinview

import android.view.*
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
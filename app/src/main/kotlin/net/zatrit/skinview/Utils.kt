package net.zatrit.skinview

import android.view.View
import android.view.ViewGroup

inline fun <L : ViewGroup.LayoutParams> View.applyLayout(func: L.() -> Unit) {
    @Suppress("UNCHECKED_CAST") val params = layoutParams as L
    params.func()
    layoutParams = params
}
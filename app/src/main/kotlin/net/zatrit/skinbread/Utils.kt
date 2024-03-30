package net.zatrit.skinbread

import android.util.Log
import android.view.*
import net.zatrit.skinbread.skins.SkinSource

inline fun <reified L : ViewGroup.LayoutParams> View.applyLayout(
    func: L.() -> Unit) {
    val params = layoutParams as L
    params.func()
    layoutParams = params
}

@DebugOnly
fun Exception.printWithSkinSource(source: SkinSource) =
    Log.e(TAG, "${source.name}: ${javaClass.name} / $message")
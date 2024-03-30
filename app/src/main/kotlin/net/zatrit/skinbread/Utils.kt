package net.zatrit.skinbread

import android.opengl.Matrix
import android.opengl.Matrix.setRotateEulerM2
import android.os.Build
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

fun setRotateEulerM(rm: FloatArray, offset: Int, x: Float, y: Float, z: Float) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        setRotateEulerM2(rm, offset, x, y, z)
    } else {
        @Suppress("DEPRECATION") Matrix.setRotateEulerM(rm, offset, x, y, z)
    }
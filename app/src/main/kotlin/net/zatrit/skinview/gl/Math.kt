package net.zatrit.skinview.gl

inline fun mat4(func: (FloatArray) -> Unit): FloatArray {
    val mat = FloatArray(16)
    func(mat)
    return mat
}
package net.zatrit.skinview.gl

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun mat4(func: (FloatArray) -> Unit): FloatArray {
    contract { callsInPlace(func, InvocationKind.EXACTLY_ONCE) }

    val mat = FloatArray(16)
    func(mat)
    return mat
}
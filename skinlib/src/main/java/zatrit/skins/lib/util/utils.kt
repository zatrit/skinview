package zatrit.skins.lib.util

import java.io.Closeable
import kotlin.contracts.*

/** A lightweight implementation of [Closeable.use] that has
 * fewer dependencies on the standard Kotlin library. */
@OptIn(ExperimentalContracts::class)
@Suppress("ConvertTryFinallyToUseCall")
inline fun <T : Closeable, R> T.use2(func: (T) -> R): R {
    contract {
        callsInPlace(func, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        func(this)
    } finally {
        close()
    }
}
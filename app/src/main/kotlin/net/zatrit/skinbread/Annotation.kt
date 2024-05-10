package net.zatrit.skinbread

/** Marks the function as necessary for debugging only.
 * Check Proguard rules for more information about [DebugOnly] usage. */
annotation class DebugOnly

/** Marks the item as requiring an OpenGL context for use */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
annotation class GLContext
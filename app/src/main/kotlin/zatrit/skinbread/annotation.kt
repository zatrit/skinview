package zatrit.skinbread

/** Marks the function as necessary for debugging only.
 * Check ProGuard rules for more information about [DebugOnly] usage. */
annotation class DebugOnly

/** Marks the item as requiring
 * an [OpenGL context](https://www.khronos.org/opengl/wiki/OpenGL_Context) for use. */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
annotation class GLContext
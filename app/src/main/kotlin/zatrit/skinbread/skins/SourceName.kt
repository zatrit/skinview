package zatrit.skinbread.skins

import android.content.Context

/** A [SkinSource] name that can be translated depending on the context. */
interface SourceName {
    /** @return string representation of the source name. */
    fun getName(context: Context): String
}

/** An implementation of [SourceName] based on a string resource. */
class ResName(private val id: Int) : SourceName {
    override fun getName(context: Context) = context.getString(id)
}

/** A string-based implementation of [SourceName]. */
class ConstName(private val name: String) : SourceName {
    override fun getName(context: Context) = name
}
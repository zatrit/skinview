package zatrit.skinbread.skins

import android.content.Context

interface SourceName {
    fun getName(context: Context): String
}

class ResName(private val id: Int) : SourceName {
    override fun getName(context: Context) = context.getString(id)
}

class ConstName(private val name: String) : SourceName {
    override fun getName(context: Context) = name
}
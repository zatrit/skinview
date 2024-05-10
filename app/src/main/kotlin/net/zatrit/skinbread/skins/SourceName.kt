import android.content.Context
import net.zatrit.skinbread.DebugOnly

interface SourceName {
    fun getName(context: Context): String
}

class ResName(private val id: Int) : SourceName {
    override fun getName(context: Context) = context.getString(id)

    @DebugOnly
    override fun toString() = id.toString()
}

class ConstName(private val name: String) : SourceName {
    override fun getName(context: Context) = name

    @DebugOnly
    override fun toString() = name
}
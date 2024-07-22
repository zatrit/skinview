package zatrit.skinbread.skins

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.*
import zatrit.skinbread.*
import zatrit.skinbread.ui.ORDER

private const val ENABLED = "enabled"

/** A class that describes the [order] of the sources and which ones will be displayed. */
@Parcelize
class Arranging(
  private val size: Int,

  val enabled: BooleanArray = BooleanArray(size) { false }.apply {
      // Enable vanilla and local by default
      set(VANILLA, true)
      set(LOCAL, true)
  },

  /** An array of source indexes to use when sorting textures. */
  var order: IntArray = IntArray(size) { it },
) : Parcelable {
    fun loadJson(json: JSONObject) {
        val enabled = json.optJSONArray(ENABLED)?.takeIf { it.length() == size }
        val order = json.optJSONArray(ORDER)?.takeIf { it.length() == size }

        for (i in 0..<size) {
            this.enabled[i] = enabled?.getBoolean(i) ?: false
            this.order[i] = order?.getInt(i) ?: i
        }
    }

    fun saveJson(): String {
        val enabled = JSONArray()
        val order = JSONArray()

        for (i in 0..<size) {
            enabled.put(this.enabled[i])
            order.put(this.order[i])
        }

        val json = JSONObject()
        json.put(ENABLED, enabled)
        json.put(ORDER, order)

        return json.toString(0)
    }
}
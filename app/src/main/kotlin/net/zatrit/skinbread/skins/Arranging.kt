package net.zatrit.skinbread.skins

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.zatrit.skinbread.LOCAL
import net.zatrit.skinbread.VANILLA
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
class Arranging(
  private val size: Int,

  val enabled: BooleanArray = BooleanArray(size) { false }.also {
      // Enable vanilla and local by default
      it[VANILLA] = true
      it[LOCAL] = true
  },
  var order: IntArray = IntArray(size) { it },
) : Parcelable {
    fun loadJson(json: JSONObject) {
        val enabled =
            json.optJSONArray("enabled")?.takeIf { it.length() == size }
        val order = json.optJSONArray("order")?.takeIf { it.length() == size }

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
        json.put("enabled", enabled)
        json.put("order", order)

        return json.toString(0)
    }
}
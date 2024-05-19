package net.zatrit.skinbread.skins

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.*

@Parcelize
class Arranging(
    val size: Int,

    val enabled: BooleanArray = BooleanArray(size) { false }.also {
        it[0] = true // Enable
    },
    val order: IntArray = IntArray(size) { it },
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
        val json = JSONObject()
        val enabled = JSONArray()
        val order = JSONArray()

        for (i in 0..<size) {
            enabled.put(this.enabled[i])
            order.put(this.order[i])
        }

        json.put("enabled", enabled)
        json.put("order", order)

        return json.toString(0)
    }
}
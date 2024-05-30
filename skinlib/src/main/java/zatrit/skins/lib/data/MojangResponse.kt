package zatrit.skins.lib.data

import org.jetbrains.annotations.ApiStatus.Internal
import org.json.JSONObject

/** Mojang API response representation. */
@Internal
class MojangResponse(json: JSONObject) {
    var id: String = json.getString("id")
    var name: String = json.getString("name")
    val properties = ArrayList<MojangProperty>()

    init {
        val props = json.getJSONArray("properties")
        for (i in 0..<props.length()) {
            properties.add(MojangProperty(props.getJSONObject(i)))
        }
    }

    @Internal
    class MojangProperty(json: JSONObject) {
        val name: String = json.getString("name")
        val value: String = json.getString("value")
    }
}

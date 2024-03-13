package net.zatrit.skins.lib.data

import net.zatrit.skins.lib.util.LoadJson
import org.jetbrains.annotations.ApiStatus.Internal
import org.json.JSONObject

@Internal
class MojangResponse : LoadJson {
    lateinit var id: String
    lateinit var name: String
    val properties = ArrayList<MojangProperty>()

    override fun loadJson(json: JSONObject) {
        id = json.getString("id")
        name = json.getString("name")

        val props = json.getJSONArray("properties")
        for (i in 0..<props.length()) {
            properties.add(MojangProperty().apply {
                loadJson(props.getJSONObject(i))
            })
        }
    }

    class MojangProperty : LoadJson {
        lateinit var name: String
        lateinit var value: String

        override fun loadJson(json: JSONObject) {
            name = json.getString("name")
            value = json.getString("value")
        }
    }
}

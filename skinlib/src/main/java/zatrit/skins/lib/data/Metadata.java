package zatrit.skins.lib.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A class that describes texture parameters
 * such as the model and whether it is animated.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    private boolean animated = false;
    private @Nullable String model;

    public Metadata(@NotNull JSONObject json) throws JSONException {
        if (json.has("animated")) {
            animated = json.getBoolean("animated");
        }

        if (json.has("model")) {
            model = json.getString("model");
        }
    }
}

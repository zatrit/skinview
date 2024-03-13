package net.zatrit.skins.lib.data;

import net.zatrit.skins.lib.util.LoadJson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * A class that describes texture parameters
 * such as the model and whether it is animated.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Metadata implements LoadJson {
    private boolean animated = false;
    private @Nullable String model;

    @Override
    @SneakyThrows
    public void loadJson(@NotNull JSONObject json) {
        if (json.has("animated")) {
            animated = json.getBoolean("animated");
        }

        if (json.has("model")) {
            model = json.getString("model");
        }
    }
}

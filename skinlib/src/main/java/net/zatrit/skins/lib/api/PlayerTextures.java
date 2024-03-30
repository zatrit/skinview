package net.zatrit.skins.lib.api;

import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.data.TypedTexture;
import net.zatrit.skins.lib.layer.android.ImageLayer;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Player-specific textures container.
 */
public interface PlayerTextures {
    /**
     * @return true, if texture is present.
     */
    boolean hasTexture(TextureType type);

    /**
     * @return texture of specified type if
     * present (check via {@link #hasTexture}).
     */
    @Nullable TypedTexture getTexture(
        TextureType type, List<Layer<TypedTexture>> layers);
}

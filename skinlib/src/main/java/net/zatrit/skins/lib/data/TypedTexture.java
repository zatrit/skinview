package net.zatrit.skins.lib.data;

import net.zatrit.skins.lib.TextureType;
import net.zatrit.skins.lib.api.Texture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TypedTexture {
    private final Texture texture;
    private final TextureType type;
}

package net.zatrit.skins.lib;

import net.zatrit.skins.lib.api.Layer;
import net.zatrit.skins.lib.data.TypedTexture;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public final class Config {
    private @NotNull Collection<Layer<TypedTexture>> layers = Collections.emptyList();
    // Very cool Gson that parses TextureType ignoring case
    private @NotNull Executor executor = Executors.newCachedThreadPool();
}

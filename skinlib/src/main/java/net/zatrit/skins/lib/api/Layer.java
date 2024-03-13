package net.zatrit.skins.lib.api;

import java.util.function.UnaryOperator;

/**
 * A layer that modifies an object of type T
 * in some way is similar in implementation to {@link UnaryOperator}.
 */
public interface Layer<T> {
    T apply(T input);

    default Layer<T> andThen(Layer<T> layer) {
        return t -> layer.apply(apply(t));
    }
}

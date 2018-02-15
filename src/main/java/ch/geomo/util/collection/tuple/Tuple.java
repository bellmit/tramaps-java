/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.tuple;

import org.jetbrains.annotations.NotNull;

public interface Tuple<T, S> {

    /**
     * @return the first value
     */
    T getFirst();

    /**
     * Alias for {@link #getFirst()}.
     */
    default T first() {
        return getFirst();
    }

    /**
     * @return the second value
     */
    S getSecond();

    /**
     * Alias for {@link #getSecond()}.
     */
    default S second() {
        return getSecond();
    }

    /**
     * @return a new {@link ImmutableTuple} with given elements.
     */
    @NotNull
    static <T, S> Tuple<T, S> createTuple(T first, S second) {
        return new ImmutableTuple<>(first, second);
    }

    /**
     * @return a new {@link ImmutableTuple} with given elements.
     */
    @NotNull
    static <T, S> Tuple<T, S> createTuple(T first, S second, boolean mutable) {
        if (mutable) {
            return new MutableTuple<>(first, second);
        }
        return new ImmutableTuple<>(first, second);
    }

}

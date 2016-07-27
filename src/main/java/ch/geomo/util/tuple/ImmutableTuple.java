/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.util.tuple;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An immutable tuple implementation.
 */
public class ImmutableTuple<T> implements Tuple<T> {

    private T first;
    private T second;

    public ImmutableTuple(@Nullable T first, @Nullable T second) {
        this.first = first;
        this.second = second;
    }

    @Nullable
    public T getFirst() {
        return first;
    }

    @Nullable
    public T getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof ImmutableTuple)) {
            return false;
        }

        ImmutableTuple tuple = (ImmutableTuple) obj;
        return Objects.equals(getFirst(), tuple.getFirst()) && Objects.equals(getSecond(), tuple.getSecond())
                || Objects.equals(getSecond(), tuple.getFirst()) && Objects.equals(getFirst(), tuple.getSecond());

    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return first + "/" + second;
    }

}

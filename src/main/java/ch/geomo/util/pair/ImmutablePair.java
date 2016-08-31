/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.util.pair;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An immutable pair implementation.
 */
public class ImmutablePair<T> implements Pair<T> {

    private T first;
    private T second;

    public ImmutablePair(@Nullable T first, @Nullable T second) {
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

        if (obj == null || !(obj instanceof ImmutablePair)) {
            return false;
        }

        ImmutablePair tuple = (ImmutablePair) obj;
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

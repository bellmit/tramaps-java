/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.pair;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An implementation of a mutable pair.
 */
public class MutablePair<T> implements Pair<T> {

    private T first;
    private T second;

    public MutablePair() {
    }

    public MutablePair(@Nullable Pair<T> pair) {
        if (pair != null) {
            first = pair.getFirst();
            second = pair.getSecond();
        }
    }

    public MutablePair(@Nullable T first, @Nullable T second) {
        this.first = first;
        this.second = second;
    }

    @Nullable
    @Override
    public T getFirst() {
        return first;
    }

    @Nullable
    @Override
    public T getSecond() {
        return second;
    }

    /**
     * Sets/updates the first value.
     */
    public void setFirst(@Nullable T first) {
        this.first = first;
    }

    /**
     * Sets/updates the second value.
     */
    public void setSecond(@Nullable T second) {
        this.second = second;
    }

    /**
     * Sets/updates the value at given index.
     *
     * @throws IndexOutOfBoundsException if index is neither 1 or 0
     */
    public void set(int index, @Nullable T value) {
        if (index < 0 || index > 1) {
            throw new IndexOutOfBoundsException("An index > 1 or < 0 is not allowed.");
        }
        if (index == 0) {
            first = value;
        }
        else {
            second = value;
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof MutablePair)) {
            return false;
        }

        MutablePair tuple = (MutablePair) obj;
        return Objects.equals(getFirst(), tuple.getFirst()) && Objects.equals(getSecond(), tuple.getSecond())
                || Objects.equals(getSecond(), tuple.getFirst()) && Objects.equals(getFirst(), tuple.getSecond());

    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "MutablePair: [" + first + ", " + second + "]";
    }

    /**
     * Sets first and second value to null.
     */
    public void clear() {
        first = null;
        second = null;
    }

}

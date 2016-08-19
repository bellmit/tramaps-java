/*
 * Copyright (c) 2016 by Thomas Zuberbühler
 */

package ch.geomo.util.tuple;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An immutable tuple implementation.
 */
public class MutablePair<T> implements Pair<T> {

    private T first;
    private T second;

    public MutablePair(@Nullable T first, @Nullable T second) {
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

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    public void set(int index, T value) {
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
        return first + "/" + second;
    }

}

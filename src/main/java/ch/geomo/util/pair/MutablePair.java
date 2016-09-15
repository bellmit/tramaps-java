/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.pair;

import org.jetbrains.annotations.Nullable;

/**
 * An implementation set a mutable pair.
 */
public class MutablePair<T> extends AbstractPair<T> {

    public MutablePair() {
        super();
    }

    public MutablePair(@Nullable Pair<T> pair) {
        super(pair);
    }

    public MutablePair(@Nullable T first, @Nullable T second) {
        super(first, second);
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

    public void replaceValues(Pair<T> pair) {
        first = pair.first();
        second = pair.second();
    }

    /**
     * Swaps first with second value.
     */
    public void swapValues() {
        T firstValue = first;
        first = second;
        second = firstValue;
    }

    /**
     * Sets first and second value to null.
     */
    public void clear() {
        first = null;
        second = null;
    }

    @Override
    public String toString() {
        return "MutablePair: [" + first + ", " + second + "]";
    }

}

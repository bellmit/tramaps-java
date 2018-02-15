/*
 * Copyright (c) 2016-2018 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.pair;

import java.util.Objects;

public abstract class AbstractPair<T> implements Pair<T> {

    protected T first;
    protected T second;

    public AbstractPair() {
    }

    public AbstractPair(Pair<T> pair) {
        if (pair != null) {
            first = pair.getFirst();
            second = pair.getSecond();
        }
    }

    public AbstractPair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public T getFirst() {
        return first;
    }

    @Override
    public T getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractPair
                && ((Objects.equals(first, ((AbstractPair) obj).first) && Objects.equals(second, ((AbstractPair) obj).second))
                || (Objects.equals(first, ((AbstractPair) obj).second) && Objects.equals(second, ((AbstractPair) obj).first)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": {" + first + ", " + second + "}";
    }

}

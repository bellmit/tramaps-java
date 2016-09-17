/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.collection.tuple;

public class ImmutableTuple<T, S> implements Tuple<T, S> {

    private T firstValue;
    private S secondValue;

    public ImmutableTuple(T firstValue, S secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    @Override
    public T getFirst() {
        return firstValue;
    }

    @Override
    public S getSecond() {
        return secondValue;
    }

}
